package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Pedido

@Dao
interface PedidoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPedidoTask(c: Pedido)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPedidoListTask(c: List<Pedido>)

    @Update
    fun updatePedidoTask(vararg c: Pedido)

    @Delete
    fun deletePedidoTask(c: Pedido)

    @Query("SELECT * FROM Pedido")
    fun getPedidoTask(): LiveData<Pedido>

    @Query("SELECT * FROM Pedido WHERE pedidoId =:id")
    fun getPedidoByIdTask(id: Int): Pedido

    @Query("SELECT * FROM Pedido")
    fun getPedido(): DataSource.Factory<Int, Pedido>

    @Query("DELETE FROM Pedido")
    fun deleteAll()

    @Query("SELECT * FROM Pedido WHERE personalVendedorId=:id")
    fun getPedidoByUser(id: Int): DataSource.Factory<Int, Pedido>

    @Query("UPDATE Pedido SET estado = 1 WHERE pedidoId=:id")
    fun updatePedidoEnabled(id: Int)

    @Query("SELECT * FROM Pedido WHERE pedidoId=:id")
    fun getPedidoCliente(id: Int): LiveData<Pedido>

    @Query("UPDATE Pedido SET subtotal=:subTotal  ,totalNeto =:total WHERE pedidoId=:id")
    fun updateTotalPedido(id: Int, total: Double, subTotal: Double)

    @Query("SELECT * FROM Pedido WHERE pedidoId=:id")
    fun getPedidoById(id: Int): Boolean

}