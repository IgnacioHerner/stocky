package com.ignaherner.stocky.ui.navigation

object Routes {
    const val PRODUCTS = "products"
    const val NEW_SALE = "new_sale"
    const val SALES_HISTORY = "sales_history"


    // Route "pattern" (la que define el destino)
    const val SALE_DETAIL = "sale_detail/{saleId}"

    // Helper parra construir la ruta real al navegar
    fun saleDetail(saleId: Long): String = "sale_detail/$saleId"
}