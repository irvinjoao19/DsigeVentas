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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Personal
import com.dsige.dsigeventas.data.tableview.MyTableAdapter
import com.dsige.dsigeventas.data.tableview.MyTableViewListener
import com.dsige.dsigeventas.data.viewModel.MapViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.evrencoskun.tableview.TableView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_personal_map.*
import javax.inject.Inject

class PersonalMapActivity : DaggerAppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_map)
        bindUI()
    }

    private fun bindUI() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel =
            ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)

        mapViewModel.init(Util.getFecha())
        fabStatistics.setOnClickListener(this)
    }

    override fun onMapReady(mMap: GoogleMap) {
        val camera = CameraPosition.Builder()
            .target(LatLng(-12.036175, -76.999561))
            .zoom(9f)  // limite 21
            //.bearing(165) // 0 - 365°
            .tilt(30f)        // limit 90
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

        mapViewModel.getPersonal().observe(this, Observer<List<Personal>> { t ->
            if (t != null) {
                for (p: Personal in t) {
                    if (p.latitud.isNotEmpty() || p.longitud.isNotEmpty()) {
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(p.latitud.toDouble(), p.longitud.toDouble()))
                                .title(p.personalId.toString())
                                .icon(Util.bitmapDescriptorFromVector(this, R.drawable.ic_people))
                        )
                    }
                }
            }
        })
        mMap.setOnMarkerClickListener(this@PersonalMapActivity)
    }

    override fun onMarkerClick(m: Marker): Boolean {
        dialogInformation(m.title)
        return true
    }

    private fun dialogInformation(title: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_personal, null)
        val linearLayoutLoad: ConstraintLayout = view.findViewById(R.id.linearLayoutLoad)
        val linearLayoutPrincipal: LinearLayout = view.findViewById(R.id.linearLayoutPrincipal)
        val imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewPedidos: TextView = view.findViewById(R.id.textViewPedidos)
        val textViewClientes: TextView = view.findViewById(R.id.textViewClientes)
        val textViewProductos: TextView = view.findViewById(R.id.textViewProductos)
        val textViewTotal: TextView = view.findViewById(R.id.textViewTotal)
        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.show()

        imageViewClose.setOnClickListener { dialogSpinner.dismiss() }

        Handler().postDelayed({
            mapViewModel.getPersonalById(title.toInt()).observe(this, Observer<Personal> { p ->
                if (p != null) {
                    textViewTitle.setText(
                        Util.getTextHTML("<strong>${p.nombrePersonal}</strong>"),
                        TextView.BufferType.SPANNABLE
                    )
                    textViewPedidos.setText(
                        Util.getTextHTML("<strong>Cantidad de Pedidos: </string>" + p.countPedidos),
                        TextView.BufferType.SPANNABLE
                    )
                    textViewClientes.setText(
                        Util.getTextHTML("<strong>Cantidad de Cliente: </string>" + p.countClientes),
                        TextView.BufferType.SPANNABLE
                    )
                    textViewProductos.setText(
                        Util.getTextHTML("<strong>Cantidad de Productos: </string>" + p.countProductos),
                        TextView.BufferType.SPANNABLE
                    )
                    textViewTotal.setText(
                        Util.getTextHTML("<font color =red><strong>Total: </string></font>" + p.total),
                        TextView.BufferType.SPANNABLE
                    )
                    linearLayoutLoad.visibility = View.GONE
                    linearLayoutPrincipal.visibility = View.VISIBLE
                }
            })
        }, 800)
    }

    private fun dialogResumen() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_resumen, null)
        val linearLayoutLoad: ConstraintLayout = view.findViewById(R.id.linearLayoutLoad)
        val linearLayoutPrincipal: LinearLayout = view.findViewById(R.id.linearLayoutPrincipal)
        val imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
        val tableView: TableView = view.findViewById(R.id.tableView)

        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        imageViewClose.setOnClickListener {
            dialog.dismiss()
        }

        val mTableAdapter = MyTableAdapter(this)
        tableView.adapter = mTableAdapter
        // Create listener
        tableView.tableViewListener = MyTableViewListener(tableView)

        Handler().postDelayed({
            mapViewModel.getPersonal().observe(this, Observer<List<Personal>> { p ->
                if (p != null) {
                    mTableAdapter.setUserList(p)
                    tableView.visibility = View.VISIBLE
                    linearLayoutLoad.visibility = View.GONE
                    linearLayoutPrincipal.visibility = View.VISIBLE
                    dialog.window!!.setLayout(1000, 500)
                }
            })
        }, 800)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabStatistics -> dialogResumen()
        }
    }
}