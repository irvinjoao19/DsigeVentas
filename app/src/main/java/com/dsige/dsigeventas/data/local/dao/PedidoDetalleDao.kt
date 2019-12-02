package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.PedidoDetalle

@Dao
interface PedidoDetalleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductoTask(c: PedidoDetalle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductoListTask(c: List<PedidoDetalle>)

    @Update
    fun updateProductoTask(vararg c: PedidoDetalle)

    @Delete
    fun deleteProductoTask(c: PedidoDetalle)

    @Query("SELECT * FROM PedidoDetalle")
    fun getProductoTask(): LiveData<PedidoDetalle>

    @Query("SELECT * FROM PedidoDetalle")
    fun getProducto(): PedidoDetalle

    @Query("SELECT * FROM PedidoDetalle WHERE productoId=:id")
    fun getAllProductoTask(id: Int): List<PedidoDetalle>

    @Query("SELECT * FROM PedidoDetalle WHERE productoId =:id")
    fun getProductoById(id: Int): LiveData<PedidoDetalle>

    @Query("SELECT * FROM PedidoDetalle WHERE pedidoId=:pedido AND productoId =:producto")
    fun getProductoExits(pedido: Int,producto : Int): Boolean

    @Query("DELETE FROM PedidoDetalle")
    fun deleteAll()

    @Query("SELECT * FROM PedidoDetalle WHERE pedidoId=:id")
    fun getProductoByPedido(id: Int): DataSource.Factory<Int, PedidoDetalle>

    @Query("SELECT * FROM PedidoDetalle WHERE pedidoId=:id")
    fun getPedidoById(id: Int): List<PedidoDetalle>

    @Query("SELECT COUNT(*) FROM PedidoDetalle WHERE pedidoId=:id")
    fun validateCountPedido(id: Int): Int

    @Query("SELECT COUNT(*) FROM PedidoDetalle WHERE estado = 0 AND pedidoId=:id")
    fun validatePedido(id: Int): Int

    @Query("UPDATE PedidoDetalle SET estado = 1 WHERE pedidoId=:id")
    fun updatePedidoEnabled(id: Int)

}