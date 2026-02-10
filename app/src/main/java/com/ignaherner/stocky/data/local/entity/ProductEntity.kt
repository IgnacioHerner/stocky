package com.ignaherner.stocky.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val cost: Double,
    val salePrice: Double,
    val currentStock: Int,
    val minimumStock: Int,
    val category: String
)
