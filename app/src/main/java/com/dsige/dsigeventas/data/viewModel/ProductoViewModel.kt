package com.dsige.dsigeventas.data.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.local.model.Pedido
import com.dsige.dsigeventas.data.local.model.PedidoDetalle
import com.dsige.dsigeventas.data.local.model.Stock
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
import okhttp3.MultipartBody
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

    fun savePedido(pedidoId: Int) {
        roomRepository.savePedido(pedidoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = "Ok"
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.message.toString()
                }
            })
    }

    fun updateProducto(p: PedidoDetalle) {
        roomRepository.updateProducto(p)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    mensajeSuccess.value = "GUARDADO"
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.message.toString()
                }
            })
    }

    fun sendPedido(id: Int) {
        val pedidos: Observable<Pedido> = roomRepository.getPedidoById(id)
        pedidos.flatMap { a ->
            val json = Gson().toJson(a)
            Log.i("TAG", json)
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
            Observable.zip(
                Observable.just(a), roomRepository.sendPedido(body),
                BiFunction<Pedido, Mensaje, Mensaje> { _, mensaje ->
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
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

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
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Int) {
                    when (t) {
                        0 -> mensajeSuccess.value = "Ok"
                        1 -> mensajeError.value = "Completar los productos en cantidad 0"
                        2 -> mensajeError.value = "Agregar Producto"
                        3 -> mensajeSuccess.value = "Cliente"
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
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun generarPedidoCliente(latitud: String, longitud: String, clienteId: Int) {
        roomRepository.generarPedidoCliente(latitud, longitud, clienteId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Int) {
                    pedidoId.value = t
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
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
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun validateCliente(id: Int) {
        roomRepository.validateCliente(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Int) {
                    sendCliente(t,id)
                }

                override fun onError(e: Throwable) {
                    mensajeError.value = e.toString()
                }
            })
    }

    fun sendCliente(id: Int,pedidoId:Int) {
        val pedidos: Observable<Cliente> = roomRepository.getClienteByIdTask(id)
        pedidos.flatMap { a ->
            val json = Gson().toJson(a)
            Log.i("TAG", json)
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
            Observable.zip(
                Observable.just(a), roomRepository.sendCliente(body),
                BiFunction<Cliente, Mensaje, Mensaje> { _, mensaje ->
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
                    updateCliente(m,pedidoId)
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


    private fun updateCliente(m:Mensaje,id:Int){
        roomRepository.updateCliente(m,id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver{
                override fun onComplete() {
                    sendPedido(id)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    mensajeError.postValue(e.toString())
                }

            })
    }

}