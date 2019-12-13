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
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
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
import com.dsige.dsigeventas.ui.adapters.EstadoAdapter
import com.dsige.dsigeventas.ui.adapters.GrupoAdapter
import com.dsige.dsigeventas.ui.adapters.ProductoPedidoAdapter
import com.dsige.dsigeventas.ui.adapters.RepartoDetalleAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.android.support.DaggerAppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.inject.Inject

class MapsActivity : DaggerAppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel

    lateinit var camera: CameraPosition
    lateinit var mMap: GoogleMap
    var mapView: View? = null
    lateinit var place1: MarkerOptions
    lateinit var place2: MarkerOptions
    lateinit var locationManager: LocationManager

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null

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
            repartoViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(RepartoViewModel::class.java)

            latitud = b.getString("latitud")!!
            longitud = b.getString("longitud")!!
            title = b.getString("title")!!
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)

            repartoViewModel.mensajeError.observe(this, Observer<String> { s ->
                if (s != null) {
                    loadFinish()
                    Util.toastMensaje(this, s)
                }
            })
            repartoViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
                if (s != null) {
                    loadFinish()
                    Util.toastMensaje(this, s)
                    finish()
                }
            })
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
        mMap.setOnMarkerClickListener(this)
    }

    private fun zoomToLocation(location: Location) {
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

    private fun getUrl(origin: LatLng, dest: LatLng): String {
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val mode = "mode=driving&alternatives=true"
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

    private fun getUrl2(o: LatLng, d: LatLng) {
        val origen = String.format("%s,%s", o.latitude, o.longitude)
        val destino = String.format("%s,%s", d.latitude, d.longitude)
        FetchURL().execute("", origen, destino)
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
            PointsParser().execute(s)
        }

        @Throws(IOException::class)
        private fun downloadUrl(strUrl: String): String {
//            var data = ""
//            val apiServices = AppRest.api.create(ApiService::class.java)
//            val call = apiServices
//                .getDirection(
//                    origen, destino, "driving", false, getString(R.string.google_maps_key)
//                )
//
//            val response = call.execute()!!
//            if (response.code() == 200) {
//                val map: MapPrincipal = response.body()!!
//                data = Gson().toJson(map)
//            }


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
                    BufferedReader(InputStreamReader(iStream))
                val sb = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                data = sb.toString()
//                Log.d("mylog", "Downloaded URL: $data")
                br.close()
            } catch (e: Exception) {
                Log.d("mylog", "Exception downloading URL: $e")
            } finally {
                Objects.requireNonNull(iStream)!!.close()
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

    override fun onMarkerClick(m: Marker): Boolean {
        dialogResumen(m)
        return true
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textView: TextView = view.findViewById(R.id.textViewLado)
        textView.text = String.format("%s", "Enviando")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun loadFinish() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    private fun sendDialog(d: AlertDialog, r: Reparto) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas enviar el reparto?")
            .setPositiveButton("SI") { dialog, _ ->
                load()
                repartoViewModel.updateReparto(1, r)
                dialog.dismiss()
                d.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun dialogResumen(m: Marker) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.dialog_reparto, null)

        val linearLayoutLoad: ConstraintLayout = v.findViewById(R.id.linearLayoutLoad)
        val linearLayoutPrincipal: LinearLayout = v.findViewById(R.id.linearLayoutPrincipal)
        val textViewRuc: TextView = v.findViewById(R.id.textViewRuc)
        val textViewDoc: TextView = v.findViewById(R.id.textViewDoc)
        val textViewNameClient: TextView = v.findViewById(R.id.textViewNameClient)
        val textViewSubTotal: TextView = v.findViewById(R.id.textViewSubTotal)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val imageViewClose: ImageView = v.findViewById(R.id.imageViewClose)
        val editTextEstado: TextInputEditText = v.findViewById(R.id.editTextEstado)
        val editTextMotivo: TextInputEditText = v.findViewById(R.id.editTextMotivo)
        val textInputMotivo: TextInputLayout = v.findViewById(R.id.textInputMotivo)
        val buttonGuardar: MaterialButton = v.findViewById(R.id.buttonGuardar)

        var re = Reparto()

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        imageViewClose.setOnClickListener { dialog.dismiss() }
        editTextEstado.setOnClickListener { dialogSpinner(textInputMotivo, "Estado", 1, re) }
        editTextMotivo.setOnClickListener { dialogSpinner(textInputMotivo, "Grupo", 2, re) }

        buttonGuardar.setOnClickListener {
            sendDialog(dialog, re)
        }
        val repartoDetalleAdapter =
            RepartoDetalleAdapter(object : OnItemClickListener.RepartoDetalleListener {
                override fun onItemClick(r: RepartoDetalle, v: View, position: Int) {
                    when (v.id) {
                        R.id.editTextCantidad -> updateCantidadReparto(r)
                        R.id.imageViewNegative -> {
                            val resta = r.cantidad
                            if (resta != 0.0) {
                                val rTotal = (resta - 1).toString()
                                val nNegative = rTotal.toDouble()

                                r.cantidad = nNegative
                                r.total = nNegative * r.precioVenta
                                repartoViewModel.updateRepartoDetalle(r)
                            }
                        }
                        else -> {
                            val popupMenu = PopupMenu(this@MapsActivity, v)
                            popupMenu.menu.add(0, 1, 0, getText(R.string.delete))
                            popupMenu.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    1 -> deleteRepartoDialog(r)
                                }
                                false
                            }
                            popupMenu.show()
                        }
                    }
                }
            })
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = repartoDetalleAdapter

        Handler().postDelayed({
            repartoViewModel.getReparto()
                .observe(this, Observer<List<Reparto>> { count ->
                    if (count != null) {
                        for (s: Reparto in count) {
                            if (s.latitud.isNotEmpty() || s.longitud.isNotEmpty()) {
                                val l1 = Location("location 1")
                                l1.latitude = s.latitud.toDouble()
                                l1.longitude = s.longitud.toDouble()
                                val distance = Util.calculationByDistance(l1, m.position)

                                if (distance <= 20) {
                                    repartoViewModel.getRepartoById(s.repartoId)
                                        .observe(this, Observer<Reparto> { r ->
                                            if (r != null) {
                                                textViewRuc.text = r.numeroDocumento
                                                textViewDoc.setText(
                                                    Util.getTextHTML("<strong>Nro Doc Vta: </string>" + r.docVTA),
                                                    TextView.BufferType.SPANNABLE
                                                )
                                                textViewNameClient.text = r.apellidoNombreCliente
                                                textViewSubTotal.setText(
                                                    Util.getTextHTML("<font color='red'>Total : </font> S/" + r.subTotal),
                                                    TextView.BufferType.SPANNABLE
                                                )

                                                editTextEstado.setText(r.nombreEstado)
                                                editTextMotivo.setText(r.motivo)
                                                re = r

                                                linearLayoutLoad.visibility = View.GONE
                                                linearLayoutPrincipal.visibility = View.VISIBLE
                                            }
                                        })

                                    repartoViewModel.getDetalleRepartoById(s.repartoId)
                                        .observe(this, Observer<PagedList<RepartoDetalle>> { p ->
                                            if (p.size != 0) {
                                                updateReparto(s.repartoId, p)
                                                repartoDetalleAdapter.submitList(p)
                                            }
                                        })
                                    break
                                }
                            }
                        }
                    }
                })
        }, 800)
    }

    private fun updateCantidadReparto(p: RepartoDetalle) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.dialog_count_producto, null)
        val editTextProducto: EditText = v.findViewById(R.id.editTextProducto)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        //editTextProducto.setText(p.cantidad.toString())
        Util.showKeyboard(editTextProducto, this)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        buttonAceptar.setOnClickListener {
            if (editTextProducto.text.toString().isNotEmpty()) {
                val nPositive = editTextProducto.text.toString().toDouble()
                if (nPositive > p.cantidadExacta) {
                    repartoViewModel.setError("Cantidad no debe ser mayor al actual " + p.cantidadExacta)
                } else {
                    p.cantidad = nPositive
                    p.total = nPositive * p.precioVenta
                    repartoViewModel.updateRepartoDetalle(p)
                }
                Util.hideKeyboardFrom(this, v)
                dialog.dismiss()

            } else {
                repartoViewModel.setError("Digite cantidad")
            }
        }
        buttonCancelar.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun deleteRepartoDialog(r: RepartoDetalle) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas eliminar el producto ?")
            .setPositiveButton("SI") { dialog, _ ->
                r.estado = 0
                repartoViewModel.updateRepartoDetalle(r)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun updateReparto(repartoId: Int, repartos: List<RepartoDetalle>) {
        var total = 0.0
        for (p in repartos) {
            total += p.total
        }
        repartoViewModel.updateTotalReparto(repartoId, total)
    }

    private fun dialogSpinner(
        l: TextInputLayout, title: String, tipo: Int, re: Reparto
    ) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_spinner, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.show()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = layoutManager

        textViewTitle.text = title

        when (tipo) {
            1 -> {
                val estadoAdapter = EstadoAdapter(object : OnItemClickListener.EstadoListener {
                    override fun onItemClick(e: Estado, v: View, position: Int) {
                        re.estado = e.estadoId
                        re.nombreEstado = e.nombre
                        repartoViewModel.updateReparto(0, re)
                        l.visibility = View.GONE
                        if (e.estadoId == 30) {
                            l.visibility = View.VISIBLE
                        }
                        dialogSpinner.dismiss()
                    }
                })
                recyclerView.adapter = estadoAdapter
                repartoViewModel.getEstados().observe(this, Observer<List<Estado>> { e ->
                    if (e != null) {
                        estadoAdapter.addItems(e)
                    }
                })
            }
            2 -> {
                val grupoAdapter = GrupoAdapter(object : OnItemClickListener.GrupoListener {
                    override fun onItemClick(g: Grupo, v: View, position: Int) {
                        re.motivoId = g.detalleTablaId
                        re.motivo = g.descripcion
                        repartoViewModel.updateReparto(0, re)
                        dialogSpinner.dismiss()
                    }
                })
                recyclerView.adapter = grupoAdapter
                repartoViewModel.getGrupos().observe(this, Observer<List<Grupo>> { e ->
                    if (e != null) {
                        grupoAdapter.addItems(e)
                    }
                })
            }
        }
    }
}