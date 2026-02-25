package com.ignaherner.stocky.ui.screens.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.stocky.data.local.entity.ProductEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSaleScreen(
    viewModel: NewSaleViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        val msg = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.consumeMessage()
    }

    LaunchedEffect(state.shouldNavigateToHistory) {
        if (state.shouldNavigateToHistory) {
            viewModel.consumeMessage()
            onNavigateToHistory()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(
            title = { Text("Nueva venta") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProductDropdown(
                products = state.products,
                selected = state.selectedProduct,
                onSelect = viewModel::onSelectProduct
            )

            OutlinedTextField(
                value = state.quantityText,
                onValueChange = viewModel::onQuantityChange,
                label = { Text("Cantidad") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::addToCart,
                enabled = !state.isSaving && state.products.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar al carrito")
            }

            if (state.cart.isEmpty()) {
                Text("Carrito vacío")
            } else {
                Text("Carrito", style = MaterialTheme.typography.titleMedium)

                state.cart.forEach { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text("Cantidad: ${item.quantity}  •  Precio: ${item.unitPrice}")
                                Text("Subtotal: ${item.lineTotal}")
                            }
                            TextButton(onClick = { viewModel.removeFromCart(item.productId) }) {
                                Text("Quitar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Total: ${state.total} ARS", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = viewModel::registerSale,
                enabled = !state.isSaving && state.products.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Guardando..." else "Registrar venta")
            }

            if(state.message != null){
                Text(
                    text = state.message!!,
                    color = if(state.message!!.contains("✅")) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDropdown(
    products: List<ProductEntity>,
    selected: ProductEntity?,
    onSelect: (ProductEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded}
    ) {
        OutlinedTextField(
            value = selected?.name ?: "Selecciona un producto",
            onValueChange = {},
            readOnly = true,
            label = { Text("Producto") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            products.forEach { product ->
                DropdownMenuItem(
                    text = { Text(product.name) },
                    onClick = {
                        onSelect(product)
                        expanded = false
                    }
                )
            }
        }
    }
}

