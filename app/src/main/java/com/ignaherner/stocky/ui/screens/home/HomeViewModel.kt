package com.ignaherner.stocky.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.repository.ProductRepository
import com.ignaherner.stocky.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val salesRepository: SalesRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                productRepository.observeProducts(), // Flow<List<ProductEntity>>
                salesRepository.observeSalesWithItems() // Flow<List<SaleWithItems>>
            ) { products, salesWithItems ->

                // 1) Inventario a costo
                val totalCost = products.sumOf { product ->
                    product.cost * product.currentStock
                }

                // 2) Inventario a venta
                val totalSaleValue = products.sumOf { product ->
                    product.salePrice * product.currentStock
                }

                // 3) Stock bajo
                val lowStockCount = products.count { product ->
                    product.cost <= product.minimumStock
                }

                // 4) Ganancia historica
                // Necesitamos mappear productId -> cost actual
                val costById = products.associateBy ({it.id}, {it.cost} )

                val totalProfitAllTime = salesWithItems.sumOf { saleWithItems ->
                    saleWithItems.items.sumOf { item ->
                        val unitCost = costById[item.productId] ?: 0.0
                        (item.unitPrice - unitCost) * item.quantity
                    }
                }

                HomeUiState(
                    totalInventoryCost = totalCost,
                    totalInventorySaleValue = totalSaleValue,
                    totalProfitAllTime = totalProfitAllTime,
                    lowStockCount = lowStockCount
                )

            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}