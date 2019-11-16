package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Identidad

@Dao
interface IdentidadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIdentidadTask(c: Identidad)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIdentidadListTask(c: List<Identidad>)

    @Update
    fun updateIdentidadTask(vararg c: Identidad)

    @Delete
    fun deleteIdentidadTask(c: Identidad)

    @Query("SELECT * FROM Identidad")
    fun getIdentidadTask(): LiveData<Identidad>

    @Query("SELECT * FROM Identidad")
    fun getIdentidad(): Identidad

    @Query("SELECT * FROM Identidad")
    fun getIdentidadById(): LiveData<Identidad>

    @Query("DELETE FROM Identidad")
    fun deleteAll()
}