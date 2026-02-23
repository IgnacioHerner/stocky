package com.ignaherner.stocky.ui.screens.sales.sale_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ignaherner.stocky.data.repository.SalesRepository

class SaleDetailViewModelFactory(
    private val saleId: Long,
    private val salesRepository: SalesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaleDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SaleDetailViewModel(saleId, salesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}