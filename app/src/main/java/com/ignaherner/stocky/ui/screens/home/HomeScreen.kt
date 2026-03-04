package com.ignaherner.stocky.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.stocky.ui.utils.CurrencyFormatter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProductsClick: () -> Unit,
    onSalesClick: () -> Unit,
    onLowStockClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (title, costCard, saleCard, profitCard, topProductCard, lowStockCard, productsCard, salesCard) =
            createRefs()

        Text(
            text = "Stocky",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        MetricCard(
            title = "Inventario (costo)",
            value = CurrencyFormatter.formatARS(state.totalInventoryCost),
            icon = Icons.Outlined.AccountBalanceWallet,
            modifier = Modifier.constrainAs(costCard) {
                top.linkTo(title.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(saleCard.start)
                width = Dimension.fillToConstraints
            }
        )

        MetricCard(
            title = "Inventario (venta)",
            value = CurrencyFormatter.formatARS(state.totalInventorySaleValue),
            icon = Icons.Outlined.AttachMoney,
            modifier = Modifier.constrainAs(saleCard) {
                top.linkTo(title.bottom, margin = 16.dp)
                start.linkTo(costCard.end, margin = 12.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        MetricCard(
            title = "Ganancia (histórica)",
            value = CurrencyFormatter.formatARS(state.totalProfitAllTime),
            icon = Icons.Outlined.TrendingUp,
            modifier = Modifier.constrainAs(profitCard) {
                top.linkTo(costCard.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        val afterProfitAnchor = if (state.topProductName != null) topProductCard else profitCard

        if (state.topProductName != null) {
            MetricCard(
                title = "Producto más vendido",
                value = "${state.topProductName} (${state.topProductUnits} vendidos)",
                modifier = Modifier.constrainAs(topProductCard) {
                    top.linkTo(profitCard.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )
        }

        // Anchor final: si hay low stock usamos lowStockCard, si no usamos afterProfitAnchor
        val mainActionsAnchor = if (state.lowStockCount > 0) lowStockCard else afterProfitAnchor

        if (state.lowStockCount > 0) {
            ActionCard(
                title = "Stock bajo",
                subtitle = "${state.lowStockCount} productos para reponer",
                icon = Icons.Outlined.Warning,
                modifier = Modifier
                    .constrainAs(lowStockCard) {
                        top.linkTo(afterProfitAnchor.bottom, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .clickable { onLowStockClick() }
            )
        }

        ActionCard(
            title = "Productos",
            subtitle = "Gestionar inventario",
            icon = Icons.Outlined.Inventory2,
            modifier = Modifier
                .constrainAs(productsCard) {
                    top.linkTo(mainActionsAnchor.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(salesCard.start, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }
                .clickable { onProductsClick() }
        )

        ActionCard(
            title = "Ventas",
            subtitle = "Historial y detalles",
            icon = Icons.Outlined.ReceiptLong,
            modifier = Modifier
                .constrainAs(salesCard) {
                    top.linkTo(mainActionsAnchor.bottom, margin = 20.dp)
                    start.linkTo(productsCard.end, margin = 12.dp)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .clickable { onSalesClick() }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
            }

            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
