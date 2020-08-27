package com.dsige.dsigeventas.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
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
import com.dsige.dsigeventas.ui.activities.MapsActivity
import com.dsige.dsigeventas.ui.activities.RepartoActivity
import com.dsige.dsigeventas.ui.adapters.LocalAdapter
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
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_maps.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MapsFragment : DaggerFragment(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener, View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> startActivity(Intent(context, RepartoActivity::class.java))
            R.id.editTextLocal -> dialogLocal()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationManager =
            context!!.getSystemService(DaggerAppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
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

    private var param1: String? = null
    private var param2: String? = null

    lateinit var mMap: GoogleMap
    lateinit var locationManager: LocationManager
    lateinit var camera: CameraPosition

    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Int = 10
    private var MIN_TIME_BW_UPDATES: Int = 5000
    private var isFirstTime: Boolean = true
    private var waypoints: String = ""

    private var lat: String = ""
    private var lat2: String = ""
    private var lng: String = ""
    private var lng2: String = ""
    private var title: String = ""
    private var localId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        editTextLocal.setText(String.format("ATE"))
        editTextLocal.setOnClickListener(this)
        fab.setOnClickListener(this)

        repartoViewModel.getReparto().observe(viewLifecycleOwner, Observer { count ->
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

                        if (i == 1) {
                            lat = s.latitud
                            lng = s.longitud
                        }

                        if (i == y) {
                            lat2 = s.latitud
                            lng2 = s.longitud
                        }
                    }
                    i++
                }
                FetchURL().execute(getUrl(lat, lng, lat2, lng2))
            }
        })

        repartoViewModel.getTotalReparto().observe(viewLifecycleOwner, Observer { c ->
            textViewAsignados.setText(
                Util.getTextHTML("<strong>Asignados: </string>$c"),
                TextView.BufferType.SPANNABLE
            )
        })

        repartoViewModel.getTotalEntregado().observe(viewLifecycleOwner, Observer { c ->
            textViewEntregado.setText(
                Util.getTextHTML("<strong>Entregados: </string>$c"),
                TextView.BufferType.SPANNABLE
            )
        })

        repartoViewModel.getTotalDevuelto().observe(viewLifecycleOwner, Observer { c ->
            textViewDevuelto.setText(
                Util.getTextHTML("<strong>Devueltos: </string>$c"),
                TextView.BufferType.SPANNABLE
            )
        })

        repartoViewModel.getTotalParciales().observe(viewLifecycleOwner, Observer { c ->
            textViewParciales.setText(
                Util.getTextHTML("<strong>Parciales: </string>$c"),
                TextView.BufferType.SPANNABLE
            )
        })

        repartoViewModel.tipo.value = 1
    }

    private fun zoomToLocation(location: Location?) {
        if (location != null) {
            if (context != null) {
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
                        .icon(Util.bitmapDescriptorFromVector(context!!, R.drawable.ic_car_map))
                )
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

    override fun onLocationChanged(location: Location) {
        if (isFirstTime) {
            zoomToLocation(location)
        }
        isFirstTime = false
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

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
                    var i = 0
                    if (mapLegs != null) {
                        for (m: MapLegs in mapLegs) {
                            val start: MapStartLocation? = m.start_location
                            if (start != null) {
                                val position = LatLng(start.lat, start.lng)
                                if (context != null) {
                                    val iconFactory = IconGenerator(context)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(position)
                                            .title(m.start_address)
                                            .icon(
                                                BitmapDescriptorFactory.fromBitmap(
                                                    iconFactory.makeIcon(
                                                        (i++).toString()
                                                    )
                                                )
                                            )
                                    )
                                }
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMarkerClick(m: Marker): Boolean {
        dialogResumen(m)
        return true
    }

    private fun dialogResumen(m: Marker) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.cardview_resumen_maps, null)
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
                Intent(context, MapsActivity::class.java)
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
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        val layoutManager = LinearLayoutManager(context)
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
                editTextLocal.setText(l.nombre)
                dialog.dismiss()
            }
        })
        recyclerView.adapter = localAdapter
        repartoViewModel.getLocales().observe(this, Observer { e ->
            if (e != null) {
                localAdapter.addItems(e)
            }
        })
    }
}