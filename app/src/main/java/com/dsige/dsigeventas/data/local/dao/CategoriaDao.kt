package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Categoria

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategoriaTask(c: Categoria)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategoriaListTask(c: List<Categoria>)

    @Update
    fun updateCategoriaTask(vararg c: Categoria)

    @Delete
    fun deleteCategoriaTask(c: Categoria)

    @Query("SELECT * FROM Categoria")
    fun getCategoriaTask(): LiveData<Categoria>

    @Query("SELECT * FROM Categoria")
    fun getCategoria(): Categoria

    @Query("SELECT * FROM Categoria WHERE categoriaId =:id")
    fun getCategoriaByIdTask(id:Int) : Categoria

    @Query("SELECT * FROM Categoria WHERE categoriaId =:id")
    fun getCategoriaById(id: Int): LiveData<Categoria>

    @Query("DELETE FROM Categoria")
    fun deleteAll()

}