package com.ignaherner.stocky.ui.screens.sales.sale_detail

data class SaleItemDetailUi(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val unitCost: Double
) {
    val subtotal: Double
        get() = quantity * unitPrice
    val profit: Double
        get() = (unitPrice - unitCost) * quantity
}
