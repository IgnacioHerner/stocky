package com.ignaherner.stocky.ui.screens.sales.sales_history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private fun startDay(millis: Long) : Long {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = millis }
    cal.set(java.util.Calendar.HOUR_OF_DAY,0)
    cal.set(java.util.Calendar.MINUTE,0)
    cal.set(java.util.Calendar.SECOND,0)
    cal.set(java.util.Calendar.MILLISECOND,0)
    return cal.timeInMillis
}

private fun endOfDay(millis: Long) : Long {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = millis }
    cal.set(java.util.Calendar.HOUR_OF_DAY,0)
    cal.set(java.util.Calendar.MINUTE,0)
    cal.set(java.util.Calendar.SECOND,0)
    cal.set(java.util.Calendar.MILLISECOND,0)
    return cal.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHistoryScreen(
    viewModel: SalesHistoryViewModel,
    onBack: () -> Unit) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()


    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Historial de ventas") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        ) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            FilterRow(
                from = state.from,
                to = state.to,
                onApply = {fromMillis, toMillis ->
                    viewModel.applyFilter(
                        from = startDay(fromMillis),
                        to = endOfDay(toMillis)
                    )
                }
            )

            if (state.sales.isEmpty()){
                Text("No hay ventas en este rango.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()) {
                    items(state.sales) { sale ->
                        SaleSummaryCard(sale)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterRow(
    from: Long?,
    to: Long?, onApply: (fromMillis: Long, toMillis: Long) -> Unit
) {
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    var selectedFrom by remember { mutableLongStateOf(from ?: System.currentTimeMillis()) }
    var selectedTo by remember { mutableLongStateOf(to ?: System.currentTimeMillis()) }

    // sincronizar si cambia el state desde VM
    LaunchedEffect(from, to) {
        if (from != null) selectedFrom = from
        if (to != null) selectedTo = to
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {showFromPicker = true},
            modifier = Modifier.weight(1f)
        ) { Text("Desde")}

        OutlinedButton(
            onClick = {showToPicker = true},
            modifier = Modifier.weight(1f)
        ) { Text("Hasta")}

        Button(onClick = {onApply(selectedFrom, selectedTo) }) {
            Text("Aplicar")
        }
    }

    if(showFromPicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = selectedFrom)
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false},
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { selectedFrom = it }
                    showFromPicker = false
                }) { Text("OK")}
            }
        ) { DatePicker(state = pickerState) }
    }

    if (showToPicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = selectedTo)
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { selectedTo = it }
                    showToPicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = pickerState) }
    }


}

@Composable
fun SaleSummaryCard(sale: SaleSummaryUi) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val dateText = formatter.format(Date(sale.date))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(dateText, style = MaterialTheme.typography.titleMedium)
            Text("Total: ${sale.total} ARS")
            Text("Lineas: ${sale.itemsCount} = Productos: ${sale.productsCount}")
        }
    }
}