package com.ignaherner.stocky.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.local.entity.ProductEntity
import com.ignaherner.stocky.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val showOnlyLowStockFlow = MutableStateFlow(false)

    val uiState: StateFlow<ProductsUiState> =
        combine(
            repository.observeProducts(),
            repository.observeTotalCost(),
            repository.observeTotalSaleValue(),
            showOnlyLowStockFlow
        ) { products, totalCost, totalSale, showOnlyLowStock ->
            ProductsUiState(
                products = products,
                lowStockProducts = products.filter {
                    it.currentStock <= it.minimumStock
                },
                totalCost = totalCost,
                totalSaleValue = totalSale,
                showOnlyLowStock = showOnlyLowStock
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProductsUiState()
        )

    fun setShowOnlyLowStock(enabled: Boolean) {
        showOnlyLowStockFlow.value = enabled
    }

    fun toggleLowStockFilter() {
        showOnlyLowStockFlow.value = !showOnlyLowStockFlow.value
    }

    fun insert(product: ProductEntity) {
        viewModelScope.launch {
            repository.insert(product)
        }
    }

    fun update(product: ProductEntity) {
        viewModelScope.launch {
            repository.update(product)
        }
    }

    fun delete(product: ProductEntity) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
}