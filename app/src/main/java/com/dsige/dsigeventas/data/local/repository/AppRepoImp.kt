package com.dsige.dsigeventas.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.dsige.dsigeventas.data.local.AppDataBase
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.helper.Util
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.ArrayList

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

            val productos = ArrayList<Producto>()
            for (i in 0..10) {
                val p = Producto()
                p.productoId = i
                p.categoriaId = 1
                p.codigo = "1234567"
                p.codigoBarra = "1234567"
                p.pedidoId = 1
                p.nombre = String.format("Mantequilla%s", i)
                p.descripcion = String.format("Descripcion%s", i)
                p.precioCompra = 10.5
                p.precioVenta = 11.0
                p.unidadMedida = 0.0
                p.abreviaturaProducto = String.format("MA%s", i)
                p.urlFoto = String.format("mantequilla%s.jpg", i)
                p.peso = 100.0
                p.stockMinimo = 10
                p.estado = 1
                p.fecha = Util.getFechaActual()
                p.subTotal = 0.0
                productos.add(p)
            }

            val pe = Pedido()
            pe.pedidoId = 1
            pe.usuarioId = 1
            pe.total = 0.0

            dataBase.pedidoDao().insertPedidoTask(pe)

//            val a = Categoria("Linea 1", productos, 1)
//            dataBase.categoriaDao().insertCategoriaTask(a)

            for (p in productos) {
                dataBase.productoDao().insertProductoTask(p)
            }
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
}