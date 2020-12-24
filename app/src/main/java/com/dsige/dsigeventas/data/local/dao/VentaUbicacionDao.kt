package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.VentaUbicacion

@Dao
interface VentaUbicacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVentaUbicacionTask(c: VentaUbicacion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVentaUbicacionListTask(c: List<VentaUbicacion>)

    @Update
    fun updateVentaUbicacionTask(vararg c: VentaUbicacion)

    @Delete
    fun deleteVentaUbicacionTask(c: VentaUbicacion)

    @Query("SELECT * FROM VentaUbicacion")
    fun getVentaUbicacion(): LiveData<List<VentaUbicacion>>

    @Query("DELETE FROM VentaUbicacion")
    fun deleteAll()

    @Query("SELECT * FROM VentaUbicacion WHERE pedidoCabId =:id")
    fun getVentaUbicacionById(id: Int): LiveData<VentaUbicacion>

}