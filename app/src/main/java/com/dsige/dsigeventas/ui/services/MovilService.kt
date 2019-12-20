package com.dsige.dsigeventas.ui.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.dsige.dsigeventas.ui.broadcasts.MovilReceiver

class MovilService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        Log.i("service", "Close MainService")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("service", "Open MainService")
    }

    override fun onDestroy() {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(this, MovilReceiver::class.java).putExtra("tipo", 0)
        val pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        pi.cancel()
        am.cancel(pi)
        super.onDestroy()
        Log.i("service", "Close MainService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val movil = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intentMovil = Intent(this, MovilReceiver::class.java).putExtra("tipo", 1)
        val pMovil =
            PendingIntent.getBroadcast(this, 0, intentMovil, PendingIntent.FLAG_UPDATE_CURRENT)
        val time: Long = 60 * 1000
        movil.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time, pMovil)
        return START_STICKY
    }
}