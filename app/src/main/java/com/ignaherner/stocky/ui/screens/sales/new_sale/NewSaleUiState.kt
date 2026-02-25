package com.ignaherner.stocky.ui.screens.sales.new_sale

import com.ignaherner.stocky.data.local.entity.ProductEntity

data class NewSaleUiState(
    val products: List<ProductEntity> = emptyList(),
    val selectedProduct: ProductEntity? = null,
    val quantityText: String = "",
    val cart: List<CartItemUi> = emptyList(),
    val isSaving: Boolean = false,
    val message: String? = null,
    val shouldNavigateToHistory: Boolean = false
) {
    val total: Double = cart.sumOf { it.lineTotal }
}