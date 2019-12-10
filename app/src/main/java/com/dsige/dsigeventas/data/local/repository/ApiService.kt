package com.dsige.dsigeventas.data.local.repository

import com.dsige.dsigeventas.data.local.model.MapPrincipal
import com.dsige.dsigeventas.data.local.model.Sync
import com.dsige.dsigeventas.data.local.model.Usuario
import com.dsige.dsigeventas.helper.Mensaje
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @Headers("Cache-Control: no-cache")
    @POST("Login")
    fun getLogin(@Body body: RequestBody): Observable<Usuario>

    @Headers("Cache-Control: no-cache")
    @GET("Sync")
    fun getSync(@Query("operarioId") operarioId: Int, @Query("version") version: String): Observable<Sync>

    @Headers("Cache-Control: no-cache")
    @POST("Save")
    fun save(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveGps")
    fun saveGps(@Body body: RequestBody): Call<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveMovil")
    fun saveMovil(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SaveInspeccion")
    fun saveInspection(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("SavePedido")
    fun sendPedido(@Body body: RequestBody): Observable<Mensaje>

    @GET("/maps/api/directions/json?")
    fun getDirection(
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("mode") mode: String?,
        @Query("alternatives") alternatives: Boolean,
        @Query("key") key: String?
    ): Call<MapPrincipal>

    @Headers("Cache-Control: no-cache")
    @POST("SaveCliente")
    fun sendCliente(@Body body: RequestBody): Observable<Mensaje>

    @Headers("Cache-Control: no-cache")
    @POST("UpdateReparto")
    fun sendUpdateReparto(@Body body: RequestBody): Observable<Mensaje>
}