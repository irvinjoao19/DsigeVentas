package com.dsige.dsigeventas.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.local.repository.ApiError
import com.dsige.dsigeventas.data.local.repository.AppRepository
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ClienteViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getCliente(): LiveData<PagedList<Cliente>> {
        return roomRepository.getCliente()
    }

    fun getClienteById(id: Int): LiveData<Cliente> {
        return roomRepository.getClienteById(id)
    }

    fun insertOrUpdateCliente(c: Cliente) {
        roomRepository.insertOrUpdateCliente(c)
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
}