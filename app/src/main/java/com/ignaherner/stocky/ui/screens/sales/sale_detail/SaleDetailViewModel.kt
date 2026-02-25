package com.ignaherner.stocky.ui.screens.sales.sale_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.repository.ProductRepository
import com.ignaherner.stocky.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SaleDetailViewModel(
    private val saleId: Long,
    private val salesRepository: SalesRepository,
    private val productRepository: ProductRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(SaleDetailUiState())
    val uiState: StateFlow<SaleDetailUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                salesRepository.observeSaleWithItems(saleId),
                productRepository.observeProducts()
            ) { saleWithItems, products ->

                val nameById = products.associateBy({ it.id }, { it.name })
                val costById = products.associateBy({it.id}, {it.cost})

                val itemsUi = (saleWithItems?.items ?: emptyList()).map { item ->
                    SaleItemDetailUi(
                        productId = item.productId,
                        productName = nameById[item.productId] ?: "Producto eliminado",
                        quantity = item.quantity,
                        unitPrice = item.unitPrice,
                        unitCost = costById[item.productId] ?: 0.0
                    )
                }

                val totalProfit = itemsUi.sumOf { it.profit }

                SaleDetailUiState(
                    sale = saleWithItems?.sale,
                    itemsUi = itemsUi,
                    totalProfit = totalProfit
                )
            }.collectLatest { newState ->
                _uiState.value = newState
            }
        }
    }


    fun clearMessage() {
        _uiState.update { it.copy(message  = null) }
    }

    fun deleteSale() {
        _uiState.update { it.copy(isDeleting = true, message = null) }

        viewModelScope.launch {
            try {
                salesRepository.deleteSaleAndRestoreStock(saleId)
                _uiState.update { it.copy(isDeleting = false, message = "Venta eliminada ✅") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isDeleting = false, message = "Error al eliminar venta") }
            }
        }
    }
}