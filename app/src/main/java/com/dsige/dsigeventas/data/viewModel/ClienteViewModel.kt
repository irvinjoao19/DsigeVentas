package com.dsige.dsigeventas.data.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.data.local.repository.ApiError
import com.dsige.dsigeventas.data.local.repository.AppRepository
import com.dsige.dsigeventas.helper.Mensaje
import com.google.gson.Gson
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ClienteViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    val cliente: MutableLiveData<Cliente> = MutableLiveData()
    val search: MutableLiveData<String> = MutableLiveData()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getFormaPago(): LiveData<List<FormaPago>> {
        return roomRepository.getFormaPago()
    }

    fun getDepartamentos(): LiveData<List<Departamento>> {
        return roomRepository.getDepartamentos()
    }

    fun getProvinciasById(id: String): LiveData<List<Provincia>> {
        return roomRepository.getProvinciasById(id)
    }

    fun getDistritosById(dId: String, pId: String): LiveData<List<Distrito>> {
        return roomRepository.getDistritosById(dId, pId)
    }

    fun getCliente(): LiveData<PagedList<Cliente>> {
        return Transformations.switchMap(search) { input ->
            if (input == null || input.isEmpty()) {
                roomRepository.getCliente()
            } else {
                val f = Gson().fromJson(search.value, Filtro::class.java)
                if (f.departamentoId.isEmpty() || f.provinciaId.isEmpty() || f.distritoId.isEmpty()) {
                    if (f.search.isNotEmpty()) {
                        roomRepository.getCliente(String.format("%s%s%s", "%", f.search, "%"))
                    } else {
                        roomRepository.getCliente()
                    }
                } else {
                    if (f.search.isEmpty()) {
                        roomRepository.getCliente(
                            f.departamentoId.toInt(), f.provinciaId.toInt(), f.distritoId.toInt()
                        )
                    } else {
                        roomRepository.getCliente(
                            f.departamentoId.toInt(), f.provinciaId.toInt(), f.distritoId.toInt(),
                            String.format("%s%s%s", "%", f.search, "%")
                        )
                    }
                }
            }
        }
    }

    fun getClienteById(id: Int): LiveData<Cliente> {
        return roomRepository.getClienteById(id)
    }

    fun setCliente(c: Cliente) {
        cliente.value = c
    }

    fun validateCliente(c: Cliente,tipo:Int) {
        if (c.tipo.isEmpty()) {
            mensajeError.value = "Seleccione tipo"
            return
        }

        if (c.documento.isEmpty()) {
            mensajeError.value = "Ingrese documento"
            return
        }

        if (c.tipo == "Natural"){
            if (c.documento.length != 8) {
                mensajeError.value = "Se requiere de 8 digitos"
                return
            }
        }

        if (c.tipo == "Juridico"){
            if (c.documento.length != 11) {
                mensajeError.value = "Se requiere 11 digitos si el tipo es Juridico"
                return
            }
        }

        if (c.nombreCliente.isEmpty()) {
            mensajeError.value = "Ingrese Nombre"
            return
        }
        if (c.nombreGiroNegocio.isEmpty()) {
            mensajeError.value = "Seleccione forma de pago"
            return
        }
        if (c.nombreDepartamento.isEmpty()) {
            mensajeError.value = "Seleccione departamento"
            return
        }
        if (c.nombreDistrito.isEmpty()) {
            mensajeError.value = "Seleccione distrito"
            return
        }
        if (c.direccion.isEmpty()) {
            mensajeError.value = "Ingrese dirección"
            return
        }
        if (c.nroCelular.isEmpty()) {
            mensajeError.value = "Ingrese nro de celular"
            return
        }
        if (c.email.isEmpty()) {
            mensajeError.value = "Ingrese email"
            return
        }

        if(tipo == 1)
            sendCliente(c)
        else
            insertOrUpdateCliente(c,null)
    }

    private fun insertOrUpdateCliente(c: Cliente,m:Mensaje?) {
        roomRepository.insertOrUpdateCliente(c,m)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    if (c.clienteId == 0) {
                        mensajeSuccess.value = "Cliente Registrado"
                    } else {
                        mensajeSuccess.value = "Cliente Actualizado"
                    }
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun sendCliente(c:Cliente) {
        val json = Gson().toJson(c)
        Log.i("TAG", json)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        roomRepository.sendCliente(body)
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje>{
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Mensaje) {
                    insertOrUpdateCliente(c,t)
                }

                override fun onError(e: Throwable) {

                }
            })
    }
}