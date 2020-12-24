package com.dsige.dsigeventas.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.dsigeventas.data.local.model.VentaMes
import com.dsige.dsigeventas.data.local.model.VentaSupervisor
import com.dsige.dsigeventas.data.local.model.VentaUbicacion
import com.dsige.dsigeventas.data.local.model.VentaVendedor
import com.dsige.dsigeventas.data.local.repository.ApiError
import com.dsige.dsigeventas.data.local.repository.AppRepository
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import javax.inject.Inject

class ReporteViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError = MutableLiveData<String>()
    val mensajeSuccess = MutableLiveData<String>()
    val reporte: MutableLiveData<List<VentaVendedor>> = MutableLiveData()
    val reporteSupervisor: MutableLiveData<List<VentaSupervisor>> = MutableLiveData()
    val reporteMes: MutableLiveData<List<VentaMes>> = MutableLiveData()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun clearReporteMes() {
        reporteMes.value = null
    }

    fun syncReporteVenta(id: Int) {
        roomRepository.syncReporteVenta(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<VentaVendedor>> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: List<VentaVendedor>) {
                    reporte.value = t
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val body = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            mensajeError.postValue(e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.toString())
                    }
                }
            })
    }

    fun syncReporteSupervisor(id: Int) {
        roomRepository.syncReporteSupervisor(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<VentaSupervisor>> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: List<VentaSupervisor>) {
                    reporteSupervisor.value = t
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val body = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            mensajeError.postValue(e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.toString())
                    }
                }
            })
    }

    fun syncReporteMes(id: Int) {
        roomRepository.syncReporteMes(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<VentaMes>> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: List<VentaMes>) {
                    reporteMes.value = t
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val body = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(body!!)
                            mensajeError.postValue(error!!.Message)
                        } catch (e1: IOException) {
                            mensajeError.postValue(e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.toString())
                    }
                }
            })
    }

    fun syncReporteUbicacion(id: Int) {
        roomRepository.deleteReporteUbicacion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    roomRepository.syncReporteUbicacion(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<VentaUbicacion>> {
                            override fun onComplete() {}
                            override fun onSubscribe(d: Disposable) {}

                            override fun onNext(t: List<VentaUbicacion>) {
                                insertVentaUbicacion(t)
                            }

                            override fun onError(e: Throwable) {
                                if (e is HttpException) {
                                    val body = e.response().errorBody()
                                    try {
                                        val error = retrofit.errorConverter.convert(body!!)
                                        mensajeError.postValue(error!!.Message)
                                    } catch (e1: IOException) {
                                        mensajeError.postValue(e1.toString())
                                    }
                                } else {
                                    mensajeError.postValue(e.toString())
                                }
                            }
                        })
                }
            })
    }

    private fun insertVentaUbicacion(t: List<VentaUbicacion>) {
        roomRepository.insertVentaUbicacion(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    fun getVentaUbicacion(): LiveData<List<VentaUbicacion>> {
        return roomRepository.getVentaUbicacion()
    }

    fun getVentaUbicacionById(id: Int): LiveData<VentaUbicacion> {
        return roomRepository.getVentaUbicacionById(id)
    }
}