package com.ignaherner.stocky.di

import android.content.Context
import androidx.room.Room
import com.ignaherner.stocky.data.local.db.StockyDatabase
import com.ignaherner.stocky.data.repository.ProductRepository

class AppContainer(context: Context) {

    // Database
    private val database: StockyDatabase =
        Room.databaseBuilder(
            context,
            StockyDatabase::class.java,
            "stocky_db"
        )
            .fallbackToDestructiveMigration() // MVP: recrea base si cambia esquema
            .build()

    // Repository
    val productRepository: ProductRepository =
        ProductRepository(database.productDao())
}