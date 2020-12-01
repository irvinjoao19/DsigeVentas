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
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProductoViewModel @Inject
internal constructor(private val roomRepository: AppRepository, private val retrofit: ApiError) :
    ViewModel() {

    val mensajeError: MutableLiveData<String> = MutableLiveData()
    val mensajeSuccess: MutableLiveData<String> = MutableLiveData()
    val producto: MutableLiveData<Stock> = MutableLiveData()
    val pedidoId: MutableLiveData<Int> = MutableLiveData()
    val searchPedido: MutableLiveData<String> = MutableLiveData()
    val searchProducto: MutableLiveData<String> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()

    fun setLoading(b: Boolean) {
        loading.value = b
    }

    fun setError(s: String) {
        mensajeError.value = s
    }

    fun getProductoByPedido(id: Int): LiveData<PagedList<PedidoDetalle>> {
        return roomRepository.getProductoByPedido(id)
    }

    fun getProductos(): LiveData<PagedList<Stock>> {
        return Transformations.switchMap(searchProducto) { input ->
            if (input == null || input.isEmpty()) {
                roomRepository.getProductos()
            } else {
                roomRepository.getProductos(String.format("%s%s%s", "%", input, "%"))
            }
        }
    }

    fun getProductoById(id: Int): LiveData<Stock> {
        return roomRepository.getProductoById(id)
    }

    fun setProducto(p: Stock) {
        producto.value = p
    }

    fun updateCheckPedido(s: Stock) {
        roomRepository.updateCheckPedido(s)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.message.toString()
                }
            })
    }

    fun updateProducto(p: PedidoDetalle) {
        roomRepository.sendDetallePedido(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onNext(t: Mensaje) {
                    roomRepository.updateProducto(p, t.mensaje)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : CompletableObserver {
                            override fun onComplete() {
                                if (t.mensaje == "0") {
                                    mensajeError.value = "Supero el Stock"
                                } else {
                                    mensajeSuccess.value = "GUARDADO"
                                }
                            }

                            override fun onSubscribe(d: Disposable) {}
                            override fun onError(e: Throwable) {
                                mensajeError.value = e.message.toString()
                            }
                        })
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
            })
    }

    fun sendPedido(id: Int) {
        val pedidos: Observable<Pedido> = roomRepository.getOrdenById(id)
        pedidos.flatMap { a ->
            Observable.zip(
                Observable.just(a), roomRepository.sendCabeceraPedido(a), { _, mensaje ->
                    mensaje
                })
        }.subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(m: Mensaje) {
                    Log.i("TAG", "RECIBIENDO LOS DATOS")
                    updatePedido(m)
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

    private fun updatePedido(m: Mensaje) {
        roomRepository.updatePedido(m)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    mensajeError.value = e.message.toString()
                }
            })
    }

    fun validatePedido(id: Int) {
        roomRepository.validatePedido(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: Int) {
                    when (t) {
                        0 -> mensajeSuccess.value = "Ok"
                        1 -> mensajeError.value = "Completar los productos en cantidad 0"
                        2 -> mensajeError.value = "Agregar Producto"
                    }
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun getPedidoCliente(id: Int): LiveData<Pedido> {
        return roomRepository.getPedidoCliente(id)
    }

    fun updateTotalPedido(id: Int, igv: Double, total: Double, subTotal: Double) {
        roomRepository.updateTotalPedido(id, igv, total, subTotal)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }

    fun generarPedidoCliente(latitud: String, longitud: String, clienteId: Int) {
        roomRepository.generarPedidoCliente(latitud, longitud, clienteId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Pedido> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: Pedido) {
                    sendPedido(t)
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun sendPedido(p: Pedido) {
        roomRepository.sendCabeceraPedido(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onNext(t: Mensaje) {
                    insertPedido(p, t)
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val response = e.response().errorBody()
                        try {
                            val error = retrofit.errorConverter.convert(response!!)
                            mensajeError.postValue(error.Message)
                        } catch (e1: IOException) {
                            mensajeError.postValue(e1.toString())
                        }
                    } else {
                        mensajeError.postValue(e.toString())
                    }
                }
            })
    }

    private fun insertPedido(p: Pedido, t: Mensaje) {
        roomRepository.insertPedido(p, t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {
                    pedidoId.value = p.pedidoId
                    mensajeError.value = "Pedido generado"
                }
            })
    }

    fun personalSearch(s: String): LiveData<PagedList<Cliente>> {
        return roomRepository.getCliente(String.format("%s%s%s", "%", s, "%"))
    }

    fun getPedido(): LiveData<PagedList<Pedido>> {
        return Transformations.switchMap(searchPedido) { input ->
            if (input == null || input.isEmpty()) {
                roomRepository.getPedido()
            } else {
                roomRepository.getPedido(String.format("%s%s%s", "%", input, "%"))
            }
        }
    }

    fun deletePedidoDetalle(p: PedidoDetalle) {
        roomRepository.deletePedidoDetalle(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun deletePedido(p: Pedido) {
        roomRepository.deletePedido(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }

    fun initProductos(localId: Int) {
        roomRepository.syncProductos(localId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Stock>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: List<Stock>) {
                    insertProductos(t)
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
                    loading.value = false
                }

                override fun onComplete() {
                    loading.value = false
                }
            })
    }

    private fun insertProductos(p: List<Stock>) {
        roomRepository.insertProductos(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }

    // todo nuevo para guardar pedido

    fun savePedidoOnline(pedidoId: Int) {
        roomRepository.savePedidoOnline(pedidoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    mensajeError.value = e.message.toString()
                }

                override fun onComplete() {
                    roomRepository.getPedidoDetalles(pedidoId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<PedidoDetalle>> {
                            override fun onSubscribe(d: Disposable) {}
                            override fun onError(e: Throwable) {}
                            override fun onComplete() {}
                            override fun onNext(t: List<PedidoDetalle>) {
                                sendDetallePedidoGroup(t)
                            }
                        })
                }
            })
    }

    private fun sendDetallePedidoGroup(p: List<PedidoDetalle>) {
        roomRepository.sendDetallePedidoGroup(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Mensaje>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {
                    mensajeError.value = e.message.toString()
                }

                override fun onNext(t: List<Mensaje>) {
                    saveDetallePedidoGroup(t)
                }
            })
    }

    private fun saveDetallePedidoGroup(t: List<Mensaje>) {
        roomRepository.saveDetallePedidoGroup(t)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    mensajeSuccess.value = "Productos Agregados"
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.message.toString()
                }
            })
    }
}