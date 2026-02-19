package com.ignaherner.stocky.ui.screens.sales.sales_history

data class SaleSummaryUi(
    val id: Long,
    val date: Long,
    val total: Double,
    val itemsCount: Int,
    val productsCount: Int
)