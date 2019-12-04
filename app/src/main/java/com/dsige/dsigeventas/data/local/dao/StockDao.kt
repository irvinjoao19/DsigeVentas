package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Stock

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStockTask(c: Stock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStockListTask(c: List<Stock>)

    @Update
    fun updateStockTask(vararg c: Stock)

    @Delete
    fun deleteStockTask(c: Stock)

    @Query("SELECT * FROM Stock")
    fun getStockTask(): LiveData<Stock>

    @Query("SELECT * FROM Stock")
    fun getStock(): Stock

    @Query("SELECT * FROM Stock WHERE productoId=:id")
    fun getAllStockTask(id: Int): List<Stock>

    @Query("SELECT * FROM Stock WHERE productoId =:id")
    fun getStockById(id: Int): LiveData<Stock>

    @Query("SELECT * FROM Stock")
    fun getProductos(): DataSource.Factory<Int, Stock>

    @Query("SELECT * FROM Stock WHERE nombreProducto LIKE :s")
    fun getProductos(s:String): DataSource.Factory<Int, Stock>

    @Query("DELETE FROM Stock")
    fun deleteAll()

    @Query("SELECT * FROM Stock WHERE isSelected=:b")
    fun getStockSelected(b: Boolean): List<Stock>

    @Query("UPDATE Stock SET isSelected=:b")
    fun enabledStockSelected(b: Boolean)

}