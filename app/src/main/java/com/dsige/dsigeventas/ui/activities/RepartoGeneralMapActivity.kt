package com.dsige.dsigeventas.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.data.viewModel.RepartoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.DataParser
import com.dsige.dsigeventas.helper.Gps
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.LocalAdapter
import com.dsige.dsigeventas.ui.adapters.RepartoSearchAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.maps.android.ui.IconGenerator
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_reparto_general_map.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class RepartoGeneralMapActivity : DaggerAppCompatActivity(), OnMapReadyCallback, LocationListener,
    TextView.OnEditorActionListener, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    override fun onEditorAction(v: TextView, p1: Int, p2: KeyEvent?): Boolean {
        if (v.text.toString().isNotEmpty()) {
            personalSearch(localId, v.text.toString())
        }
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextLocal -> dialogLocal()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

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

        mMap.setOnMarkerClickListener(this)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var locationManager: LocationManager
    lateinit var camera: CameraPosition
    lateinit var mMap: GoogleMap

    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Int = 10
    private var MIN_TIME_BW_UPDATES: Int = 5000
    private var localId: Int = 1
    private var isFirstTime: Boolean = true
    private var waypoints: String = ""

    private var lat: String = ""
    private var lat2: String = ""
    private var lng: String = ""
    private var lng2: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reparto_general_map)
        bindUI()
    }

    private fun bindUI() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        editTextLocal.setOnClickListener(this)
        editTextClient.setOnEditorActionListener(this)
        editTextLocal.setText(String.format("ATE"))


        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {

                lat = gps.latitude.toString()
                lng = gps.longitude.toString()

                repartoViewModel.getReparto().observe(this, Observer { count ->
                    mMap.clear()
                    if (count.isNotEmpty()) {
                        waypoints = "waypoints=optimize:true|"
                        var i = 1
                        val y = count.size
                        textViewPendientes.setText(
                            Util.getTextHTML("<strong>Pendientes: </strong>$y"),
                            TextView.BufferType.SPANNABLE
                        )
                        for (s: Reparto in count) {
                            if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                waypoints += String.format("%s,%s|", s.latitud, s.longitud)
//                                if (i == 1) {
//                                    lat = s.latitud
//                                    lng = s.longitud
//                                }
                                if (i == y) {
                                    lat2 = s.latitud
                                    lng2 = s.longitud
                                }
                            }
                            i++
                        }
                        FetchURL().execute(getUrl(lat, lng, lat2, lng2))
//                        FetchURL().execute(getUrl(lat, lng, lat, lng))
                    }
                })
            }
        } else {
            gps.showSettingsAlert(this)
        }

        repartoViewModel.getTotalReparto().observe(this, Observer { c ->
            textViewAsignados.setText(
                Util.getTextHTML("<strong>Asignados: </string>$c"),
                TextView.BufferType.SPANNABLE
            )
        })

        repartoViewModel.getTotalEntregado().observe(this, Observer { c ->
            textViewEntregado.setText(
                Util.getTextHTML("<strong>Entregados: </string>$c"),
                TextView.BufferType.SPANNABLE
            )
        })

        repartoViewModel.getTotalDevuelto().observe(this, Observer { c ->
            textViewDevuelto.setText(
                Util.getTextHTML("<strong>Devueltos: </string>$c"),
                TextView.BufferType.SPANNABLE
            )
        })

        repartoViewModel.getTotalParciales().observe(this, Observer { c ->
            textViewParciales.setText(
                Util.getTextHTML("<strong>Parciales: </string>$c"),
                TextView.BufferType.SPANNABLE
            )
        })

        repartoViewModel.tipo.value = 1
    }

    override fun onLocationChanged(l: Location) {
        if (isFirstTime) {
            zoomToLocation(l)
        }
        isFirstTime = false
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    override fun onMarkerClick(m: Marker): Boolean {
        dialogResumen(m)
        return true
    }

    private fun zoomToLocation(location: Location?) {
        if (location != null) {
            camera = CameraPosition.Builder()
                .target(LatLng(location.latitude, location.longitude))
                .zoom(12f)  // limite 21
                //.bearing(165) // 0 - 365°
                .tilt(30f)        // limit 90
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .title("YO")
                    .icon(Util.bitmapDescriptorFromVector(this, R.drawable.ic_car_map))
            )
        }
    }

    private fun dialogResumen(m: Marker) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.cardview_resumen_maps, null)
        val buttonGo: MaterialButton = v.findViewById(R.id.buttonGo)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val textViewLatitud: TextView = v.findViewById(R.id.textViewLatitud)
        val textViewLongitud: TextView = v.findViewById(R.id.textViewLongitud)
        val imageViewClose: ImageView = v.findViewById(R.id.imageViewClose)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitle.text = m.title
        textViewLatitud.setText(
            Util.getTextHTML("<strong>Latitud: </strong> " + m.position.latitude),
            TextView.BufferType.SPANNABLE
        )
        textViewLongitud.setText(
            Util.getTextHTML("<strong>Longitud : </strong> " + m.position.longitude),
            TextView.BufferType.SPANNABLE
        )
        buttonGo.setOnClickListener {
            startActivity(
                Intent(this, MapsActivity::class.java)
                    .putExtra("latitud", m.position.latitude.toString())
                    .putExtra("longitud", m.position.longitude.toString())
                    .putExtra("title", m.title)
                    .putExtra("localId", localId)
            )
            dialog.dismiss()
        }
        imageViewClose.setOnClickListener { dialog.dismiss() }
    }

    private fun dialogLocal() {
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

        textViewTitle.text = String.format("Locales")

        val localAdapter = LocalAdapter(object : OnItemClickListener.LocalListener {
            override fun onItemClick(l: Local, v: View, position: Int) {
                repartoViewModel.tipo.value = l.localId
                localId = l.localId
                editTextLocal.setText(l.nombre)
                dialog.dismiss()
            }
        })
        recyclerView.adapter = localAdapter
        repartoViewModel.getLocales().observe(this, Observer {
            localAdapter.addItems(it)
        })
    }


    @SuppressLint("StaticFieldLeak")
    private inner class FetchURL : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg strings: String): String { // For storing data from web service
            var data = ""
            try {
                data = downloadUrl(strings[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            val map: MapPrincipal = Gson().fromJson(s, MapPrincipal::class.java)
            val mapRoutes: List<MapRoute>? = map.routes
            if (mapRoutes != null) {
                for (r: MapRoute in map.routes) {
                    val mapLegs: List<MapLegs>? = r.legs
                    var i = 1
                    if (mapLegs != null) {
                        for (m: MapLegs in mapLegs) {
                            val start: MapStartLocation? = m.start_location
                            if (start != null) {
                                val position = LatLng(start.lat, start.lng)
                                val iconFactory = IconGenerator(this@RepartoGeneralMapActivity)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(position)
                                        .title(m.start_address)
                                        .icon(
                                            BitmapDescriptorFactory.fromBitmap(
                                                iconFactory.makeIcon((i++).toString())
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
            PointsParser().execute(s)
        }

        @Throws(IOException::class)
        private fun downloadUrl(strUrl: String): String {
            var data = ""
            var iStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(strUrl)
                // Creating an http connection to communicate with url
                urlConnection = url.openConnection() as HttpURLConnection
                // Connecting to url
                urlConnection.connect()
                // Reading data from url
                iStream = urlConnection.inputStream
                val br =
                    BufferedReader(InputStreamReader(iStream!!))
                val sb = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                data = sb.toString()
                br.close()
            } catch (e: Exception) {
                Log.d("mylog", "Exception downloading URL: $e")
            } finally {
                iStream?.close()
                urlConnection!!.disconnect()
            }
            return data
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class PointsParser :
        AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? =
                null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DataParser()
                routes = parser.parse(jObject)
            } catch (e: java.lang.Exception) {
                Log.d("mylog", e.toString())
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng>
            var lineOptions: PolylineOptions?
            for (i in result!!.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()
                val path = result[i]
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(7f)
                lineOptions.color(Color.BLUE)
                mMap.addPolyline(lineOptions)
            }
        }
    }

    private fun getUrl(lat: String, lng: String, lat2: String, lng2: String): String {
        val str_origin = "origin=$lat,$lng"
        val str_dest = "destination=$lat2,$lng2"
        val mode = "mode=driving&alternatives=false"
        val sensor = "sensor=false"
        val parameters = "$str_origin&$str_dest&$mode&$sensor&$waypoints"
        val output = "json"

        Log.i(
            "TAG", String.format(
                "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
                output,
                parameters,
                getString(R.string.google_maps_key)
            )
        )
        return String.format(
            "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
            output,
            parameters,
            getString(R.string.google_maps_key)
        )
    }

    private fun getUrlSearch(origin: LatLng, dest: LatLng): String {
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val mode = "mode=driving&alternatives=false"
        val parameters = "$str_origin&$str_dest&$mode"
        val output = "json"

        Log.i(
            "TAG", String.format(
                "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
                output,
                parameters,
                getString(R.string.google_maps_key)
            )
        )
        return String.format(
            "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
            output,
            parameters,
            getString(R.string.google_maps_key)
        )
    }

    private fun personalSearch(localId: Int, s: String) {
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

        val repartoAdapter =
            RepartoSearchAdapter(object : OnItemClickListener.RepartoListener {
                override fun onItemClick(r: Reparto, v: View, position: Int) {
                    mMap.clear()

                    if (r.latitud.isNotEmpty() || r.longitud.isNotEmpty()) {
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(r.latitud.toDouble(), r.longitud.toDouble()))
                                .title(r.apellidoNombreCliente)
                                .icon(
                                    Util.bitmapDescriptorFromVector(
                                        this@RepartoGeneralMapActivity, R.drawable.ic_people
                                    )
                                )
                        )
                        val place1 =
                            MarkerOptions().position(LatLng(lat.toDouble(), lng.toDouble()))
                                .title("YO")
                        val place2 = MarkerOptions().position(
                            LatLng(r.latitud.toDouble(), r.longitud.toDouble())
                        )
                            .title(r.apellidoNombreCliente)
                        FetchURL().execute(getUrlSearch(place1.position, place2.position))
//                    textViewCantidad.text = String.format("Clientes encontrado : %s", 1)

                    }

                    dialog.dismiss()
                }
            })
        recyclerView.adapter = repartoAdapter
        repartoViewModel.personalSearch(localId, s)
            .observe(this, Observer(repartoAdapter::submitList))
    }
}