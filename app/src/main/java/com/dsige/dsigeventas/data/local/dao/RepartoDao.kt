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

    @Query("SELECT * FROM Reparto WHERE estado =:e ORDER BY distancia ASC LIMIT 24")
    fun getRepartoList(e: Int): LiveData<List<Reparto>>

    @Query("SELECT * FROM Reparto WHERE estado =:e")
    fun getReparto(e: Int): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e AND apellidoNombreCliente LIKE :s")
    fun getReparto(e: Int, s: String): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e AND localId =:l")
    fun getReparto(e: Int, l: Int): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e AND localId =:l AND distritoId =:d")
    fun getReparto(e: Int, l: Int, d: Int): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e AND localId =:l AND apellidoNombreCliente LIKE :s")
    fun getReparto(e: Int, l: Int, s: String): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e AND localId =:l AND distritoId =:d AND apellidoNombreCliente LIKE :s")
    fun getReparto(e: Int, l: Int, d: Int, s: String): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e AND localId =:t ORDER BY distancia ASC LIMIT 24")
    fun getRepartoByTipo(e: Int, t: Int): LiveData<List<Reparto>>

    @Query("SELECT count(*) FROM Reparto WHERE localId =:id")
    fun getTotalReparto(id: Int): LiveData<Int>

    @Query("SELECT count(*) FROM Reparto WHERE estado =:e AND localId =:id")
    fun getRepartoCount(e: Int, id: Int): LiveData<Int>

    @Query("UPDATE Reparto SET subTotal=:total WHERE repartoId=:id")
    fun updateTotalReparto(id: Int, total: Double)

    @Query("SELECT * FROM Reparto WHERE estado =:e AND distritoId =:d")
    fun getRepartoDistrito(e: Int, d: Int): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e AND localId =:l AND apellidoNombreCliente LIKE :s")
    fun personalRepartoSearch(e: Int, l: Int, s: String): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e AND apellidoNombreCliente LIKE :s")
    fun personalRepartoSearch(e: Int, s: String): DataSource.Factory<Int, Reparto>

    @Query("SELECT * FROM Reparto WHERE estado =:e")
    fun getRepartoTask(e: Int): List<Reparto>

    @Query("UPDATE Reparto SET distancia =:d  WHERE repartoId =:id")
    fun updateRepartoDistance(id: Int, d: Double)

}