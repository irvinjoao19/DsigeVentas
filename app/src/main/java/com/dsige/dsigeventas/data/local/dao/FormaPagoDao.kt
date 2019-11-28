package com.dsige.dsigeventas.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.dsigeventas.data.local.model.FormaPago

@Dao
interface FormaPagoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormaPagoTask(c: FormaPago)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormaPagoListTask(c: List<FormaPago>)

    @Update
    fun updateFormaPagoTask(vararg c: FormaPago)

    @Delete
    fun deleteFormaPagoTask(c: FormaPago)

    @Query("SELECT * FROM FormaPago")
    fun getFormaPagoTask(): LiveData<FormaPago>

    @Query("SELECT * FROM FormaPago")
    fun getFormaPago(): LiveData<List<FormaPago>>

    @Query("SELECT * FROM FormaPago WHERE formaPagoId=:id")
    fun getFormaPagosById(id: Int): LiveData<List<FormaPago>>

    @Query("DELETE FROM FormaPago")
    fun deleteAll()
}