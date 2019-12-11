package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Reparto

@Dao
interface RepartoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepartoTask(c: Reparto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepartoListTask(c: List<Reparto>)

    @Update
    fun updateRepartoTask(vararg c: Reparto)

    @Delete
    fun deleteRepartoTask(c: Reparto)

    @Query("SELECT * FROM Reparto WHERE repartoId =:id")
    fun getRepartoByIdTask(id: Int): Reparto

    @Query("SELECT * FROM Reparto")
    fun getRepartos(): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e")
    fun getMapReparto(e: Int): List<Reparto>

    @Query("SELECT * FROM Reparto WHERE repartoId=:id")
    fun getRepartosById(id: Int): LiveData<List<Reparto>>

    @Query("DELETE FROM Reparto")
    fun deleteAll()

    @Query("SELECT * FROM Reparto WHERE repartoId=:id")
    fun getRepartoById(id: Int): LiveData<Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e")
    fun getReparto(e: Int): LiveData<List<Reparto>>
}