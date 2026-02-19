package com.ignaherner.stocky.ui.screens.sales

data class CartItemUi(
    val productId: Long,
    val name: String,
    val unitPrice: Double,
    val quantity: Int
){
    val lineTotal: Double get() = unitPrice * quantity
}
