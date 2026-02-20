package com.ignaherner.stocky.ui.screens.sales.sales_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ignaherner.stocky.data.repository.SalesRepository

class SalesHistoryViewModelFactory(
    private val salesRepository: SalesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesHistoryViewModel(salesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}