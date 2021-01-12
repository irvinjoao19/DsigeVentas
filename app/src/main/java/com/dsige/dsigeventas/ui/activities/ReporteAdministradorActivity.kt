package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.data.viewModel.ReporteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.MenuAdapter
import com.dsige.dsigeventas.ui.adapters.ReporteAdministradorAdapter
import com.dsige.dsigeventas.ui.adapters.ReporteSupervisorAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_reporte_administrador.*
import java.util.ArrayList
import javax.inject.Inject

class ReporteAdministradorActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextTipoReporte -> dialogSpinner()
            R.id.fabVendedor -> startActivity(
                Intent(this, AdminVendedoresMapActivity::class.java)
                    .putExtra("title", "Ubicación de Vendedores")
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var ventasViewModel: ReporteViewModel
    private var usuarioId: Int = 0
    private var nombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_administrador)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getString("title")!!, b.getInt("id"))
        }
    }

    private fun bindUI(title: String, id: Int) {
        usuarioId = id
        nombre = title
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        ventasViewModel =
            ViewModelProvider(this, viewModelFactory).get(ReporteViewModel::class.java)

        ventasViewModel.syncReporteCabecera()
        ventasViewModel.reporteCabecera.observe(this, {
            if (it != null) {
                Util.getTextStyleHtml(
                    String.format(
                        "<font color='%s'>Venta del Mes: %s</font>", "#03A9F4", it.totalVtaMes
                    ), textView1
                )
                Util.getTextStyleHtml(
                    String.format("Total Devoluciones: %s", it.totalDevolucion), textView2
                )
                Util.getTextStyleHtml(
                    String.format("Venta Real: %s", it.totalVtaReal), textView3
                )
                Util.getTextStyleHtml(
                    String.format("Total Venta del Dia: %s", it.totalVtaDia), textView4
                )
                Util.getTextStyleHtml(
                    String.format("Total de Pedidos: %s", it.totalPedidoDia), textView5
                )
            }
        })

        editTextTipoReporte.setOnClickListener(this)
        fabVendedor.setOnClickListener(this)

        val reporteAdminAdapter =
            ReporteAdministradorAdapter(object : OnItemClickListener.VentaAdminListener {
                override fun onItemClick(s: VentaAdmin, v: View, position: Int) {
                    showPopupMenu(v, s)
                }
            })

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = reporteAdminAdapter

        ventasViewModel.reporteAdmin.observe(this) {
            if (it != null) {
                reporteAdminAdapter.addItems(it)
            }
        }
    }

    private fun dialogSpinner() {
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

        textViewTitle.text = String.format("%s", "Tipo Reporte")

        val menuAdapter = MenuAdapter(object : OnItemClickListener.MenuListener {
            override fun onItemClick(m: MenuPrincipal, view: View, position: Int) {
                editTextTipoReporte.setText(m.title)
                ventasViewModel.clearReporteAdmin()
                when (m.id) {
                    1 -> {
                        fabVendedor.visibility = View.GONE
                        ventasViewModel.syncReporteAdminBody(1)
                    }
                    else -> {
                        fabVendedor.visibility = View.VISIBLE
                        ventasViewModel.syncReporteAdminBody(2)
                    }
                }
                dialogSpinner.dismiss()
            }
        })
        recyclerView.adapter = menuAdapter

        val menu = ArrayList<MenuPrincipal>()
        menu.add(MenuPrincipal(1, "Supervisor"))
        menu.add(MenuPrincipal(2, "Vendedor"))
        menuAdapter.addItems(menu)
    }

    @SuppressLint("RestrictedApi")
    private fun showPopupMenu(v: View, s: VentaAdmin) {
        val popup = PopupMenu(v.context, v)
        val inflater = popup.menuInflater
        if (s.tipo == 1) {
            inflater.inflate(R.menu.admin_supervisor, popup.menu)
        } else {
            inflater.inflate(R.menu.admin_vendedor, popup.menu)
        }

        val menuBuilder = popup.menu as MenuBuilder
        menuBuilder.setOptionalIconsVisible(true)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.map -> {
                    startActivity(
                        Intent(
                            this@ReporteAdministradorActivity,
                            AdminMapActivity::class.java
                        )
                            .putExtra("title", s.vendedor)
                            .putExtra("id", s.vendedorId)
                            .putExtra("local", s.localId)
                            .putExtra("tipo", s.tipo)
                    )
                    true
                }
                R.id.resumenVD -> {
                    dialogReporteVentaDiaria(s.vendedorId, s.localId, s.tipo)
                    true
                }
                else -> {
                    dialogReporteVentaVendedor(s.vendedorId, s.localId)
                    true
                }
            }
        }
        popup.show()
    }

    private fun dialogReporteVentaDiaria(id: Int, local: Int, tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_reporte_resumen, null)
        val tableVenta: TableLayout = view.findViewById(R.id.tableVenta)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        ventasViewModel.clearReporteMes()
        if (tipo == 1) {
            ventasViewModel.syncReporteAdminSupervisor2(id, local)
        } else {
            ventasViewModel.syncReporteAdminVendedor2(id, local)
        }

        ventasViewModel.reporteMes.observe(this, {
            if (it != null) {
                getDataVentaDiaria(tableVenta, it)
            }
        })
    }

    private fun getDataVentaDiaria(table: TableLayout, data: List<VentaMes>) {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0
        val textSize = 18
        val rows = data.size

        for (i in 0 until rows) {
            val row: VentaMes = data[i]
            val tv = TextView(this)
            tv.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT
            )
            tv.gravity = Gravity.CENTER
            tv.setPadding(5, 15, 0, 5)
            tv.setBackgroundColor(Color.parseColor("#f8f8f8"))
            tv.text = row.fecha
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

            val tv2 = TextView(this)
            tv2.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT
            )
            tv2.setPadding(5, 15, 0, 5)
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            tv2.gravity = Gravity.CENTER
            tv2.setBackgroundColor(Color.parseColor("#f8f8f8"))
            tv2.setTextColor(Color.parseColor("#000000"))
            tv2.text = row.total.toString()

            val tr = TableRow(this)
            val trParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            trParams.setMargins(
                leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin
            )
            tr.setPadding(5, 5, 5, 5)
            tr.layoutParams = trParams
            tr.addView(tv)
            tr.addView(tv2)
            table.addView(tr, trParams)
        }
    }

    private fun dialogReporteVentaVendedor(id: Int, local: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_reporte_x_vendedor, null)
        val tableVenta: TableLayout = view.findViewById(R.id.tableVenta)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        ventasViewModel.clearReporteAdminVendedor()
        ventasViewModel.syncReporteAdminSupervisor3(id, local)
        ventasViewModel.reporteAdminVendedor.observe(this, {
            if (it != null) {
                getDataVentaVendedor(tableVenta, it)
            }
        })
    }

    private fun getDataVentaVendedor(table: TableLayout, data: List<VentaAdminVendedor>) {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0
        val textSize = 18
        val rows = data.size

        for (i in 0 until rows) {
            val row: VentaAdminVendedor = data[i]
            val tv = TextView(this)
            tv.layoutParams = TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT
            )

            tv.ellipsize = TextUtils.TruncateAt.END
            tv.isSingleLine = false
            tv.gravity = Gravity.CENTER
            tv.maxLines = 2
            tv.setPadding(5, 15, 0, 15)
            tv.setBackgroundColor(Color.parseColor("#f8f8f8"))
            tv.text = row.vendedor
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

            val tv2 = TextView(this)
            tv2.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT
            )
            tv2.setPadding(5, 15, 0, 5)
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            tv2.gravity = Gravity.CENTER
            tv2.setBackgroundColor(Color.parseColor("#f8f8f8"))
            tv2.setTextColor(Color.parseColor("#000000"))
            tv2.text = row.totalMes.toString()

            val tv3 = TextView(this)
            tv3.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT
            )
            tv3.setPadding(5, 15, 0, 5)
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            tv3.gravity = Gravity.CENTER
            tv3.setBackgroundColor(Color.parseColor("#f8f8f8"))
            tv3.setTextColor(Color.parseColor("#000000"))
            tv3.text = row.totalDia.toString()

            val tr = TableRow(this)
            val trParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            trParams.setMargins(
                leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin
            )
            tr.setPadding(5, 5, 5, 5)
            tr.layoutParams = trParams
            tr.addView(tv)
            tr.addView(tv2)
            tr.addView(tv3)
            table.addView(tr, trParams)
        }
    }
}