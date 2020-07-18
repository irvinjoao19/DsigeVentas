package com.dsige.dsigeventas.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.local.model.Distrito
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Gps
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.ClienteAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_client_general_map.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ClientGeneralMapActivity : DaggerAppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, TextView.OnEditorActionListener, LocationListener {

    override fun onEditorAction(v: TextView, p1: Int, p2: KeyEvent?): Boolean {
        if (v.text.toString().isNotEmpty()) {
            personalSearch(v.text.toString())
        }
        return false
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var clienteViewModel: ClienteViewModel
    lateinit var locationManager: LocationManager
    lateinit var mMap: GoogleMap

    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Int = 10
    private var MIN_TIME_BW_UPDATES: Int = 5000
    private var isFirstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_general_map)
        bindUI()
    }

    private fun bindUI() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)

        editTextClient.setOnEditorActionListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val permisos = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap = googleMap

            val camera = CameraPosition.Builder()
                .target(LatLng(-12.036175, -76.999561))
                .zoom(12f)  // limite 21
                //.bearing(165) // 0 - 365°
                .tilt(30f)        // limit 90
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

            mMap.isMyLocationEnabled = true
            mMap.isTrafficEnabled = true

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES.toLong(),
                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                this
            )
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES.toLong(),
                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                this
            )
            Handler().postDelayed({
                val gps = Gps(this)
                if (gps.isLocationEnabled()) {
                    getLocationName(this, gps.location!!, mMap)
                } else {
                    gps.showSettingsAlert(this)
                }
            }, 2000)
        } else {
            ActivityCompat.requestPermissions(this, permisos, 1)
        }
    }

    override fun onMarkerClick(m: Marker): Boolean {

        return true
    }

    private fun personalSearch(s: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_spinner, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = layoutManager
        textViewTitle.text = String.format("%s", "Resultado de busqueda")

        val clienteAdapter =
            ClienteAdapter(object : OnItemClickListener.ClienteListener {
                override fun onItemClick(c: Cliente, v: View, position: Int) {
                    mMap.clear()
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(c.latitud.toDouble(), c.longitud.toDouble()))
                            .title(c.nombreCliente)
                            .icon(
                                Util.bitmapDescriptorFromVector(
                                    this@ClientGeneralMapActivity, R.drawable.ic_people
                                )
                            )
                    )
                    textViewCantidad.text = String.format("Clientes encontrado : %s", 1)
                    dialog.dismiss()
                }
            })
        recyclerView.adapter = clienteAdapter
        clienteViewModel.personalSearch(s).observe(this, Observer(clienteAdapter::submitList))
    }

    override fun onLocationChanged(l: Location) {

    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    private fun getLocationName(context: Context, location: Location, mMap: GoogleMap) {
        try {
            val addressObservable = Observable.just(
                Geocoder(context).getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )[0]
            )
            addressObservable.subscribeOn(Schedulers.io())
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<Address> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(address: Address) {
                        showCliente(mMap, address.locality)
                    }

                    override fun onError(e: Throwable) {
                        clienteViewModel.setError(e.message!!)
                        progressBar.visibility = View.GONE
                    }

                    override fun onComplete() {
                        progressBar.visibility = View.GONE
                    }
                })
        } catch (e: IOException) {
            progressBar.visibility = View.GONE
        }
    }

    private fun showCliente(mMap: GoogleMap, distrito: String) {
        clienteViewModel.getClienteByDistrito(distrito).observe(this, Observer { c ->
            if (c != null) {
                var a = 0
                for (p: Cliente in c) {
                    if (p.latitud.isNotEmpty() || p.longitud.isNotEmpty()) {
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(p.latitud.toDouble(), p.longitud.toDouble()))
                                .title(p.nombreCliente)
                                .icon(Util.bitmapDescriptorFromVector(this, R.drawable.ic_people))
                        )
                        a++
                    }
                }
                textViewCantidad.text = String.format("Clientes encontrados : %s", a)
            }
        })
    }
}