package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Producto

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductoTask(c: Producto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductoListTask(c: List<Producto>)

    @Update
    fun updateProductoTask(vararg c: Producto)

    @Delete
    fun deleteProductoTask(c: Producto)

    @Query("SELECT * FROM Producto")
    fun getProductoTask(): LiveData<Producto>

    @Query("SELECT * FROM Producto")
    fun getProducto(): Producto

    @Query("SELECT * FROM Producto WHERE productoId=:id")
    fun getAllProductoTask(id: Int): List<Producto>

    @Query("SELECT * FROM Producto WHERE ProductoId =:id")
    fun getProductoById(id: Int): LiveData<Producto>

    @Query("DELETE FROM Producto")
    fun deleteAll()

}