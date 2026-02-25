package com.ignaherner.stocky.ui.screens.home

data class HomeUiState(
    val totalInventoryCost: Double = 0.0, // cuánto vale tu inventario si lo valuás al costo
    val totalInventorySaleValue: Double = 0.0, // cuánto “podrías facturar” si vendés to_do al precio de venta
    val totalProfitAllTime: Double = 0.0, // ganancia histórica (todas las ventas)
    val lowStockCount: Int = 0 // cantidad de productos con stock <= mínimo
)
