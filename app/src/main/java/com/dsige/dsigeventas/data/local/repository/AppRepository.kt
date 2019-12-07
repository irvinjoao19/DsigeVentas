package com.dsige.dsigeventas.data.local.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.helper.Mensaje
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call

interface AppRepository {

    fun getUsuarioIdTask(): Int

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

    fun getCliente(search: String): LiveData<PagedList<Cliente>>

    fun getCliente(d: Int, p: Int, s: Int, search: String): LiveData<PagedList<Cliente>>

    fun getClienteById(id: Int): LiveData<Cliente>

    fun insertOrUpdateCliente(c: Cliente): Completable

    fun getDepartamentos(): LiveData<List<Departamento>>

    fun getProvinciasById(id: String): LiveData<List<Provincia>>

    fun getDistritosById(dId: String, pId: String): LiveData<List<Distrito>>

    // TODO : Productos

    fun getProductos(): LiveData<PagedList<Stock>>

    fun getProductos(search: String): LiveData<PagedList<Stock>>

    fun getProductoById(id: Int): LiveData<Stock>

    fun updateCheckPedido(s: Stock): Completable

    fun getProductoByPedido(id: Int): LiveData<PagedList<PedidoDetalle>>

    fun savePedido(pedidoId: Int): Completable

    fun updateProducto(p: PedidoDetalle): Completable

    fun getPedidoById(id: Int): Observable<Pedido>

    fun sendPedido(body: RequestBody): Observable<Mensaje>

    fun updatePedido(m: Mensaje): Completable

    fun validatePedido(id: Int): Observable<Int>

    fun getPedidoCliente(id: Int): LiveData<Pedido>

    fun updateTotalPedido(id: Int, igv: Double, total: Double, subTotal: Double): Completable

    fun generarPedidoCliente(latitud: String, longitud: String, clienteId: Int): Observable<Int>

    fun getFormaPago(): LiveData<List<FormaPago>>

    fun getPedido(): LiveData<PagedList<Pedido>>

    fun getPedido(search: String): LiveData<PagedList<Pedido>>

    fun getRepartos(): LiveData<PagedList<Reparto>>

    fun getMapReparto(): Observable<List<Reparto>>

    fun deletePedidoDetalle(p: PedidoDetalle): Completable

    fun getRepartoById(id: Int): LiveData<Reparto>

    fun deletePedido(p: Pedido): Completable

    fun saveGpsTask(body: RequestBody): Call<Mensaje>

    fun saveMovil(body: RequestBody): Observable<Mensaje>

    fun validateCliente(id: Int): Observable<Int>

    fun getClienteByIdTask(id: Int): Observable<Cliente>

    fun sendCliente(body: RequestBody): Observable<Mensaje>

    fun updateCliente(m: Mensaje, pedidoId: Int): Completable

    fun getDetalleRepartoById(id: Int): LiveData<PagedList<RepartoDetalle>>
}