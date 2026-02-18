package com.ignaherner.stocky.ui.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ignaherner.stocky.data.repository.ProductRepository
import com.ignaherner.stocky.data.repository.SalesRepository

class NewSaleViewModelFactory(
    private val productRepository: ProductRepository,
    private val salesRepository: SalesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewSaleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewSaleViewModel(productRepository, salesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}