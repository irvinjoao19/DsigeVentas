package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Personal

@Dao
interface PersonalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonalTask(c: Personal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonalListTask(c: List<Personal>)

    @Update
    fun updatePersonalTask(vararg c: Personal)

    @Delete
    fun deletePersonalTask(c: Personal)

    @Query("SELECT * FROM Personal")
    fun getPersonal(): LiveData<List<Personal>>

    @Query("SELECT * FROM Personal WHERE personalId =:id")
    fun getPersonalById(id: Int): LiveData<Personal>

    @Query("SELECT * FROM Personal WHERE PersonalId =:id")
    fun getPersonalTaskById(id: Int): Personal

    @Query("DELETE FROM Personal")
    fun deleteAll()
}