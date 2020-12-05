package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Distrito

@Dao
interface DistritoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistritoTask(c: Distrito)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistritoListTask(c: List<Distrito>)

    @Update
    fun updateDistritoTask(vararg c: Distrito)

    @Delete
    fun deleteDistritoTask(c: Distrito)

    @Query("SELECT * FROM Distrito")
    fun getDistritoTask(): LiveData<Distrito>

    @Query("SELECT * FROM Distrito")
    fun getDistrito(): Distrito

    @Query("SELECT * FROM Distrito WHERE codigoProvincia=:pId AND codigoDepartamento=:dId")
    fun getDistritosById(dId: String, pId: String): LiveData<List<Distrito>>

    @Query("DELETE FROM Distrito")
    fun deleteAll()

    @Query("SELECT distritoId FROM Distrito WHERE nombre LIKE :d")
    fun searchDistritoId(d: String): Int
}