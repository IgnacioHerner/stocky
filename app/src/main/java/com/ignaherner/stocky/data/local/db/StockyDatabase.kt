package com.ignaherner.stocky.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ignaherner.stocky.data.local.dao.ProductDao
import com.ignaherner.stocky.data.local.entity.ProductEntity


@Database(
    entities = [
        ProductEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class StockyDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

}