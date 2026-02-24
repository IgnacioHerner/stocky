package com.ignaherner.stocky.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ignaherner.stocky.ui.screens.home.HomeScreen
import com.ignaherner.stocky.ui.screens.home.HomeViewModel
import com.ignaherner.stocky.ui.screens.products.ProductsScreen
import com.ignaherner.stocky.ui.screens.products.ProductsViewModel
import com.ignaherner.stocky.ui.screens.sales.NewSaleScreen
import com.ignaherner.stocky.ui.screens.sales.NewSaleViewModel
import com.ignaherner.stocky.ui.screens.sales.sale_detail.SaleDetailScreen
import com.ignaherner.stocky.ui.screens.sales.sale_detail.SaleDetailViewModel
import com.ignaherner.stocky.ui.screens.sales.sales_history.SalesHistoryScreen
import com.ignaherner.stocky.ui.screens.sales.sales_history.SalesHistoryViewModel

@Composable
fun StockyNavGraph(
    navController: NavHostController,
    productsViewModelProvider: @Composable () -> ProductsViewModel,
    newSaleViewModelProvider: @Composable () -> NewSaleViewModel,
    salesHistoryModelProvider: @Composable () -> SalesHistoryViewModel,
    saleDetailFactoryProvider: (Long) -> ViewModelProvider.Factory,
    homeViewModelProvider: @Composable () -> HomeViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable (Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModelProvider(),
                onProductsClick = {navController.navigate(Routes.PRODUCTS)},
                onSalesClick = {navController.navigate(Routes.SALES_HISTORY)}
            )
        }


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
                onBack = { navController.popBackStack() },
                onSaleClick = { saleId ->
                    navController.navigate(Routes.saleDetail(saleId))
                }
            )
        }

        composable(Routes.SALE_DETAIL) { backStackEntry ->
            val saleId = backStackEntry.arguments
                ?.getString("saleId")
                ?.toLongOrNull() ?: return@composable

            val factory = saleDetailFactoryProvider(saleId)

            val viewModel = ViewModelProvider(backStackEntry, factory)[SaleDetailViewModel::class.java]

            SaleDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}