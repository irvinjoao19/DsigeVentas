package com.dsige.dsigeventas.data.local.repository

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.dsige.dsigeventas.data.local.AppDataBase
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.helper.Mensaje
import com.dsige.dsigeventas.helper.Util
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call

class AppRepoImp(private val apiService: ApiService, private val dataBase: AppDataBase) :
    AppRepository {

    override fun getUsuarioIdTask(): Int {
        return dataBase.usuarioDao().getUsuarioIdTask()
    }

    override fun getUsuario(): LiveData<Usuario> {
        return dataBase.usuarioDao().getUsuario()
    }

    override fun getUsuarioService(
        usuario: String, password: String, imei: String, version: String
    ): Observable<Usuario> {
        val u = Filtro(usuario, password, imei, version)
        val json = Gson().toJson(u)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getLogin(body)
    }

    override fun getLogout(login: String): Observable<Mensaje> {
        val u = Filtro(login)
        val json = Gson().toJson(u)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getLogout(body)
    }

    override fun insertUsuario(u: Usuario): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().insertUsuarioTask(u)
        }
    }

    override fun deleteUsuario(): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().deleteAll()
            dataBase.categoriaDao().deleteAll()
            dataBase.clienteDao().deleteAll()
            dataBase.departamentoDao().deleteAll()
            dataBase.distritoDao().deleteAll()
            dataBase.estadoDao().deleteAll()
            dataBase.formaPagoDao().deleteAll()
            dataBase.giroNegocioDao().deleteAll()
            dataBase.grupoDao().deleteAll()
            dataBase.identidadDao().deleteAll()
            dataBase.pedidoDao().deleteAll()
            dataBase.pedidoDetalleDao().deleteAll()
            dataBase.personalDao().deleteAll()
            dataBase.provinciaDao().deleteAll()
            dataBase.repartoDao().deleteAll()
            dataBase.repartoDetalleDao().deleteAll()
            dataBase.stockDao().deleteAll()
        }
    }

    override fun deleteTotal(): Completable {
        return Completable.fromAction {

        }
    }

    override fun getSync(operarioId: Int, version: String): Observable<Sync> {
        return apiService.getSync(operarioId, version)
    }

    override fun saveSync(s: Sync): Completable {
        return Completable.fromAction {

            val i: List<Identidad>? = s.identidades
            if (i != null) {
                dataBase.identidadDao().insertIdentidadListTask(i)
            }

            val d: List<Departamento>? = s.departamentos
            if (d != null) {
                dataBase.departamentoDao().insertDepartamentoListTask(d)
            }

            val p: List<Provincia>? = s.provincias
            if (p != null) {
                dataBase.provinciaDao().insertProvinciaListTask(p)
            }

            val t: List<Distrito>? = s.distritos
            if (t != null) {
                dataBase.distritoDao().insertDistritoListTask(t)
            }

            val n: List<GiroNegocio>? = s.negocios
            if (n != null) {
                dataBase.giroNegocioDao().insertGiroNegocioListTask(n)
            }
//            val st: List<Stock>? = s.productos
//            if (st != null) {
//                dataBase.stockDao().insertStockListTask(st)
//            }

            val cl: List<Cliente>? = s.clientes
            if (cl != null) {
                dataBase.clienteDao().insertClienteListTask(cl)
            }

            val pa: List<FormaPago>? = s.formaPagos
            if (pa != null) {
                dataBase.formaPagoDao().insertFormaPagoListTask(pa)
            }

            val r: List<Reparto>? = s.repartos
            if (r != null) {
                dataBase.repartoDao().insertRepartoListTask(r)
                for (re: Reparto in r) {
                    val de: List<RepartoDetalle>? = re.detalle
                    if (de != null) {
                        dataBase.repartoDetalleDao().insertRepartoDetalleListTask(de)
                    }
                }
            }

            val e: List<Estado>? = s.estados
            if (e != null) {
                dataBase.estadoDao().insertEstadoListTask(e)
            }

            val g: List<Grupo>? = s.grupos
            if (g != null) {
                dataBase.grupoDao().insertGrupoListTask(g)
            }

            val l: List<Local>? = s.locales
            if (l != null) {
                dataBase.localDao().insertLocalListTask(l)
            }
        }
    }

    override fun getPersonal(fecha: String): Observable<List<Personal>> {
        return apiService.getPersonal(fecha)
    }

    override fun getPersonal(): LiveData<List<Personal>> {
        return dataBase.personalDao().getPersonal()
    }

    override fun getCliente(search: String): LiveData<PagedList<Cliente>> {
        return dataBase.clienteDao().getCliente(search).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getCliente(): LiveData<PagedList<Cliente>> {
        return dataBase.clienteDao().getCliente().toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getCliente(s: Int, search: String): LiveData<PagedList<Cliente>> {
        return dataBase.clienteDao().getCliente(s, search).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getCliente(s: Int): LiveData<PagedList<Cliente>> {
        return dataBase.clienteDao().getCliente(s).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getClienteById(id: Int): LiveData<Cliente> {
        return dataBase.clienteDao().getClienteById(id)
    }

    override fun insertOrUpdateCliente(c: Cliente, m: Mensaje?): Completable {
        return Completable.fromAction {
            if (m != null) {
                c.identity = m.codigoRetorno
            }
            if (c.clienteId == 0) {
                dataBase.clienteDao().insertClienteTask(c)
            } else {
                dataBase.clienteDao().updateClienteTask(c)
            }
        }
    }

    override fun getDepartamentos(): LiveData<List<Departamento>> {
        return dataBase.departamentoDao().getDepartamentos()
    }

    override fun getProvinciasById(id: String): LiveData<List<Provincia>> {
        return dataBase.provinciaDao().getProvinciasById(id)
    }

    override fun getDistritosById(dId: String, pId: String): LiveData<List<Distrito>> {
        return dataBase.distritoDao().getDistritosById(dId, pId)
    }

    override fun syncProductos(localId: Int): Observable<List<Stock>> {
        return apiService.getProductos(localId)
    }

    override fun insertProductos(p: List<Stock>): Completable {
        return Completable.fromAction {
            val detalle = dataBase.pedidoDetalleDao().getPedidoActive()
            for (d: PedidoDetalle in detalle) {
                for (s: Stock in p) {
                    if (d.productoId == s.productoId) {
                        dataBase.pedidoDetalleDao()
                            .updateStockPedidoDetalle(d.pedidoDetalleId, s.stock)
                        break
                    }
                }
            }
            dataBase.stockDao().insertStockListTask(p)
        }
    }

    override fun getProductos(): LiveData<PagedList<Stock>> {
        return dataBase.stockDao().getProductos().toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getProductos(search: String): LiveData<PagedList<Stock>> {
        return dataBase.stockDao().getProductos(search).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getProductoById(id: Int): LiveData<Stock> {
        return dataBase.stockDao().getStockById(id)
    }

    override fun updateCheckPedido(s: Stock): Completable {
        return Completable.fromAction {
            dataBase.stockDao().updateStockTask(s)
        }
    }

    override fun getProductoByPedido(id: Int): LiveData<PagedList<PedidoDetalle>> {
        return dataBase.pedidoDetalleDao().getProductoByPedido(id).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun savePedidoOnline(pedidoId: Int): Completable {
        return Completable.fromAction {
            val p = dataBase.pedidoDao().getPedidoByIdTask(pedidoId)
            val stock = dataBase.stockDao().getStockSelected(true)
            for (s: Stock in stock) {
                val a = PedidoDetalle()
                a.pedidoId = pedidoId
                a.identity = p.identity
                a.localId = p.localId
                a.productoId = s.productoId
                a.codigo = s.codigoProducto
                a.nombre = s.nombreProducto
                a.descripcion = s.descripcionProducto
                a.stockMinimo = s.stock
                a.abreviaturaProducto = s.abreviaturaProducto
                a.precio1 = s.precio
                a.precio2 = s.precio2
                a.factor = s.factor
                a.active = 2
                a.precioMayMenor = s.precioMayMenor
                a.precioMayMayor = s.precioMayMayor
                a.rangoCajaHorizontal = s.rangoCajaHorizontal
                a.rangoCajaMayorista = s.rangoCajaMayorista

                if (!dataBase.pedidoDetalleDao().getProductoExits(a.pedidoId, a.productoId)) {
                    dataBase.pedidoDetalleDao().insertProductoTask(a)
                }
            }
            dataBase.stockDao().enabledStockSelected(false)
        }
    }

    override fun getPedidoDetalles(pedidoId: Int): Observable<List<PedidoDetalle>> {
        return Observable.create { e ->
            val data = dataBase.pedidoDetalleDao().getPedidoDetalleByIdTask(pedidoId)
            e.onNext(data)
            e.onComplete()
        }
    }

    override fun updateProducto(p: PedidoDetalle, t: String): Completable {
        return Completable.fromAction {
            if (t == "0") {
                val d = dataBase.pedidoDetalleDao()
                    .getVerificatePedidoDetalleByIdTask(p.pedidoDetalleId)
                dataBase.pedidoDetalleDao().updateProductoTask(d)
            } else {
                dataBase.pedidoDetalleDao().updateProductoTask(p)
            }
        }
    }

    override fun getPedidoById(id: Int): Observable<Pedido> {
        return Observable.create { e ->
            val p = dataBase.pedidoDao().getPedidoByIdTask(id)
            val d: List<PedidoDetalle>? = dataBase.pedidoDetalleDao().getPedidoById(id)
            if (d != null) {
                p.detalles = d
            }
            e.onNext(p)
            e.onComplete()
        }
    }

    override fun sendPedido(body: RequestBody): Observable<Mensaje> {
        return apiService.sendPedido(body)
    }

    override fun updatePedido(m: Mensaje): Completable {
        return Completable.fromAction {
//            dataBase.clienteDao().updateCliente(m.codigoBaseCliente, m.codigoRetornoCliente)
            dataBase.pedidoDao().updatePedidoEnabled(m.codigoBase)
            dataBase.pedidoDetalleDao().updatePedidoEnabled(m.codigoBase)
        }
    }

    /**
    0 -> "Ok"
    1 -> "Completar los productos en cantidad 0"
    2 -> "Agregar Producto"
     */
    override fun validatePedido(id: Int): Observable<Int> {
        return Observable.create { e ->
            val c = dataBase.pedidoDetalleDao().validateCountPedido(id)
            if (c == 0) {
                e.onNext(2)
            } else {
                val a = dataBase.pedidoDetalleDao().validatePedido(id)
                if (a > 0) {
                    e.onNext(1)
                } else {
                    e.onNext(0)
                }
            }
            e.onComplete()
        }
    }

    override fun getPedidoCliente(id: Int): LiveData<Pedido> {
        return dataBase.pedidoDao().getPedidoCliente(id)
    }

    override fun updateTotalPedido(
        id: Int, igv: Double, total: Double, subTotal: Double
    ): Completable {
        return Completable.fromAction {
            dataBase.pedidoDao().updateTotalPedido(id, total, subTotal)
        }
    }

    // estado : 1 -> identity , 0 clienteId
    override fun generarPedidoCliente(
        latitud: String, longitud: String, clienteId: Int
    ): Observable<Pedido> {
        return Observable.create { e ->
            val identity = dataBase.pedidoDao().getPedidoIdentity()
            val o = Pedido()
            o.pedidoId = if (identity == 0) 1 else identity + 1
            o.clienteId = clienteId
            val c =
                dataBase.clienteDao().getClienteTaskById(clienteId)
            val u = dataBase.usuarioDao().getUsuarioTask()
            o.nombreCliente = c.nombreCliente
            o.empresaId = c.empresaId
            o.tipoPersonal = c.tipoPersonal
            o.porcentajeIGV = 18.0
            o.tipoDocumento = 2
            o.almacenId = 18
            o.cuadrillaId = 1
            o.monedaId = 1
            o.puntoVentaId = 1
            o.codigoInternoCliente = c.codigoInterno
            o.personalVendedorId = u.usuarioId
            o.localId = u.localId
            o.latitud = latitud
            o.longitud = longitud
            o.direccionPedido = ""
//            dataBase.pedidoDao().insertPedidoTask(o)
//            e.onNext(o.pedidoId)
            e.onNext(o)
            e.onComplete()
        }
    }

    override fun getFormaPago(): LiveData<List<FormaPago>> {
        return dataBase.formaPagoDao().getFormaPago()
    }

    override fun getPedido(): LiveData<PagedList<Pedido>> {
        return dataBase.pedidoDao().getPedido().toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getPedido(search: String): LiveData<PagedList<Pedido>> {
        return dataBase.pedidoDao().getPedido(search).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getRepartos(): LiveData<PagedList<Reparto>> {
        return dataBase.repartoDao().getRepartos().toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getTotalReparto(id: Int): LiveData<Int> {
        return dataBase.repartoDao().getTotalReparto(id)
    }

    override fun getRepartoCount(valor: Int, id: Int): LiveData<Int> {
        return dataBase.repartoDao().getRepartoCount(valor, id)
    }

    override fun getRepartoList(): LiveData<List<Reparto>> {
        return dataBase.repartoDao().getRepartoList(8)
    }

    override fun getReparto(): LiveData<PagedList<Reparto>> {
        return dataBase.repartoDao().getReparto(8).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getReparto(s: String): LiveData<PagedList<Reparto>> {
        return dataBase.repartoDao().getReparto(8, s).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getReparto(localId: Int): LiveData<PagedList<Reparto>> {
        return dataBase.repartoDao().getReparto(8, localId).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getRepartoDistrito(d: Int): LiveData<PagedList<Reparto>> {
        return dataBase.repartoDao().getRepartoDistrito(8, d).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getReparto(localId: Int, distritoId: Int): LiveData<PagedList<Reparto>> {
        return dataBase.repartoDao().getReparto(8, localId, distritoId).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getReparto(localId: Int, s: String): LiveData<PagedList<Reparto>> {
        return dataBase.repartoDao().getReparto(8, localId, s).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getReparto(
        localId: Int,
        distritoId: Int,
        s: String
    ): LiveData<PagedList<Reparto>> {
        return dataBase.repartoDao().getReparto(8, localId, distritoId, s).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun deletePedidoDetalle(p: PedidoDetalle): Completable {
        return Completable.fromAction {
            dataBase.pedidoDetalleDao().deleteProductoTask(p)
        }
    }

    override fun getRepartoById(id: Int): LiveData<Reparto> {
        return dataBase.repartoDao().getRepartoById(id)
    }

    override fun deletePedido(p: Pedido): Completable {
        return Completable.fromAction {
            dataBase.pedidoDetalleDao().deletePedidoById(p.pedidoId)
            dataBase.pedidoDao().deletePedidoTask(p)
        }
    }

    override fun saveGpsTask(body: RequestBody): Call<Mensaje> {
        return apiService.saveGps(body)
    }

    override fun saveMovilTask(body: RequestBody): Call<Mensaje> {
        return apiService.saveMovil(body)
    }

    override fun getClienteByIdTask(id: Int): Observable<Cliente> {
        return Observable.create { e ->
            val c = dataBase.clienteDao().getClienteTaskById(id)
            e.onNext(c)
            e.onComplete()
        }
    }

    override fun sendCliente(body: RequestBody): Observable<Mensaje> {
        return apiService.sendCliente(body)
    }

    override fun updateCliente(m: Mensaje, pedidoId: Int): Completable {
        return Completable.fromAction {
            dataBase.clienteDao().updateCliente(m.codigoBase, m.codigoRetorno)
            dataBase.pedidoDao().updatePedido(pedidoId, m.codigoRetorno)
        }
    }

    override fun getDetalleRepartoById(id: Int): LiveData<PagedList<RepartoDetalle>> {
        return dataBase.repartoDetalleDao().getDetalleRepartoById(id, 1).toLiveData(
            Config(pageSize = 20, enablePlaceholders = true)
        )
    }

    override fun getEstados(): LiveData<List<Estado>> {
        return dataBase.estadoDao().getEstados()
    }

    override fun getGrupos(): LiveData<List<Grupo>> {
        return dataBase.grupoDao().getGrupos()
    }

    override fun updateReparto(re: Reparto): Completable {
        return Completable.fromAction {
            dataBase.repartoDao().updateRepartoTask(re)
        }
    }

    override fun getRepartoByIdTask(id: Int): Observable<Reparto> {
        return Observable.create { e ->
            val p = dataBase.repartoDao().getRepartoByIdTask(id)
            val d: List<RepartoDetalle>? = dataBase.repartoDetalleDao().getRepartoById(id)
            if (d != null) {
                p.detalle = d
            }
            e.onNext(p)
            e.onComplete()
        }
    }

    override fun sendUpdateReparto(body: RequestBody): Observable<Mensaje> {
        return apiService.sendUpdateReparto(body)
    }

    override fun updateRepartoDetalle(r: RepartoDetalle): Completable {
        return Completable.fromAction {
            dataBase.repartoDetalleDao().updateRepartoDetalleTask(r)
        }
    }

    override fun updateTotalReparto(repartoId: Int, total: Double): Completable {
        return Completable.fromAction {
            dataBase.repartoDao().updateTotalReparto(repartoId, total)
        }
    }

    override fun insertPersonal(t: List<Personal>): Completable {
        return Completable.fromAction {
            dataBase.personalDao().insertPersonalListTask(t)
        }
    }

    override fun getPersonalById(id: Int): LiveData<Personal> {
        return dataBase.personalDao().getPersonalById(id)
    }

    override fun getResumen(fecha: String): Observable<Resumen> {
        return apiService.getResumen(fecha)
    }

    override fun getRepartoByTipo(t: Int): LiveData<List<Reparto>> {
        return when (t) {
            0 -> dataBase.repartoDao().getRepartoList(8)
            else -> dataBase.repartoDao().getRepartoByTipo(8, t)
        }
    }

    override fun getLocales(): LiveData<List<Local>> {
        return dataBase.localDao().getLocales()
    }

    override fun getOrdenById(id: Int): Observable<Pedido> {
        return Observable.create { e ->
            val p = dataBase.pedidoDao().getPedidoByIdTask(id)
            e.onNext(p)
            e.onComplete()
        }
    }

    override fun updatePhotoCliente(clienteId: Int, nameImg: String): Completable {
        return Completable.fromAction {
            dataBase.clienteDao().updatePhotoCliente(clienteId, nameImg)
        }
    }

    override fun getClienteByDistrito(distrito: String): LiveData<List<Cliente>> {
        return dataBase.clienteDao().getClienteByDistrito(distrito)
    }

    override fun personalRepartoSearch(l: Int, s: String): LiveData<PagedList<Reparto>> {
        return when (l) {
            0 -> dataBase.repartoDao().personalRepartoSearch(8, s).toLiveData(
                Config(pageSize = 20, enablePlaceholders = true)
            )
            else -> dataBase.repartoDao().personalRepartoSearch(8, l, s).toLiveData(
                Config(pageSize = 20, enablePlaceholders = true)
            )
        }
    }

    override fun calculando(latitud: String, longitud: String): Completable {
        return Completable.fromAction {
            val m = Location("me")
            m.latitude = latitud.toDouble()
            m.longitude = longitud.toDouble()
            val repartos = dataBase.repartoDao().getRepartoTask(8)
            for (s in repartos) {
                if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                    val l1 = Location("reparto")
                    l1.latitude = s.latitud.toDouble()
                    l1.longitude = s.longitud.toDouble()
                    val distance = Util.calculationByDistance(l1, m)
                    dataBase.repartoDao().updateRepartoDistance(s.repartoId, distance)
                }
            }
        }
    }

    override fun sendCabeceraPedido(p: Pedido): Observable<Mensaje> {
        val json = Gson().toJson(p)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.sendCabeceraPedido(body)
    }

    override fun insertPedido(p: Pedido, t: Mensaje): Completable {
        return Completable.fromAction {
            p.identity = t.codigoRetorno
            dataBase.pedidoDao().insertPedidoTask(p)
        }
    }

    override fun sendDetallePedidoGroup(p: List<PedidoDetalle>): Observable<List<Mensaje>> {
        val json = Gson().toJson(p)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.sendDetallePedidoGroup(body)
    }

    override fun saveDetallePedidoGroup(t: List<Mensaje>): Completable {
        return Completable.fromAction {
            for (m: Mensaje in t) {
                dataBase.pedidoDetalleDao().updateDetallePedidoOnline(m.codigoBase, m.codigoRetorno)
            }
        }
    }

    override fun sendDetallePedido(p: PedidoDetalle): Observable<Mensaje> {
        val json = Gson().toJson(p)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.sendDetallePedido(body)
    }
}