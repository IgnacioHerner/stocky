package com.ignaherner.stocky.ui.screens.sales.sale_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SaleDetailViewModel(
    private val saleId: Long,
    private val salesRepository: SalesRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(SaleDetailUiState())
    val uiState: StateFlow<SaleDetailUiState> = _uiState

    init {
        viewModelScope.launch {
            salesRepository.observeSaleWithItems(saleId).collectLatest { saleWithItems ->
                _uiState.update {
                    it.copy(
                        sale = saleWithItems?.sale,
                        items = saleWithItems?.items ?: emptyList()
                    )
                }
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