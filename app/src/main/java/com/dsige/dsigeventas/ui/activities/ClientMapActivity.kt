package com.dsige.dsigeventas.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.MapLegs
import com.dsige.dsigeventas.data.local.model.MapPrincipal
import com.dsige.dsigeventas.data.local.model.MapRoute
import com.dsige.dsigeventas.data.local.model.MapStartLocation
import com.dsige.dsigeventas.helper.DataParser
import com.dsige.dsigeventas.helper.Gps
import com.dsige.dsigeventas.helper.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.maps.android.ui.IconGenerator
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class ClientMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    lateinit var camera: CameraPosition
    lateinit var mMap: GoogleMap
    var mapView: View? = null
    lateinit var place1: MarkerOptions
    lateinit var place2: MarkerOptions
    lateinit var locationManager: LocationManager

    var MIN_DISTANCE_CHANGE_FOR_UPDATES: Int = 10
    var MIN_TIME_BW_UPDATES: Int = 5000
    var isFirstTime: Boolean = true

    var latitud: String = ""
    var longitud: String = ""
    var title: String = ""

    override fun onResume() {
        super.onResume()
        val gps = Gps(this)
        if (!gps.isLocationEnabled()) {
            gps.showSettingsAlert(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_map)
        val b = intent.extras
        if (b != null) {
            latitud = b.getString("latitud")!!
            longitud = b.getString("longitud")!!
            title = b.getString("title")!!
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
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

            val sydney = LatLng(latitud.toDouble(), longitud.toDouble())
            mMap.addMarker(MarkerOptions().position(sydney).title(title))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

            mMap.isMyLocationEnabled = true
            mMap.isTrafficEnabled = true

            if (mapView?.findViewById<View>(Integer.parseInt("1")) != null) {
                // Get the button view
                val locationButton =
                    (mapView!!.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(
                        Integer.parseInt("2")
                    )
                // and next place it, on bottom right (as Google Maps app)
                val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                layoutParams.setMargins(0, 0, 30, 30)
            }

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
        } else {
            ActivityCompat.requestPermissions(this, permisos, 1)
        }
    }

    private fun zoomToLocation(location: Location) {
        camera = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .zoom(12f)  // limite 21
            //.bearing(165) // 0 - 365°
            .tilt(30f)        // limit 90
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
    }

    private fun getUrl(origin: LatLng, dest: LatLng): String {
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val mode = "mode=driving&alternatives=true"
        val parameters = "$str_origin&$str_dest&$mode"
        val output = "json"

        return String.format(
            "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
            output, parameters, getString(R.string.google_maps_key)
        )
    }

    override fun onLocationChanged(location: Location) {
        if (isFirstTime) {
            zoomToLocation(location)
            place1 =
                MarkerOptions().position(LatLng(location.latitude, location.longitude)).title("YO")
            place2 = MarkerOptions().position(LatLng(latitud.toDouble(), longitud.toDouble()))
                .title(title)

            FetchURL().execute(getUrl(place1.position, place2.position))

            isFirstTime = false
        }
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
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(position)
                                        .title(m.start_address)
                                        .icon(
                                            Util.bitmapDescriptorFromVector(this@ClientMapActivity, R.drawable.ic_people)
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
            val colorVariable = arrayOf(Color.RED, Color.BLUE, Color.GRAY, Color.GREEN)
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
                lineOptions.color(colorVariable[i])
                val polyline = mMap.addPolyline(lineOptions)
                polyline.isClickable = true
            }
        }
    }
}