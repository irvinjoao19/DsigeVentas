package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Departamento

@Dao
interface DepartamentoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDepartamentoTask(c: Departamento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDepartamentoListTask(c: List<Departamento>)

    @Update
    fun updateDepartamentoTask(vararg c: Departamento)

    @Delete
    fun deleteDepartamentoTask(c: Departamento)

    @Query("SELECT * FROM Departamento")
    fun getDepartamentoTask(): LiveData<Departamento>

    @Query("SELECT * FROM Departamento")
    fun getDepartamento(): Departamento

    @Query("SELECT * FROM Departamento")
    fun getDepartamentoById(): LiveData<Departamento>

    @Query("DELETE FROM Departamento")
    fun deleteAll()
}