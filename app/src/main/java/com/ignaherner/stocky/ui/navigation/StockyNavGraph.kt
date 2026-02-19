package com.ignaherner.stocky.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ignaherner.stocky.ui.screens.products.ProductsScreen
import com.ignaherner.stocky.ui.screens.sales.NewSaleScreen
import com.ignaherner.stocky.ui.screens.sales.sales_history.SalesHistoryScreen

@Composable
fun StockyNavGraph(
    navController: NavHostController,
    productsViewModelProvider: @Composable () -> com.ignaherner.stocky.ui.screens.products.ProductsViewModel,
    newSaleViewModelProvider: @Composable () -> com.ignaherner.stocky.ui.screens.sales.NewSaleViewModel,
    salesHistoryModelProvider: @Composable () -> com.ignaherner.stocky.ui.screens.sales.sales_history.SalesHistoryViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.PRODUCTS,
        modifier = modifier
    ) {
        composable(Routes.PRODUCTS) {
            ProductsScreen(
                viewModel = productsViewModelProvider(),
                onNewSaleClick = { navController.navigate(Routes.NEW_SALE)},
                onSalesHistoryClick = { navController.navigate(Routes.SALES_HISTORY)}
            )
        }

        composable(Routes.NEW_SALE) {
            NewSaleScreen(
                viewModel = newSaleViewModelProvider(),
                onBack = { navController.popBackStack()}
            )
        }

        composable (Routes.SALES_HISTORY){
            SalesHistoryScreen(
                viewModel = salesHistoryModelProvider(),
                onBack = { navController.popBackStack()}
            )
        }
    }
}