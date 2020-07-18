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
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepartoViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()
    val tipo: MutableLiveData<Int> = MutableLiveData()
    val search: MutableLiveData<String> = MutableLiveData()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getRepartos(): LiveData<PagedList<Reparto>> {
        return roomRepository.getRepartos()
    }

    fun getRepartoByLocal(id: Int): LiveData<List<Reparto>> {
        return roomRepository.getRepartoByTipo(id)
    }

    fun getListReparto(): LiveData<PagedList<Reparto>> {
        return Transformations.switchMap(search) { input ->
            if (input == null) {
                roomRepository.getReparto()
            } else {
                val f = Gson().fromJson(input, Filtro::class.java)
                if (f.localId == 0 || f.distritoRId == 0) {
                    if (f.search.isNotEmpty()) {
                        roomRepository.getReparto(String.format("%s%s%s", "%", f.search, "%"))
                    } else {
                        roomRepository.getReparto()
                    }
                } else {
                    if (f.search.isEmpty()) {
                        roomRepository.getReparto(f.localId, f.distritoRId)
                    } else {
                        roomRepository.getReparto(
                            f.localId, f.distritoRId, String.format("%s%s%s", "%", f.search, "%")
                        )
                    }
                }
            }
        }
    }

    fun getReparto(): LiveData<List<Reparto>> {
        return Transformations.switchMap(tipo) { input ->
            if (input == 0 || input == null) {
                roomRepository.getRepartoList()
            } else {
                roomRepository.getRepartoByTipo(input)
            }
        }
    }

    fun getTotalReparto(): LiveData<Int> {
        return roomRepository.getTotalReparto()
    }

    fun getTotalEntregado(): LiveData<Int> {
        return roomRepository.getRepartoCount(31)
    }

    fun getTotalDevuelto(): LiveData<Int> {
        return roomRepository.getRepartoCount(30)
    }

    fun getTotalParciales(): LiveData<Int> {
        return roomRepository.getRepartoCount(32)
    }

    fun getRepartoById(id: Int): LiveData<Reparto> {
        return roomRepository.getRepartoById(id)
    }

    fun getDetalleRepartoById(id: Int): LiveData<PagedList<RepartoDetalle>> {
        return roomRepository.getDetalleRepartoById(id)
    }

    fun getEstados(): LiveData<List<Estado>> {
        return roomRepository.getEstados()
    }

    fun getGrupos(): LiveData<List<Grupo>> {
        return roomRepository.getGrupos()
    }

    fun updateReparto(tipo: Int, re: Reparto) {
        roomRepository.updateReparto(re)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    if (tipo == 1) {
                        sendUpdateReparto(re.repartoId)
                    }
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun sendUpdateReparto(id: Int) {
        val pedidos: Observable<Reparto> = roomRepository.getRepartoByIdTask(id)
        pedidos.flatMap { a ->
            val json = Gson().toJson(a)
            Log.i("TAG", json)
            val body =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
            Observable.zip(
                Observable.just(a), roomRepository.sendUpdateReparto(body),
                BiFunction<Reparto, Mensaje, Mensaje> { _, mensaje ->
                    mensaje
                })
        }.subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {

                override fun onSubscribe(d: Disposable) {
                    Log.i("TAG", d.toString())
                }

                override fun onNext(m: Mensaje) {
                    Log.i("TAG", "RECIBIENDO LOS DATOS")
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val body = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error.Message)
                        } catch (e1: IOException) {
                            mensajeError.postValue(e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.toString())
                    }
                }

                override fun onComplete() {
                    mensajeSuccess.postValue("ENVIADO")
                }
            })
    }

    fun updateRepartoDetalle(r: RepartoDetalle) {
        roomRepository.updateRepartoDetalle(r)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }

            })
    }

    fun updateTotalReparto(repartoId: Int, total: Double) {
        roomRepository.updateTotalReparto(repartoId, total)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }

            })
    }

    fun getLocales(): LiveData<List<Local>> {
        return roomRepository.getLocales()
    }
}