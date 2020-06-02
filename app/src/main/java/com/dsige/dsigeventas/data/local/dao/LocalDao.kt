package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.Local

@Dao
interface LocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalTask(c: Local)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalListTask(c: List<Local>)

    @Update
    fun updateLocalTask(vararg c: Local)

    @Delete
    fun deleteLocalTask(c: Local)

    @Query("SELECT * FROM Local")
    fun getLocalTask(): LiveData<Local>

    @Query("SELECT * FROM Local")
    fun getLocal(): Local

    @Query("SELECT * FROM Local WHERE localId =:id")
    fun getLocalByIdTask(id:Int) : Local

    @Query("SELECT * FROM Local WHERE localId =:id")
    fun getLocalById(id: Int): LiveData<Local>

    @Query("DELETE FROM Local")
    fun deleteAll()

    @Query("SELECT * FROM Local")
    fun getLocales(): LiveData<List<Local>>
}