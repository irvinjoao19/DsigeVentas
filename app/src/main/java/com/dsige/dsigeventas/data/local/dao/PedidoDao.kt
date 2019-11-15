package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
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
    fun getPedidoByIdTask(id: Int): List<Pedido>

    @Query("SELECT * FROM Pedido")
    fun getPedido(): Pedido

    @Query("DELETE FROM Pedido")
    fun deleteAll()

}