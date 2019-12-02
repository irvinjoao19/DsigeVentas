package com.dsige.dsigeventas.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.helper.FetchURL
import com.dsige.dsigeventas.helper.Gps
import com.dsige.dsigeventas.helper.TaskLoadedCallback

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, TaskLoadedCallback {

    lateinit var camera: CameraPosition
    lateinit var mMap: GoogleMap
    var mapView: View? = null
    lateinit var place1: MarkerOptions
    lateinit var place2: MarkerOptions
    var currentPolyline: Polyline? = null
    lateinit var locationManager: LocationManager

    var isFirstTime: Boolean = true
    var MIN_DISTANCE_CHANGE_FOR_UPDATES: Int = 10
    var MIN_TIME_BW_UPDATES: Int = 5000

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
        setContentView(R.layout.activity_maps)
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
            mMap.isTrafficEnabled = true
            mMap.isMyLocationEnabled = true

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

    override fun onTaskDone(vararg values: Any) {
        currentPolyline?.remove()
        currentPolyline = mMap.addPolyline(values[0] as PolylineOptions)
    }

    private fun getUrl(origin: LatLng, dest: LatLng): String {
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val mode = "mode=driving&alternatives=true"
        val parameters = "$str_origin&$str_dest&$mode"
        val output = "json"
        return String.format(
            "https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s",
            output,
            parameters,
            getString(R.string.google_maps_key)
        )
    }

    override fun onLocationChanged(location: Location) {
        zoomToLocation(location)
        if (isFirstTime) {
            place1 =
                MarkerOptions().position(LatLng(location.latitude, location.longitude)).title("YO")
            place2 = MarkerOptions().position(LatLng(latitud.toDouble(), longitud.toDouble()))
                .title(title)
            FetchURL(this).execute(getUrl(place1.position, place2.position), "driving")
            isFirstTime = false
            FetchURL(this).execute(getUrl(place1.position, place2.position), "driving")
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
}