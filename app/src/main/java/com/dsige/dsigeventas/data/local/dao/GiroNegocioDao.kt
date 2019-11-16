package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.GiroNegocio

@Dao
interface GiroNegocioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGiroNegocioTask(c: GiroNegocio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGiroNegocioListTask(c: List<GiroNegocio>)

    @Update
    fun updateGiroNegocioTask(vararg c: GiroNegocio)

    @Delete
    fun deleteGiroNegocioTask(c: GiroNegocio)

    @Query("SELECT * FROM GiroNegocio")
    fun getGiroNegocioTask(): LiveData<GiroNegocio>

    @Query("SELECT * FROM GiroNegocio")
    fun getGiroNegocio(): GiroNegocio

    @Query("SELECT * FROM GiroNegocio")
    fun getGiroNegocioById(): LiveData<GiroNegocio>

    @Query("DELETE FROM GiroNegocio")
    fun deleteAll()
}