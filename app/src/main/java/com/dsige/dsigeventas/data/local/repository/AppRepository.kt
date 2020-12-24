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
        usuario: String, password: String, imei: String, version: String
    ): Observable<Usuario>

    fun getLogout(login: String): Observable<Mensaje>
    fun insertUsuario(u: Usuario): Completable
    fun deleteUsuario(): Completable
    fun deleteTotal(): Completable
    fun getSync(operarioId: Int, version: String): Observable<Sync>
    fun saveSync(s: Sync): Completable

    // TODO : Personal

    fun getPersonal(fecha: String): Observable<List<Personal>>
    fun getPersonal(): LiveData<List<Personal>>

    //  TODO : CLIENTE

    fun getCliente(): LiveData<PagedList<Cliente>>
    fun getCliente(search: String): LiveData<PagedList<Cliente>>
    fun getCliente(s: Int, search: String): LiveData<PagedList<Cliente>>
    fun getCliente(s: Int): LiveData<PagedList<Cliente>>
    fun getClienteById(id: Int): LiveData<Cliente>
    fun insertOrUpdateCliente(c: Cliente, m: Mensaje): Completable
    fun getDepartamentos(): LiveData<List<Departamento>>
    fun getProvinciasById(id: String): LiveData<List<Provincia>>
    fun getDistritosById(dId: String, pId: String): LiveData<List<Distrito>>

    // TODO : Productos
    fun syncProductos(localId: Int): Observable<List<Stock>>
    fun insertProductos(p: List<Stock>): Completable
    fun getProductos(): LiveData<PagedList<Stock>>
    fun getProductos(search: String): LiveData<PagedList<Stock>>
    fun getProductoById(id: Int): LiveData<Stock>
    fun updateCheckPedido(s: Stock): Completable
    fun getProductoByPedido(id: Int): LiveData<PagedList<PedidoDetalle>>
    fun savePedidoOnline(pedidoId: Int): Completable
    fun getPedidoDetalles(pedidoId: Int): Observable<List<PedidoDetalle>>
    fun updateProducto(p: PedidoDetalle, t: Mensaje): Completable
    fun getPedidoById(id: Int): Observable<Pedido>
    fun sendPedido(body: RequestBody): Observable<Mensaje>
    fun updatePedido(m: Mensaje): Completable
    fun validatePedido(id: Int): Observable<Int>
    fun getPedidoCliente(id: Int): LiveData<Pedido>
    fun updateTotalPedido(id: Int, igv: Double, total: Double, subTotal: Double): Completable
    fun generarPedidoCliente(latitud: String, longitud: String, clienteId: Int): Observable<Pedido>
    fun getFormaPago(): LiveData<List<FormaPago>>
    fun getPedido(): LiveData<PagedList<Pedido>>
    fun getPedido(search: String): LiveData<PagedList<Pedido>>
    fun getRepartos(): LiveData<PagedList<Reparto>>
    fun getTotalReparto(id: Int): LiveData<Int>
    fun getRepartoCount(valor: Int, id: Int): LiveData<Int>
    fun getRepartoList(): LiveData<List<Reparto>>
    fun getReparto(): LiveData<PagedList<Reparto>>
    fun getReparto(s: String): LiveData<PagedList<Reparto>>
    fun getReparto(localId: Int): LiveData<PagedList<Reparto>>
    fun getRepartoDistrito(d: Int): LiveData<PagedList<Reparto>>
    fun getReparto(localId: Int, distritoId: Int): LiveData<PagedList<Reparto>>
    fun getReparto(localId: Int, s: String): LiveData<PagedList<Reparto>>
    fun getReparto(localId: Int, distritoId: Int, s: String): LiveData<PagedList<Reparto>>
    fun deletePedidoDetalleOnline(p: PedidoDetalle): Observable<Mensaje>
    fun deletePedidoDetalle(p: PedidoDetalle): Completable
    fun getRepartoById(id: Int): LiveData<Reparto>
    fun deletePedido(p: Pedido): Completable

    fun saveGpsTask(body: RequestBody): Call<Mensaje>
    fun saveMovilTask(body: RequestBody): Call<Mensaje>
    fun getClienteByIdTask(id: Int): Observable<Cliente>
    fun sendCliente(c: Cliente): Observable<Mensaje>
    fun updateCliente(m: Mensaje, pedidoId: Int): Completable
    fun getDetalleRepartoById(id: Int): LiveData<PagedList<RepartoDetalle>>

    fun getEstados(): LiveData<List<Estado>>

    fun getGrupos(): LiveData<List<Grupo>>

    fun updateReparto(re: Reparto): Completable
    fun getRepartoByIdTask(id: Int): Observable<Reparto>
    fun sendUpdateReparto(body: RequestBody): Observable<Mensaje>
    fun updateRepartoDetalle(r: RepartoDetalle): Completable
    fun updateTotalReparto(repartoId: Int, total: Double): Completable
    fun insertPersonal(t: List<Personal>): Completable
    fun getPersonalById(id: Int): LiveData<Personal>
    fun getResumen(fecha: String): Observable<Resumen>
    fun getRepartoByTipo(t: Int): LiveData<List<Reparto>>

    fun getLocales(): LiveData<List<Local>>
    fun getOrdenById(id: Int): Observable<Pedido>
    fun updatePhotoCliente(clienteId: Int, nameImg: String): Completable
    fun getClienteByDistrito(distrito: String): LiveData<List<Cliente>>
    fun personalRepartoSearch(l: Int, s: String): LiveData<PagedList<Reparto>>
    fun calculando(latitud: String, longitud: String): Completable

    // online
    fun sendCabeceraPedido(p: Pedido): Observable<Mensaje>
    fun insertPedido(p: Pedido, t: Mensaje): Completable
    fun sendDetallePedidoGroup(p: List<PedidoDetalle>): Observable<List<Mensaje>>
    fun saveDetallePedidoGroup(t: List<Mensaje>): Completable
    fun sendDetallePedido(p: PedidoDetalle): Observable<Mensaje>

    fun verificateDistrito(c: Cliente): Observable<Cliente>
    fun clearProductos(): Completable
    fun deletePedidoOnline(p: Pedido): Observable<Mensaje>

    //todo reporte vendedor
    fun syncReporteVenta(id: Int): Observable<List<VentaVendedor>>
    fun syncReporteSupervisor(id: Int): Observable<List<VentaSupervisor>>
    fun syncReporteMes(id: Int): Observable<List<VentaMes>>

    fun deleteReporteUbicacion(): Completable
    fun syncReporteUbicacion(id: Int): Observable<List<VentaUbicacion>>
    fun insertVentaUbicacion(t: List<VentaUbicacion>): Completable
    fun getVentaUbicacion(): LiveData<List<VentaUbicacion>>
    fun getVentaUbicacionById(id: Int): LiveData<VentaUbicacion>

}