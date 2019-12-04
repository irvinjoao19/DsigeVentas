package com.dsige.dsigeventas.ui.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log
import com.dsige.dsigeventas.data.local.model.EstadoMovil
import com.dsige.dsigeventas.data.local.repository.AppRepository
import com.dsige.dsigeventas.helper.Gps
import com.dsige.dsigeventas.helper.Mensaje
import com.dsige.dsigeventas.helper.Util
import com.google.gson.Gson
import dagger.android.AndroidInjection
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class MovilReceiver : BroadcastReceiver() {

    @Inject
    lateinit var roomRepository: AppRepository

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        when (intent.getIntExtra("tipo", 0)) {
            1 -> {
                val gps = Gps(context)
                val gpsActivo = if (gps.isLocationEnabled()) 1 else 0
                executeMovil(context, gpsActivo)
            }
        }
    }

    private fun executeMovil(context: Context, gpsActivo: Int) {
        Completable.fromAction {
            val usuarioId = roomRepository.getUsuarioIdTask()
            if (usuarioId != 0) {
                movil(context, gpsActivo, roomRepository, usuarioId)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    Log.i("TAG", "servicios iniciados")
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Log.i("TAG", e.toString())
                }
            })
    }

    private fun movil(
        context: Context,
        gpsActivo: Int,
        roomRepository: AppRepository,
        operarioId: Int
    ) {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent = context.registerReceiver(null, ifilter)!!
        val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct: Int = level

        val modoAvion = if (Settings.System.getInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON,
                0
            ) == 0
        ) 0 else 1

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        val planDatos = if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE || activeNetwork.isConnected) 1 else 0
        } else {
            0
        }

        val operario = EstadoMovil(
            operarioId,
            gpsActivo,
            batteryPct,
            Util.getFechaActual(),
            modoAvion,
            planDatos
        )
        val movil = Gson().toJson(operario)
        Log.i("GPS", movil)
        val body =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), movil)
        val mensajeCall = roomRepository.saveMovil(body)
        mensajeCall.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Mensaje> {
                override fun onComplete() {
                    Log.i("TAG", "Movil Enviado")
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Mensaje) {

                }

                override fun onError(e: Throwable) {
                    Log.i("ERROR", "ERROR DE ENVIO ESTADO MOVIL")
                }

            })
    }
}