package com.ignaherner.stocky.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long, // timestamp (ej: System.currentTimeMillis())
    val total: Double
)
