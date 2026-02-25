package com.ignaherner.stocky.data.repository

import androidx.room.withTransaction
import com.ignaherner.stocky.data.local.db.StockyDatabase
import com.ignaherner.stocky.data.local.entity.SaleEntity
import com.ignaherner.stocky.data.local.entity.SaleItemEntity
import com.ignaherner.stocky.data.local.relation.SaleWithItems
import com.ignaherner.stocky.data.repository.models.NewSaleItem
import kotlinx.coroutines.flow.Flow

class SalesRepository(
    private val database: StockyDatabase
) {

    private val saleDao = database.saleDao()
    private val productDao = database.productDao()

    fun observeSalesWithItems(): Flow<List<SaleWithItems>> =
        saleDao.observeSalesWithItems()

    /*
    * Inserta venta + items y descuenta stock
    * To_do atomico: si falla stock o cualquier paso, no se guarda nada
    * */

    fun observeSalesBetween(from: Long, to: Long): Flow<List<SaleWithItems>> =
        saleDao.observeSalesWithItemsBetween(from, to)

    fun observeSaleWithItems(saleId: Long) =
        saleDao.observeSaleWithItems(saleId)

    suspend fun registerSale(
        date: Long,
        items: List<NewSaleItem>
    ) {
        database.withTransaction {

            // 1) Validar stock y cualquier stock dentro de transaccion (estado consistente)
            val total = items.sumOf { it.unitPrice * it.quantity }

            // 2) Insert sale y obtener saleId
            val saleId = saleDao.insertSale(
                SaleEntity(
                    date = date,
                    total = total
                )
            )

            // 3) Insert items
            val saleItemsEntities = items.map { item ->
                val product = productDao.getById(item.productId)
                    ?:throw java.lang.IllegalStateException("Producto no encontrado: ${item.productId}")
                SaleItemEntity(
                    saleId = saleId,
                    productId = item.productId,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    unitCost = product.cost

                )
            }
            saleDao.insertSaleItems(saleItemsEntities)

            // 4) Descontar stock (con validacion)
            // Importante: si tiramos exception, Room revierte toda la transaccion
            for(item in items) {
                val product = productDao.getById(item.productId)
                    ?: throw IllegalStateException("Producto ${item.productId} no existe")
                val newStock = product.currentStock - item.quantity

                if(newStock < 0 ) {
                    throw InsufficientStockException(
                        "Stock insuficiente para '${product.name}'. Disponible: ${product.currentStock}, requerido: ${item.quantity}"
                    )
                }

                productDao.updateStock(productId = product.id, newStock = newStock)
            }
        }
    }

    suspend fun deleteSaleAndRestoreStock(saleId: Long) {
        database.withTransaction {

            // 1) Traer items vendidos
            val items = saleDao.getSaleItems(saleId)
            if (items.isEmpty()) {
                // Si no hay items, igual borramos sale por si existe
                saleDao.deleteSaleById(saleId)
                return@withTransaction
            }

            // 2) Restaurar stock producto por producto
            for(item in items) {
                val product = productDao.getById(item.productId)
                    ?: continue // Si el producto no existe, evitamos crashear

                val restored = product.currentStock + item.quantity
                productDao.updateStock(product.id, restored)
            }

            // 3) Borrar items y sale (en este orden)
            saleDao.deleteSaleItemsBySaleId(saleId)
            saleDao.deleteSaleById(saleId)
        }
    }
}

class InsufficientStockException(message: String) : Exception(message)


