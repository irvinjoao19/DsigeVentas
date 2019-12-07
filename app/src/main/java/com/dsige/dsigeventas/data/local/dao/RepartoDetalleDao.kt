package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.RepartoDetalle

@Dao
interface RepartoDetalleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepartoDetalleTask(c: RepartoDetalle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepartoDetalleListTask(c: List<RepartoDetalle>)

    @Update
    fun updateRepartoDetalleTask(vararg c: RepartoDetalle)

    @Delete
    fun deleteRepartoDetalleTask(c: RepartoDetalle)

    @Query("SELECT * FROM RepartoDetalle")
    fun getRepartoDetalleTask(): RepartoDetalle

    @Query("SELECT * FROM RepartoDetalle")
    fun getRepartoDetalles(): DataSource.Factory<Int, RepartoDetalle>

    @Query("SELECT * FROM RepartoDetalle")
    fun getMapRepartoDetalle(): List<RepartoDetalle>

    @Query("SELECT * FROM RepartoDetalle WHERE repartoId=:id")
    fun getRepartoDetallesById(id: Int): LiveData<List<RepartoDetalle>>

    @Query("DELETE FROM RepartoDetalle")
    fun deleteAll()

    @Query("SELECT * FROM RepartoDetalle WHERE repartoId=:id")
    fun getRepartoDetalleById(id: Int): LiveData<RepartoDetalle>

    @Query("SELECT * FROM RepartoDetalle WHERE repartoId=:id")
    fun getDetalleRepartoById(id: Int): DataSource.Factory<Int, RepartoDetalle>
}