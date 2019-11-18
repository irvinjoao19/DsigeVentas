package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Cliente

@Dao
interface ClienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClienteTask(c: Cliente)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClienteListTask(c: List<Cliente>)

    @Update
    fun updateClienteTask(vararg c: Cliente)

    @Delete
    fun deleteClienteTask(c: Cliente)

    @Query("SELECT * FROM Cliente")
    fun getCliente(): DataSource.Factory<Int, Cliente>

    @Query("SELECT * FROM Cliente WHERE departamentoId=:d AND provinciaId=:p AND distritoId=:s AND nombreCliente=:search")
    fun getCliente(
        d: Int,
        p: Int,
        s: Int,
        search: String
    ): DataSource.Factory<Int, Cliente>

    @Query("SELECT * FROM Cliente")
    fun getClienteTask(): Cliente

    @Query("SELECT * FROM Cliente WHERE clienteId =:id")
    fun getClienteById(id: Int): LiveData<Cliente>

    @Query("DELETE FROM Cliente")
    fun deleteAll()
}