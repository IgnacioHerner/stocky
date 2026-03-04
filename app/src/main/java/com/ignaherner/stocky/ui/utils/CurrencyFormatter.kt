package com.ignaherner.stocky.ui.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val localeAR = Locale("es", "AR")

    private val currencyFormatter: NumberFormat =
        NumberFormat.getCurrencyInstance(localeAR).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
    fun formatARS(value: Double): String {
        return currencyFormatter.format(value)
    }
}