package com.ignaherner.stocky.ui.screens.sales.sales_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.stocky.data.repository.SalesRepository
import com.ignaherner.stocky.ui.utils.DateRanges
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    val fromSelected: Long? = null,
    val toSelected: Long? = null,
    val message: String? = null
)

private data class DateRange(val from: Long?, val to: Long?)


@OptIn(ExperimentalCoroutinesApi::class)
class SalesHistoryViewModel(
    private val salesRepository: SalesRepository

) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesHistoryUiState())
    val uiState: StateFlow<SalesHistoryUiState> = _uiState

    private val rangeFlow = MutableStateFlow(DateRange(null, null))

    init {
        viewModelScope.launch {
            rangeFlow
                .flatMapLatest { range ->
                    salesRepository.observeSalesBetween(range.from, range.to)
                }
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

                    val range = rangeFlow.value
                    _uiState.update {
                        it.copy(
                            sales = mapped,
                            fromSelected = range.from,
                            toSelected = range.to
                        )
                    }
                }
        }
    }


    fun applyFilter(from: Long?, to: Long?) {
        rangeFlow.value = DateRange(from, to)
    }

    fun setTodayFilter() {
        val (from, to) = DateRanges.today()
        applyFilter (from, to)
    }

    fun setLast7DaysFilter() {
        val (from, to) = DateRanges.last7Days()
        applyFilter(from, to)
    }

    fun setThisMonthFilter() {
        val (from, to) = DateRanges.thisMonthToToday()
        applyFilter(from, to)
    }

    fun clearFilter() {
        applyFilter(null, null)
    }
}