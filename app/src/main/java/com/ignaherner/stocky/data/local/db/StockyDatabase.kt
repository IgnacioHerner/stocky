package com.ignaherner.stocky.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ignaherner.stocky.data.local.dao.ProductDao
import com.ignaherner.stocky.data.local.entity.ProductEntity
import com.ignaherner.stocky.data.local.entity.SaleEntity
import com.ignaherner.stocky.data.local.entity.SaleItemEntity


@Database(
    entities = [
        ProductEntity::class,
        SaleEntity::class,
        SaleItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StockyDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

}