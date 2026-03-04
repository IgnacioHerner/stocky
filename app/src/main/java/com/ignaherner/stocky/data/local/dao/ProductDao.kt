package com.ignaherner.stocky.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ignaherner.stocky.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // “Quiero obtener todos los productos, ordenados por nombre, y que se actualicen solos si algo cambia.”
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun observeProducts(): Flow<List<ProductEntity>>

    // “Sumá tod el stock por su costo y si no hay nada, devolveme 0”.
    @Query("SELECT COALESCE(SUM(currentStock * cost), 0) FROM PRODUCTS")
    fun observeTotalCost(): Flow<Double>

    // "Suma tod el stock por el precio de venta y sino hay nada, devolveme 0"
    @Query("SELECT COALESCE(SUM(currentStock * salePrice), 0) FROM products")
    fun observeTotalSaleValue(): Flow<Double>

    //Obtener un producto por id(para chequear el stock actual)
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ProductEntity?

    // Incrementar stock
    @Query("UPDATE products SET currentStock = currentStock + :amount WHERE id = :productId")
    suspend fun increaseStock(productId: Long, amount: Int): Int

    // Actualizar stock
    @Query("UPDATE products SET currentStock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Long, newStock: Int)

    // Inserta este producto en la base
    @Insert
    suspend fun insert(product: ProductEntity)

    // Actualzia este producto en la base
    @Update
    suspend fun update(product: ProductEntity)

    // Borra este producto en la base
    @Delete
    suspend fun delete(product: ProductEntity)
}