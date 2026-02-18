package com.ignaherner.stocky.ui.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.local.entity.ProductEntity
import com.ignaherner.stocky.data.repository.InsufficientStockException
import com.ignaherner.stocky.data.repository.NewSaleItem
import com.ignaherner.stocky.data.repository.ProductRepository
import com.ignaherner.stocky.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

data class NewSaleUiState(
    val products: List<ProductEntity> = emptyList(),
    val selectedProduct: ProductEntity? = null,
    val quantityText: String = "",
    val isSaving: Boolean = false,
    val message: String? = null
)

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

    fun saveSale() {
        val state = _uiState.value
        val product = state.selectedProduct ?: run {
            _uiState.update { it.copy(message = "Selecciona un producto") }
            return
        }

        val quantity = state.quantityText.toIntOrNull()
        if(quantity == null || quantity <= 0) {
            _uiState.update { it.copy(message = "Cantidad inválida.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, message = null) }

        viewModelScope.launch {
            try {
                salesRepository.registerSale(
                    date = System.currentTimeMillis(),
                    items = listOf(
                        NewSaleItem(
                            productId = product.id,
                            quantity = quantity,
                            unitPrice = product.salePrice
                        )
                    )
                )
                _uiState.update { it.copy(isSaving = false, quantityText = "", message = "Venta registrada ✅") }
            } catch (e: InsufficientStockException){
                _uiState.update { it.copy(isSaving = false, message = e.message ?: "Stock insuficiente.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, message = "Error al guardar venta") }
            }
        }
    }
}