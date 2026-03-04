package com.ignaherner.stocky.ui.screens.products

sealed class ProductsUiEvent {
    data class ShowSnackbar(val message: String) : ProductsUiEvent()
}