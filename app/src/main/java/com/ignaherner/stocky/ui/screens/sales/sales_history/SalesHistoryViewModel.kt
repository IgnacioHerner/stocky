package com.ignaherner.stocky.ui.screens.sales.sales_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class SalesHistoryUiState(
    val sales: List<SaleSummaryUi> = emptyList(),
    val from: Long? = null,
    val to: Long? = null,
    val message: String? = null
)

private data class DateRange(val from: Long, val to: Long)


class SalesHistoryViewModel(
    private val salesRepository: SalesRepository

) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesHistoryUiState())
    val uiState: StateFlow<SalesHistoryUiState> = _uiState

    private val rangeFlow = MutableStateFlow<DateRange?>(null)

    init {
        val now = System.currentTimeMillis()
        val from = now - TimeUnit.DAYS.toMillis(30)
        val to = now

        rangeFlow.value = DateRange(from, to)

        viewModelScope.launch {
            rangeFlow
                .filterNotNull()
                .flatMapLatest { range ->  salesRepository.observeSalesBetween(range.from, range.to) }
                .collectLatest { sales ->
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

                    _uiState.update {
                        it.copy(
                            sales = mapped,
                            from = rangeFlow.value!!.from,
                            to = rangeFlow.value!!.to
                        )
                    }
                }
        }

    }

    fun applyFilter(from: Long, to: Long) {
        rangeFlow.value = DateRange(from, to)
    }
}