package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.VentaUbicacion
import com.dsige.dsigeventas.data.local.model.VentaUbicacionVendedor
import com.dsige.dsigeventas.data.viewModel.ReporteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_vendedor_map.*
import javax.inject.Inject

class AdminVendedoresMapActivity : DaggerAppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var ventasViewModel: ReporteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_vendedores_map)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getString("title")!!)
        }
    }

    private fun bindUI(title: String) {
        ventasViewModel =
            ViewModelProvider(this, viewModelFactory).get(ReporteViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        ventasViewModel.syncReporteAdminVendedorUbicacion()

        ventasViewModel.mensajeError.observe(this, {
            Util.toastMensaje(this, it)
        })
    }

    override fun onMapReady(mMap: GoogleMap) {
        val camera = CameraPosition.Builder()
            .target(LatLng(-12.036175, -76.999561))
            .zoom(9f)  // limite 21
            //.bearing(165) // 0 - 365°
            .tilt(30f)        // limit 90
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

        ventasViewModel.getVentaUbicacionVendedor().observe(this) { t ->
            if (t != null) {
                for (p: VentaUbicacionVendedor in t) {
                    if (p.latitud.isNotEmpty() || p.longitud.isNotEmpty()) {
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(p.latitud.toDouble(), p.longitud.toDouble()))
                                .title(p.id.toString())
                                .icon(Util.bitmapDescriptorFromVector(this, R.drawable.ic_people))
                        )
                    }
                }
            }
        }
        mMap.setOnMarkerClickListener(this@AdminVendedoresMapActivity)
    }

    override fun onMarkerClick(m: Marker): Boolean {
        dialogInformation(m.title)
        return true
    }

    private fun dialogInformation(title: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_vendedor_ubicacion, null)
        val linearLayoutLoad: ConstraintLayout = view.findViewById(R.id.linearLayoutLoad)
        val linearLayoutPrincipal: LinearLayout = view.findViewById(R.id.linearLayoutPrincipal)
        val imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textView1: TextView = view.findViewById(R.id.textView1)
        val textView2: TextView = view.findViewById(R.id.textView2)
        val textView3: TextView = view.findViewById(R.id.textView3)
        val textView4: TextView = view.findViewById(R.id.textView4)
        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.show()

        textViewTitle.text = String.format("Vendedor")
        imageViewClose.setOnClickListener { dialogSpinner.dismiss() }

        Handler().postDelayed({
            ventasViewModel.getVentaUbicacionVendedorById(title.toInt()).observe(this) { p ->
                if (p != null) {
                    Util.getTextStyleHtml(
                        String.format("<strong>%s</strong>", p.vendedor), textView1
                    )
                    Util.getTextStyleHtml(
                        String.format("Total de Pedidos: %s", p.totalPedidos), textView2
                    )
                    Util.getTextStyleHtml(
                        String.format("Total: %s", p.total), textView3
                    )
                    textView4.visibility = View.GONE
                    linearLayoutLoad.visibility = View.GONE
                    linearLayoutPrincipal.visibility = View.VISIBLE
                }
            }
        }, 800)
    }
}