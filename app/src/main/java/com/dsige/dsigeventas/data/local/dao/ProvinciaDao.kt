package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Provincia

@Dao
interface ProvinciaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProvinciaTask(c: Provincia)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProvinciaListTask(c: List<Provincia>)

    @Update
    fun updateProvinciaTask(vararg c: Provincia)

    @Delete
    fun deleteProvinciaTask(c: Provincia)

    @Query("SELECT * FROM Provincia")
    fun getProvinciaTask(): LiveData<Provincia>

    @Query("SELECT * FROM Provincia")
    fun getProvincia(): Provincia

    @Query("SELECT * FROM Provincia WHERE codigoDeparmento=:id")
    fun getProvinciasById(id: String): LiveData<List<Provincia>>

    @Query("DELETE FROM Provincia")
    fun deleteAll()
}