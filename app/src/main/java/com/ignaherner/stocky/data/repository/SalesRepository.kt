package com.ignaherner.stocky.data.repository

import androidx.room.withTransaction
import com.ignaherner.stocky.data.local.db.StockyDatabase
import com.ignaherner.stocky.data.local.entity.SaleEntity
import com.ignaherner.stocky.data.local.entity.SaleItemEntity
import com.ignaherner.stocky.data.local.relation.SaleWithItems
import kotlinx.coroutines.flow.Flow

class SalesRepository(
    private val database: StockyDatabase
) {

    private val saleDao = database.saleDao()

    fun observeSalesWithItems(): Flow<List<SaleWithItems>> =
        saleDao.observeSalesWithItems()

    suspend fun insertSaleWithItems(
        date: Long,
        total: Double,
        items: List<NewSaleItem>
    ) {
        database.withTransaction {

            // 1) Insert Sale -> obtenemos id generado
            val saleId = saleDao.insertSale(
                SaleEntity(
                    date = date,
                    total = total
                )
            )

            // 2) Convertimos a entidades reales con saleId
            val entities = items.map { item ->
                SaleItemEntity(
                    saleId = saleId,
                    productId = item.productId,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice
                )
            }

            // 3) Insert items
            saleDao.insertSaleItems(entities)
        }
    }
}

data class NewSaleItem (
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double
)

