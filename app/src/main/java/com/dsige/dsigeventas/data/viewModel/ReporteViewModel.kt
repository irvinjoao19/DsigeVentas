package com.dsige.dsigeventas.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsige.dsigeventas.data.local.model.*
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
    val reporteVendedor: MutableLiveData<List<VentaVendedor>> = MutableLiveData()
    val reporteSupervisor: MutableLiveData<List<VentaSupervisor>> = MutableLiveData()
    val reporteMes: MutableLiveData<List<VentaMes>> = MutableLiveData()
    val reporteCabecera: MutableLiveData<VentaCabecera> = MutableLiveData()
    val reporteAdmin: MutableLiveData<List<VentaAdmin>> = MutableLiveData()
    val reporteAdminVendedor: MutableLiveData<List<VentaAdminVendedor>> = MutableLiveData()

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun clearReporteMes() {
        reporteMes.value = null
    }

    fun clearReporteAdmin() {
        reporteAdmin.value = null
    }

    fun clearReporteAdminVendedor() {
        reporteAdminVendedor.value = null
    }

    fun syncReporteVendedor(id: Int) {
        roomRepository.syncReporteVendedor(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<VentaVendedor>> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: List<VentaVendedor>) {
                    reporteVendedor.value = t
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

    fun syncReporteCabecera() {
        roomRepository.syncReporteCabecera()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<VentaCabecera> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: VentaCabecera) {
                    reporteCabecera.value = t
                }
            })
    }

    fun syncReporteAdminBody(tipo: Int) {
        roomRepository.syncReporteAdminBody(tipo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<VentaAdmin>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: List<VentaAdmin>) {
                    reporteAdmin.value = t
                }
            })
    }

    fun syncReporteAdminSupervisor1(id: Int, local: Int) {
        roomRepository.deleteReporteUbicacion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    roomRepository.syncReporteAdminSupervisor1(id, local)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<VentaUbicacion>> {
                            override fun onSubscribe(d: Disposable) {}
                            override fun onError(e: Throwable) {}
                            override fun onComplete() {}
                            override fun onNext(t: List<VentaUbicacion>) {
                                insertVentaUbicacion(t)
                            }
                        })
                }
            })
    }

    fun syncReporteAdminSupervisor2(id: Int, local: Int) {
        roomRepository.syncReporteAdminSupervisor2(id, local)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<VentaMes>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: List<VentaMes>) {
                    reporteMes.value = t
                }
            })
    }

    fun syncReporteAdminSupervisor3(id: Int, local: Int) {
        roomRepository.syncReporteAdminSupervisor3(id, local)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<VentaAdminVendedor>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: List<VentaAdminVendedor>) {
                    reporteAdminVendedor.value = t
                }
            })
    }

    fun syncReporteAdminVendedor1(id: Int, local: Int) {
        roomRepository.deleteReporteUbicacion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    roomRepository.syncReporteAdminVendedor1(id, local)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<VentaUbicacion>> {
                            override fun onSubscribe(d: Disposable) {}
                            override fun onError(e: Throwable) {}
                            override fun onComplete() {}
                            override fun onNext(t: List<VentaUbicacion>) {
                                insertVentaUbicacion(t)
                            }
                        })
                }
            })
    }

    fun syncReporteAdminVendedor2(id: Int, local: Int) {
        roomRepository.syncReporteAdminVendedor2(id, local)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<VentaMes>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
                override fun onNext(t: List<VentaMes>) {
                    reporteMes.value = t
                }
            })
    }

    fun syncReporteAdminVendedorUbicacion() {
        roomRepository.deleteReporteAdminVendedorUbicacion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    roomRepository.syncReporteAdminVendedorUbicacion()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<VentaUbicacionVendedor>> {
                            override fun onSubscribe(d: Disposable) {}
                            override fun onError(e: Throwable) {}
                            override fun onComplete() {}
                            override fun onNext(t: List<VentaUbicacionVendedor>) {
                                insertVentaUbicacionVendedor(t)
                            }
                        })
                }
            })

    }

    private fun insertVentaUbicacionVendedor(t: List<VentaUbicacionVendedor>) {
        roomRepository.insertVentaUbicacionVendedor(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
    }

    fun getVentaUbicacionVendedor(): LiveData<List<VentaUbicacionVendedor>> {
        return roomRepository.getVentaUbicacionVendedor()
    }

    fun getVentaUbicacionVendedorById(id: Int): LiveData<VentaUbicacionVendedor> {
        return roomRepository.getVentaUbicacionVendedorById(id)
    }
}