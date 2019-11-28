package com.dsige.dsigeventas.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.dsige.dsigeventas.data.local.AppDataBase
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.helper.Mensaje
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody

class AppRepoImp(private val apiService: ApiService, private val dataBase: AppDataBase) :
    AppRepository {

    override fun getUsuario(): LiveData<Usuario> {
        return dataBase.usuarioDao().getUsuario()
    }

    override fun getUsuarioService(
        usuario: String,
        password: String,
        imei: String,
        version: String
    ): Observable<Usuario> {
        val u = Filtro(usuario, password, imei, version)
        val json = Gson().toJson(u)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        return apiService.getLogin(body)
    }

    override fun insertUsuario(u: Usuario): Completable {
        return Completable.fromAction {
            dataBase.usuarioDao().insertUsuarioTask(u)
        }
    }

    override fun deleteUsuario(): Completable {
        return Completable.fromAction { dataBase.usuarioDao().deleteAll() }
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

            val st: List<Stock>? = s.productos
            if (st != null) {
                dataBase.stockDao().insertStockListTask(st)
            }

            val cl: List<Cliente>? = s.clientes
            if (cl != null) {
                dataBase.clienteDao().insertClienteListTask(cl)
            }
        }
    }

    override fun populatPersonal(): LiveData<List<Personal>> {
        return dataBase.personalDao().getPersonal()
    }

    override fun getCliente(): LiveData<PagedList<Cliente>> {
        return dataBase.clienteDao().getCliente().toLiveData(
            Config(
                pageSize = 20,
                enablePlaceholders = true
            )
        )
    }

    override fun getCliente(
        d: Int, p: Int, s: Int, search: String
    ): LiveData<PagedList<Cliente>> {
        return dataBase.clienteDao().getCliente(d, p, s, search).toLiveData(
            Config(
                pageSize = 20,
                enablePlaceholders = true
            )
        )
    }

    override fun getClienteById(id: Int): LiveData<Cliente> {
        return dataBase.clienteDao().getClienteById(id)
    }

    override fun insertOrUpdateCliente(c: Cliente): Completable {
        return Completable.fromAction {
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

    override fun getProductos(): LiveData<PagedList<Stock>> {
        return dataBase.stockDao().getProductos().toLiveData(
            Config(
                pageSize = 20,
                enablePlaceholders = true
            )
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
            Config(
                pageSize = 20,
                enablePlaceholders = true
            )
        )
    }

    override fun savePedido(pedidoId: Int): Completable {
        return Completable.fromAction {
            val stock = dataBase.stockDao().getStockSelected(true)
//            val list = ArrayList<Producto>()
            for (s: Stock in stock) {
                val a = PedidoDetalle()
                a.pedidoId = pedidoId
                a.productoId = s.productoId
                a.codigo = s.codigoProducto
                a.nombre = s.nombreProducto
                a.descripcion = s.descripcionProducto
                a.stockMinimo = s.stock
                a.precioCompra = s.precio
                a.abreviaturaProducto = s.abreviaturaProducto

                if (!dataBase.pedidoDetalleDao().getProductoExits(a.pedidoId, a.productoId)) {
                    dataBase.pedidoDetalleDao().insertProductoTask(a)
                }
//                list.add(a)
            }
//            dataBase.productoDao().insertProductoListTask(list)
        }
    }

    override fun updateProducto(p: PedidoDetalle): Completable {
        return Completable.fromAction {
            dataBase.pedidoDetalleDao().updateProductoTask(p)
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
            dataBase.pedidoDao().updatePedidoEnabled(m.codigoBase)
            dataBase.stockDao().enabledStockSelected(false)
        }
    }

    override fun validatePedido(id: Int): Observable<Boolean> {
        return Observable.create { e ->
            val a = dataBase.pedidoDetalleDao().validatePedido(id)
            if (a > 0) {
                e.onNext(false)
            } else {
                e.onNext(true)
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

    override fun generarPedidoCliente(clienteId: Int): Completable {
        return Completable.fromAction {
            val p = dataBase.pedidoDao().getPedidoById(clienteId)
            if (!p) {
                val o = Pedido()
                o.pedidoId = clienteId
                o.clienteId = clienteId
                val c = dataBase.clienteDao().getClienteTaskById(clienteId)
                o.nombreCliente = c.nombreCliente
                o.empresaId = c.empresaId
                o.porcentajeIGV = 18.0
                o.tipoDocumento = 2
                o.almacenId = 18
                o.cuadrillaId = 1
                o.monedaId = 1
                o.puntoVentaId = 1
                o.codigoInternoCliente = c.codigoInterno
                o.personalVendedorId = dataBase.usuarioDao().getUsuarioId()
                dataBase.pedidoDao().insertPedidoTask(o)
            }
        }
    }
}