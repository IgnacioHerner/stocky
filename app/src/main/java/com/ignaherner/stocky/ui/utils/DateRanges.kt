package com.ignaherner.stocky.ui.utils

import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

object DateRanges {

    private val zone: ZoneId = ZoneId.systemDefault()

    fun startOfDayMillis(date: LocalDate): Long =
        date.atStartOfDay(zone).toInstant().toEpochMilli()

    fun endOfDayMillis(date: LocalDate): Long =
        date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

    fun today(): Pair<Long, Long> {
        val d = LocalDate.now(zone)
        return startOfDayMillis(d) to endOfDayMillis(d)
    }

    fun last7Days(): Pair<Long, Long>{
        val today = LocalDate.now(zone)
        val from = today.minusDays(6) // Esto incluye hoy = 7 dias
        return startOfDayMillis(from) to endOfDayMillis(today)
    }

    fun thisMonthToToday(): Pair<Long, Long> {
        val today = LocalDate.now(zone)
        val firstDay = YearMonth.from(today).atDay(1)
        return startOfDayMillis(firstDay) to endOfDayMillis(today)
    }
}