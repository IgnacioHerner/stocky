package com.ignaherner.stocky.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.local.entity.ProductEntity
import com.ignaherner.stocky.data.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<ProductEntity> = emptyList(),
    val totalCost: Double = 0.0,
    val totalSaleValue: Double = 0.0
)

class ProductsViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    val uiState: StateFlow<ProductsUiState> =
        combine(
            repository.observeProducts(),
            repository.observeTotalCost(),
            repository.observeTotalSaleValue()
        ) { products, totalCost, totalSale ->
            ProductsUiState(
                products = products,
                totalCost = totalCost,
                totalSaleValue = totalSale
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProductsUiState()
        )

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