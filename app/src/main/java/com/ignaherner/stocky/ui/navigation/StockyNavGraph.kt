package com.ignaherner.stocky.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ignaherner.stocky.StockyApp
import com.ignaherner.stocky.ui.screens.home.HomeScreen
import com.ignaherner.stocky.ui.screens.home.HomeViewModel
import com.ignaherner.stocky.ui.screens.products.ProductsScreen
import com.ignaherner.stocky.ui.screens.products.ProductsViewModel
import com.ignaherner.stocky.ui.screens.sales.new_sale.NewSaleScreen
import com.ignaherner.stocky.ui.screens.sales.new_sale.NewSaleViewModel
import com.ignaherner.stocky.ui.screens.sales.new_sale.NewSaleViewModelFactory
import com.ignaherner.stocky.ui.screens.sales.sale_detail.SaleDetailScreen
import com.ignaherner.stocky.ui.screens.sales.sale_detail.SaleDetailViewModel
import com.ignaherner.stocky.ui.screens.sales.sales_history.SalesHistoryScreen
import com.ignaherner.stocky.ui.screens.sales.sales_history.SalesHistoryViewModel

@Composable
fun StockyNavGraph(
    navController: NavHostController,
    productsViewModelProvider: @Composable () -> ProductsViewModel,
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

        composable(Routes.NEW_SALE) { backStackEntry ->
            val app = (LocalContext.current.applicationContext as StockyApp)

            val factory = NewSaleViewModelFactory(
                salesRepository = app.container.salesRepository,
                productRepository = app.container.productRepository
            )

            val viewModel: NewSaleViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = factory
            )

            NewSaleScreen(
                viewModel = viewModel,
                onBack = {navController.popBackStack()},
                onNavigateToHistory = {
                    navController.navigate(Routes.SALES_HISTORY) {
                        popUpTo(Routes.NEW_SALE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
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