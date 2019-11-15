package com.dsige.dsigeventas.data.local.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.dsige.dsigeventas.data.local.model.*
import io.reactivex.Completable
import io.reactivex.Observable

interface AppRepository {

    fun getUsuario(): LiveData<Usuario>

    fun getUsuarioService(
        usuario: String,
        password: String,
        imei: String,
        version: String
    ): Observable<Usuario>

    fun insertUsuario(u: Usuario): Completable

    fun deleteUsuario(): Completable

    fun deleteTotal(): Completable

    fun getSync(operarioId: Int, version: String): Observable<Sync>

    fun saveSync(s: Sync): Completable

    // TODO : Personal

    fun populatPersonal(): LiveData<List<Personal>>


    //  TODO : CLIENTE

    fun getCliente(): LiveData<PagedList<Cliente>>

    fun getClienteById(id: Int): LiveData<Cliente>

    fun insertOrUpdateCliente(c: Cliente): Completable
}