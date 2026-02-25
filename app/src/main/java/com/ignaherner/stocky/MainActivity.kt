package com.ignaherner.stocky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.ignaherner.stocky.ui.navigation.StockyNavGraph
import com.ignaherner.stocky.ui.screens.home.HomeViewModel
import com.ignaherner.stocky.ui.screens.home.HomeViewModelFactory
import com.ignaherner.stocky.ui.screens.products.ProductsViewModel
import com.ignaherner.stocky.ui.screens.products.ProductsViewModelFactory
import com.ignaherner.stocky.ui.screens.sales.new_sale.NewSaleViewModelFactory
import com.ignaherner.stocky.ui.screens.sales.sale_detail.SaleDetailViewModelFactory
import com.ignaherner.stocky.ui.screens.sales.sales_history.SalesHistoryViewModel
import com.ignaherner.stocky.ui.screens.sales.sales_history.SalesHistoryViewModelFactory
import com.ignaherner.stocky.ui.theme.StockyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?, ) {
        super.onCreate(savedInstanceState)

        // 1) Accedemos al "container" (tu DI manual) desde la Application
        val app = application as StockyApp
        val container = app.container

        // 2) Creamos la factories de cada ViewModel(una por pantalla)
        val productsFactory  = ProductsViewModelFactory(app.container.productRepository)

        val newSaleFactory = NewSaleViewModelFactory(
            productRepository = container.productRepository,
            salesRepository = container.salesRepository
        )

        val homeFactory = HomeViewModelFactory(
            productRepository = app.container.productRepository,
            salesRepository = app.container.salesRepository
        )

        val salesHistoryFactory = SalesHistoryViewModelFactory(container.salesRepository)

        // 3) Creamos la instancias de ViewModel con ViewModelProvider
        // Importante: Esto lo hacemos UNA vez aca, y luego se lo damos al NavGraph

        val productsViewModel: ProductsViewModel =
            ViewModelProvider(this, productsFactory)[ProductsViewModel::class.java]


        val salesHistoryViewModel =
            ViewModelProvider(this, salesHistoryFactory)[SalesHistoryViewModel::class.java]

        val saleDetailFactoryProvider: (Long) -> ViewModelProvider.Factory = { saleId ->
            SaleDetailViewModelFactory(
                saleId, container.salesRepository, container.productRepository)
        }

        val homeViewModel: HomeViewModel =
            ViewModelProvider(this, homeFactory)[HomeViewModel::class.java]

        // 4) Seteamos Compose + NavController + NavGraph
        setContent {
            StockyTheme {
                val navController = rememberNavController()

                // Providers: son lambas que devuelven el ViewModel
                // Esto evita que el NavGraph conozca tu container o ViewModelProvider

                StockyNavGraph(
                    navController = navController,
                    productsViewModelProvider = {productsViewModel},
                    salesHistoryModelProvider = {salesHistoryViewModel},
                    saleDetailFactoryProvider = saleDetailFactoryProvider,
                    homeViewModelProvider = {homeViewModel}
                )
            }
        }

    }
}