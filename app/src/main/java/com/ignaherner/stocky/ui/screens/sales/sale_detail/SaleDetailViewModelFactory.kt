package com.ignaherner.stocky.ui.screens.sales.sale_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ignaherner.stocky.data.repository.ProductRepository
import com.ignaherner.stocky.data.repository.SalesRepository

class SaleDetailViewModelFactory(
    private val saleId: Long,
    private val salesRepository: SalesRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaleDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SaleDetailViewModel(saleId, salesRepository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}