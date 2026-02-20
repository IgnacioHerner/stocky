package com.ignaherner.stocky.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ignaherner.stocky.data.local.entity.SaleEntity
import com.ignaherner.stocky.data.local.entity.SaleItemEntity
import com.ignaherner.stocky.data.local.relation.SaleWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Transaction
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun observeSalesWithItems(): Flow<List<SaleWithItems>>

//    @Transaction
//    @Query("SELECT * FROM sales WHERE date BETWEEN :from AND :to ORDER BY date DESC")
//    fun observeSalesWithItemsBetween(from: Long, to: Long): Flow<List<SaleWithItems>>

    @Query("SELECT COUNT(*) FROM sales")
    fun observeSalesCount(): Flow<Int>

    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun observeSales(): Flow<List<SaleEntity>>

    @Insert
    suspend fun insertSale(sale: SaleEntity) : Long

    @Insert
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

}