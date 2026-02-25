package com.ignaherner.stocky.ui.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.local.entity.ProductEntity
import com.ignaherner.stocky.data.repository.InsufficientStockException
import com.ignaherner.stocky.data.repository.models.NewSaleItem
import com.ignaherner.stocky.data.repository.ProductRepository
import com.ignaherner.stocky.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewSaleViewModel(
    private val productRepository: ProductRepository,
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewSaleUiState())
    val uiState: StateFlow<NewSaleUiState> = _uiState

    init {
        // Observamos productos para dropdown
        viewModelScope.launch {
            productRepository.observeProducts().collect { products ->
                _uiState.update { old ->
                    val currentSelect = old.selectedProduct
                    val selectedStillExist = currentSelect?.let { sel ->
                        products.any { it.id == sel.id }
                    } ?: false

                    old.copy(
                        products = products,
                        selectedProduct = if(selectedStillExist) currentSelect else products.firstOrNull()
                    )
                }
            }
        }
    }

    fun onSelectProduct(product: ProductEntity) {
        _uiState.update { it.copy(selectedProduct = product) }
    }

    fun onQuantityChange(text: String) {
        _uiState.update { it.copy(quantityText = text) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun addToCart() {
        val state = _uiState.value
        val product = state.selectedProduct ?: run {
            _uiState.update { it.copy(message = "Seleccioná un producto.") }
            return
        }

        val quantity = state.quantityText.toIntOrNull()
        if (quantity == null || quantity <= 0) {
            _uiState.update { it.copy(message = "Cantidad inválida.") }
            return
        }
        val qty = state.quantityText.toIntOrNull()?.takeIf { it > 0 }
            ?: run {
                _uiState.update { it.copy(message = "Cantidad inválida.") }
                return
            }

        _uiState.update { old ->
            val existing = old.cart.firstOrNull { it.productId == product.id }

            val newCart = if (existing == null) {
                old.cart + CartItemUi(
                    productId = product.id,
                    name = product.name,
                    unitPrice = product.salePrice,
                    quantity = quantity
                )
            } else {
                old.cart.map {
                    if (it.productId == product.id) it.copy(quantity = it.quantity + qty) else it                }
            }

            old.copy(
                cart = newCart,
                quantityText = "",
                message = null
            )
        }
    }

    fun removeFromCart(productId: Long) {
        _uiState.update { it.copy(cart = it.cart.filterNot { item -> item.productId == productId }) }
    }

    fun registerSale() {
        val state = _uiState.value
        if (state.cart.isEmpty()) {
            _uiState.update { it.copy(message = "Agregá al menos un producto al carrito.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, message = null) }

        viewModelScope.launch {
            try {
                val items = state.cart.map { item ->
                    NewSaleItem(
                        productId = item.productId,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice
                    )
                }

                salesRepository.registerSale(
                    date = System.currentTimeMillis(),
                    items = items
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        cart = emptyList(),
                        message = "Venta registrada ✅",
                        shouldNavigateToHistory = true
                    )
                }
            } catch (e: InsufficientStockException) {
                _uiState.update { it.copy(isSaving = false, message = e.message ?: "Stock insuficiente.", shouldNavigateToHistory = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, message = "Error al guardar venta.", shouldNavigateToHistory = false) }
            }
        }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun consumeNavigation() {
        _uiState.update { it.copy(shouldNavigateToHistory = false) }
    }
}