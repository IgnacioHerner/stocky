package com.ignaherner.stocky.ui.screens.sales.sales_history

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
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
import com.ignaherner.stocky.ui.components.EmptyState
import com.ignaherner.stocky.ui.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


private fun startOfLocalDayFromPicker(selectedDateMillis: Long): Long {
    val localDate = selectedMillisToLocalDate(selectedDateMillis)
    return localDate
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun endOfLocalDayFromPicker(selectedDateMillis: Long): Long {
    val localDate = selectedMillisToLocalDate(selectedDateMillis)
    return localDate
        .plusDays(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli() - 1
}

private val dayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun selectedMillisToLocalDate(selectedDateMillis: Long) : LocalDate {
    return Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}

private fun formatSelectedDate(selectedDateMillis: Long) : String {
    val date = selectedMillisToLocalDate(selectedDateMillis)
    return date.format(dayFormatter)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHistoryScreen(
    viewModel: SalesHistoryViewModel,
    onBack: () -> Unit,
    onSaleClick: (Long) -> Unit
) {

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
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            QuickFilterRow(
                onToday = { viewModel.setTodayFilter() },
                onLast7Days = {viewModel.setLast7DaysFilter()},
                onThisMonth = { viewModel.setThisMonthFilter() },
                onClear = { viewModel.clearFilter() },
            )

            Spacer(Modifier.height(12.dp))

            FilterRow(
                from = state.fromSelected,
                to = state.toSelected,
                onApply = {fromMillis, toMillis ->
                    viewModel.applyFilter(
                        from = startOfLocalDayFromPicker(fromMillis),
                        to = endOfLocalDayFromPicker(toMillis)
                    )
                }
            )

            if (state.sales.isEmpty()) {
                EmptyState(
                    title = "No hay ventas en este rango",
                    message = "Probá cambiar el filtro de fechas o limpiarlo para ver todas.",
                    actionLabel = "Limpiar filtro",
                    onAction = { viewModel.clearFilter() }
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()) {
                    items(state.sales) { sale ->
                        SaleSummaryCard(
                            sale = sale,
                            onClick = {onSaleClick(sale.id)}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickFilterRow(
    onToday: () -> Unit,
    onLast7Days: () -> Unit,
    onThisMonth: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(onClick = onToday, label = { Text("Hoy")})
        AssistChip(onClick = onLast7Days, label = { Text("7 días")})
        AssistChip(onClick = onThisMonth, label = { Text("Mes")})
        AssistChip(onClick = onClear, label = { Text("Limpiar")})


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
        ) {
            Text(
                text = "Desde\n${formatSelectedDate(selectedFrom)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        OutlinedButton(
            onClick = {showToPicker = true},
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Hasta\n${formatSelectedDate(selectedTo)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

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
fun SaleSummaryCard(
    sale: SaleSummaryUi,
    onClick: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val dateText = formatter.format(Date(sale.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(dateText, style = MaterialTheme.typography.titleMedium)
            Text("Total: ${CurrencyFormatter.formatARS(sale.total)} ARS")
            Text("Lineas: ${sale.itemsCount} = Productos: ${sale.productsCount}")
        }
    }
}