package com.ignaherner.stocky.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.stocky.data.local.entity.ProductEntity

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showFormDialog by rememberSaveable { mutableStateOf(false)}
    var editingProduct by rememberSaveable { mutableStateOf<ProductEntity?>(null)}

    var showOnlyLowStock by rememberSaveable { mutableStateOf(false)}

    val displayProducts = if(showOnlyLowStock){
        state.lowStockProducts
    } else {
        state.products
    }

    if (showFormDialog) {
        ProductFormDialog(
            initialProduct = editingProduct,
            onDismiss = {
                showFormDialog = false
                editingProduct = null
            },
            onSave = {name, cost, salePrice, currentStock, minimumStock, category ->
                val product = if (editingProduct == null) {
                    ProductEntity(
                        name = name,
                        cost = cost,
                        salePrice = salePrice,
                        currentStock = currentStock,
                        minimumStock = minimumStock,
                        category = category
                    )
                } else {
                    editingProduct!!.copy(
                        name = name,
                        cost = cost,
                        salePrice = salePrice,
                        currentStock = currentStock,
                        minimumStock = minimumStock,
                        category = category
                    )
                }
                if (editingProduct == null) viewModel.insert(product) else viewModel.update(product)

                showFormDialog = false
                editingProduct = null
            }
        )
    }


    ProductsContent(
        products = displayProducts,
        totalCost = state.totalCost,
        totalSaleValue = state.totalSaleValue,
        showOnlyLowStock = showOnlyLowStock,
        onToggleFilter = { showOnlyLowStock = !showOnlyLowStock },
        onAddClick = {
            editingProduct = null
            showFormDialog = true
        },
        onEdit = { product ->
            editingProduct = product
            showFormDialog = true
        },
        onDelete = { product ->
            viewModel.delete(product)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsContent(
    products: List<ProductEntity>,
    totalCost: Double,
    totalSaleValue: Double,
    showOnlyLowStock: Boolean,
    onToggleFilter: () -> Unit,
    onAddClick: () -> Unit,
    onEdit: (ProductEntity) -> Unit,
    onDelete: (ProductEntity) -> Unit
)   {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stocky - Products") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) {padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            MetricsCard(
                totalCost = totalCost,
                totalSaleValue = totalSaleValue
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Productos", style = MaterialTheme.typography.titleMedium)

                TextButton(onClick = onToggleFilter) {
                    Text(if (showOnlyLowStock) "Mostrar todos" else "Solo stock bajo")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Productos")

                TextButton(onClick = onToggleFilter) {
                    Text(
                        if (showOnlyLowStock) "Mostrar todos"
                        else "Solo stock bajo"
                    )
                }
            }

            if(products.isEmpty()) {
                Text("No hay productos todavia")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        ProductRow(
                            product = product,
                            onEdit = { onEdit(product)},
                            onDelete = { onDelete(product) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MetricsCard(
    totalCost: Double,
    totalSaleValue: Double
) {
    Card(modifier = Modifier.fillMaxWidth()){
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Metricas", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total a costo: $totalCost ARS")
            Text("Total a venta: $totalSaleValue ARS")
        }
    }
}

@Composable
fun ProductRow(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = product.currentStock <= product.minimumStock

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Stock: ${product.currentStock} | Min: ${product.minimumStock}",
                    color = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text("Costo: ${product.cost} | Venta: ${product.salePrice}")
                Text("Cat: ${product.category}")
            }
            Column {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar")}
            }
        }
    }
}