package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.VentaUbicacionVendedor

@Dao
interface VentaUbicacionVendedorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVentaUbicacionVendedorTask(c: VentaUbicacionVendedor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVentaUbicacionVendedorListTask(c: List<VentaUbicacionVendedor>)

    @Update
    fun updateVentaUbicacionVendedorTask(vararg c: VentaUbicacionVendedor)

    @Delete
    fun deleteVentaUbicacionVendedorTask(c: VentaUbicacionVendedor)

    @Query("SELECT * FROM VentaUbicacionVendedor")
    fun getVentaUbicacionVendedor(): LiveData<List<VentaUbicacionVendedor>>

    @Query("DELETE FROM VentaUbicacionVendedor")
    fun deleteAll()

    @Query("SELECT * FROM VentaUbicacionVendedor WHERE id =:i")
    fun getVentaUbicacionVendedorById(i: Int): LiveData<VentaUbicacionVendedor>

}