package com.ignaherner.stocky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.ignaherner.stocky.ui.screens.products.ProductsScreen
import com.ignaherner.stocky.ui.screens.products.ProductsViewModel
import com.ignaherner.stocky.ui.screens.products.ProductsViewModelFactory
import com.ignaherner.stocky.ui.screens.sales.NewSaleScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?, ) {
        super.onCreate(savedInstanceState)

        val app = application as StockyApp
        val factory = ProductsViewModelFactory(app.container.productRepository)
        val viewModel = ViewModelProvider(this, factory)[ProductsViewModel::class.java]

        setContent {
            ProductsScreen(viewModel = viewModel)
        }

    }
}