package com.ignaherner.stocky.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ignaherner.stocky.data.local.entity.ProductEntity

@Composable
fun ProductFormDialog(
    initialProduct: ProductEntity? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, cost: Double, salePrice: Double, currentStock: Int, minimumStock: Int, category: String) -> Unit
) {
    val isEdit = initialProduct != null

    var name by rememberSaveable { mutableStateOf(initialProduct?.name ?: "") }
    var category by rememberSaveable { mutableStateOf(initialProduct?.category ?: "")}

    var costText by rememberSaveable { mutableStateOf(initialProduct?.cost?.toString() ?: "")}
    var salePriceText by rememberSaveable { mutableStateOf(initialProduct?.salePrice?.toString() ?: "")}
    var currentStockText by rememberSaveable { mutableStateOf(initialProduct?.currentStock?.toString() ?: "")}
    var minimumStockText by rememberSaveable { mutableStateOf(initialProduct?.minimumStock?.toString() ?: "")}

    var error by rememberSaveable { mutableStateOf<String?>(null)}

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Editar producto" else "Agregar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {name = it},
                    label = { Text("Nombre")},
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it},
                    label = { Text("Categoria")},
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = costText,
                    onValueChange = {costText = it},
                    label = { Text("Costo (ARS)")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = salePriceText,
                    onValueChange = { salePriceText = it},
                    label = { Text("Precio de venta (ARS)")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = currentStockText,
                        onValueChange = { currentStockText = it},
                        label = { Text("Stock")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minimumStockText,
                        onValueChange = { minimumStockText = it},
                        label = { Text("Stock min")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val trimmedName = name.trim()
                val trimmedCategory = category.trim().ifEmpty { "General" }

                val cost = costText.toDoubleOrNull()
                val salePrice = salePriceText.toDoubleOrNull()
                val currentStock = currentStockText.toIntOrNull()
                val minimunStock = minimumStockText.toIntOrNull()

                error = when {
                    trimmedName.isEmpty() -> "El nombre no puede estar vacio"
                    cost == null || cost < 0 -> "Costo inválido"
                    salePrice == null || salePrice < 0 -> "Precio de venta inválido"
                    currentStock == null || currentStock < 0 -> "Stock inválido"
                    minimunStock == null || minimunStock < 0 -> "Stock mínimo invalido"
                    else -> null
                }
                if(error == null) {
                    onSave(
                        trimmedName,
                        cost!!,
                        salePrice!!,
                        currentStock!!,
                        minimunStock!!,
                        trimmedCategory
                    )
                }
            }) {
                Text(if (isEdit) "Guardar cambios" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}