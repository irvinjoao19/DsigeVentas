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

    @Query("SELECT * FROM Cliente WHERE (nombreCliente LIKE :search OR documento LIKE :search) ")
    fun getCliente(search: String): DataSource.Factory<Int, Cliente>

    @Query("SELECT * FROM Cliente WHERE distritoId=:s AND (nombreCliente LIKE :search OR documento LIKE :search) ")
    fun getCliente(s: Int, search: String): DataSource.Factory<Int, Cliente>

    @Query("SELECT * FROM Cliente WHERE distritoId=:s")
    fun getCliente(s: Int): DataSource.Factory<Int, Cliente>

    @Query("SELECT * FROM Cliente")
    fun getClienteTask(): Cliente

    @Query("SELECT * FROM Cliente WHERE clienteId =:id")
    fun getClienteById(id: Int): LiveData<Cliente>

    @Query("DELETE FROM Cliente")
    fun deleteAll()

    @Query("SELECT * FROM Cliente WHERE identity =:id")
    fun getClienteTaskById(id: Int): Cliente

    @Query("SELECT identity FROM Cliente WHERE clienteId =:id")
    fun getClienteIdentity(id: Int): Int

    @Query("SELECT estado FROM Cliente WHERE clienteId =:id")
    fun getClienteIsUpdte(id: Int): Int

    @Query("UPDATE Cliente SET identity =:codigoRetorno WHERE clienteId =:codigoBase")
    fun updateCliente(codigoBase: Int, codigoRetorno: Int)

    @Query("UPDATE Cliente SET nameImg =:name WHERE clienteId =:id")
    fun updatePhotoCliente(id: Int, name: String)

    @Query("SELECT * FROM Cliente WHERE nombreDistrito LIKE :d  ")
    fun getClienteByDistrito(d: String): LiveData<List<Cliente>>
}