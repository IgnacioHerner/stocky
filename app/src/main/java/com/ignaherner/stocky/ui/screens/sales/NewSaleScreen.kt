package com.ignaherner.stocky.ui.screens.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.stocky.data.local.entity.ProductEntity
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSaleScreen(viewModel: NewSaleViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nueva venta") }) }
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
                label = { Text("Cantidad")},
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::saveSale,
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