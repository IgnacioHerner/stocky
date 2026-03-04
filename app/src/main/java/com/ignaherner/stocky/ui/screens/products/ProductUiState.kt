package com.ignaherner.stocky.ui.screens.products

import com.ignaherner.stocky.data.local.entity.ProductEntity

data class ProductsUiState(
    val products: List<ProductEntity> = emptyList(),
    val lowStockProducts: List<ProductEntity> = emptyList(),
    val totalCost: Double = 0.0,
    val totalSaleValue: Double = 0.0,
    val showOnlyLowStock: Boolean = false,
    val sort: ProductSort = ProductSort.NAME
)
