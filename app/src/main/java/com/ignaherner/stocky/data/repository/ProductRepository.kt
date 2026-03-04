package com.ignaherner.stocky.data.repository

import com.ignaherner.stocky.data.local.dao.ProductDao
import com.ignaherner.stocky.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao
) {

    //Lecturas reactivas
    fun observeProducts(): Flow<List<ProductEntity>> = productDao.observeProducts()

    fun observeTotalCost(): Flow<Double> = productDao.observeTotalCost()

    fun observeTotalSaleValue(): Flow<Double> = productDao.observeTotalSaleValue()

    // Acciones puntuales

    suspend fun increaseStock(productId: Long, amount: Int) {
        require(amount > 0){"amount must be > 0"}
        productDao.increaseStock(productId, amount)
    }

    suspend fun insert(product: ProductEntity) = productDao.insert(product)

    suspend fun update(product: ProductEntity) = productDao.update(product)

    suspend fun delete(product: ProductEntity) = productDao.delete(product)
}