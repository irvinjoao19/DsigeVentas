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
                if (f.distritoId.isEmpty()) {
                    if (f.search.isNotEmpty()) {
                        roomRepository.getCliente(String.format("%s%s%s", "%", f.search, "%"))
                    } else {
                        roomRepository.getCliente()
                    }
                } else {
                    if (f.search.isEmpty()) {
                        roomRepository.getCliente(f.distritoId.toInt())
                    } else {
                        roomRepository.getCliente(
                            f.distritoId.toInt(), String.format("%s%s%s", "%", f.search, "%")
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

    fun validateCliente(c: Cliente, tipo: Int) {
        if (c.tipo.isEmpty()) {
            mensajeError.value = "Seleccione tipo"
            return
        }

        if (c.documento.isEmpty()) {
            mensajeError.value = "Ingrese documento"
            return
        }

        if (c.tipo == "Natural") {
            if (c.documento.length != 8) {
                mensajeError.value = "Se requiere de 8 digitos"
                return
            }
        }

        if (c.tipo == "Juridico") {
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

        if (c.distritoId == 0) {
            verificateDistrito(c)
        } else {
            sendCliente(c)
        }
    }

    private fun verificateDistrito(c: Cliente) {
        roomRepository.verificateDistrito(c)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Cliente> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: Cliente) {
                    sendCliente(t)
                }
            })
    }

    private fun insertOrUpdateCliente(c: Cliente, m: Mensaje) {
        roomRepository.insertOrUpdateCliente(c, m)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    if (c.clienteId == 0) {
                        mensajeSuccess.value = "Cliente Registrado"
                    } else {
                        mensajeSuccess.value = "Cliente Actualizado"
                    }
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    private fun sendCliente(c: Cliente) {
        roomRepository.sendCliente(c)
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Mensaje) {
                    insertOrUpdateCliente(c, t)
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun updatePhotoCliente(clienteId: Int, nameImg: String) {
        roomRepository.updatePhotoCliente(clienteId, nameImg)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = "Cliente Actualizado"

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun personalSearch(s: String): LiveData<PagedList<Cliente>> {
        return roomRepository.getCliente(String.format("%s%s%s", "%", s, "%"))
    }

    fun getClienteByDistrito(s: String): LiveData<List<Cliente>> {
        return roomRepository.getClienteByDistrito(String.format("%s%s%s", "%", s, "%"))
    }
}