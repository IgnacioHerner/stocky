package com.ignaherner.stocky.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProductsClick: () -> Unit,
    onSalesClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (title, costCard, saleCard, profitCard, productsCard, salesCard, lowStockText) = createRefs()

        Text(
            text = "Stocky",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        // Métrica 1: Inventario a costo (arriba izquierda)
        MetricCard(
            title = "Inventario (costo)",
            value = formatArs(state.totalInventoryCost),
            modifier = Modifier.constrainAs(costCard) {
                top.linkTo(title.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(saleCard.start, margin = 12.dp)
                width = androidx.constraintlayout.compose.Dimension.fillToConstraints
            }
        )

        // Métrica 2: Inventario a venta (arriba derecha)
        MetricCard(
            title = "Inventario (venta)",
            value = formatArs(state.totalInventorySaleValue),
            modifier = Modifier.constrainAs(saleCard) {
                top.linkTo(title.bottom, margin = 16.dp)
                start.linkTo(costCard.end, margin = 12.dp)
                end.linkTo(parent.end)
                width = androidx.constraintlayout.compose.Dimension.fillToConstraints
            }
        )

        // Métrica 3: Ganancia (abajo, ocupa ancho completo)
        MetricCard(
            title = "Ganancia (histórica)",
            value = formatArs(state.totalProfitAllTime),
            modifier = Modifier.constrainAs(profitCard) {
                top.linkTo(costCard.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = androidx.constraintlayout.compose.Dimension.fillToConstraints
            }
        )

        Text(
            text = "Stock bajo: ${state.lowStockCount}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(lowStockText) {
                top.linkTo(profitCard.bottom, margin = 12.dp)
                start.linkTo(parent.start)
            }
        )

        // Acceso: Productos
        ActionCard(
            title = "Productos",
            subtitle = "Gestionar inventario",
            modifier = Modifier
                .constrainAs(productsCard) {
                    top.linkTo(lowStockText.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(salesCard.start, margin = 12.dp)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
                .clickable { onProductsClick() }
        )

        // Acceso: Ventas
        ActionCard(
            title = "Ventas",
            subtitle = "Historial y detalles",
            modifier = Modifier
                .constrainAs(salesCard) {
                    top.linkTo(lowStockText.bottom, margin = 20.dp)
                    start.linkTo(productsCard.end, margin = 12.dp)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
                .clickable { onSalesClick() }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        androidx.compose.foundation.layout.Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatArs(value: Double): String {
    return "ARS ${"%,.2f".format(value)}"
}