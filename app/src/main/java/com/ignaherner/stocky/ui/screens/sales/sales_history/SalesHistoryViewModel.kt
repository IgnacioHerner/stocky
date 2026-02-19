package com.ignaherner.stocky.ui.screens.sales.sales_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class SalesHistoryUiState(
    val sales: List<SaleSummaryUi> = emptyList(),
    val from: Long? = null,
    val to: Long? = null,
    val message: String? = null
)

class SalesHistoryViewModel(
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesHistoryUiState())
    val uiState: StateFlow<SalesHistoryUiState> = _uiState

    init {
        val now = System.currentTimeMillis()
        val from = now - TimeUnit.DAYS.toMillis(30)
        val to = now

        _uiState.update { it.copy(from = from, to = to) }

        observeBetween(from, to)
    }

    private fun observeBetween(from: Long, to: Long) {
        viewModelScope.launch {
            salesRepository.observeSalesBetween(from, to).collectLatest { sales ->
                val mapped = sales.map { saleWithItems ->
                    val itemsCount = saleWithItems.items.size
                    val productsCount = saleWithItems.items.sumOf { it.quantity }

                    SaleSummaryUi(
                        id = saleWithItems.sale.id,
                        date = saleWithItems.sale.date,
                        total = saleWithItems.sale.total,
                        itemsCount = itemsCount,
                        productsCount = productsCount
                    )
                }

                _uiState.update { it.copy(sales = mapped) }
            }
        }
    }

    fun applyFilter(from: Long, to: Long) {
        _uiState.update { it.copy(from = from, to = to) }
        observeBetween(from, to)
    }
}