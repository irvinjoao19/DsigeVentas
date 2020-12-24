package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.data.viewModel.ReporteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.*
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_reporte_supervisor.*
import javax.inject.Inject

class ReporteSupervisorActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var ventasViewModel: ReporteViewModel
    private var usuarioId: Int = 0
    private var nombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_supervisor)
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
        ventasViewModel.syncReporteSupervisor(id)

        val reporteSupervisorAdapter =
            ReporteSupervisorAdapter(object : OnItemClickListener.VentaSupervisorListener {
                override fun onItemClick(s: VentaSupervisor, v: View, position: Int) {
                    showPopupMenu(v, s)
                }
            })

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = reporteSupervisorAdapter

        ventasViewModel.reporteSupervisor.observe(this, {
            if (it.isNotEmpty()) {
                val v: VentaSupervisor? = it[0]
                if (v != null) {
                    Util.getTextStyleHtml(
                        String.format(
                            "<font color='%s'>Venta del Mes: %s</font>", "#03A9F4", v.ventaMes
                        ), textView1
                    )
                    Util.getTextStyleHtml(
                        String.format(
                            "Total Devoluciones: %s", v.devolucionMes
                        ), textView2
                    )
                    Util.getTextStyleHtml(
                        String.format(
                            "Venta Real: %s", v.ventaRealMes
                        ), textView3
                    )
                    Util.getTextStyleHtml(
                        String.format(
                            "Total Venta del Dia: %s", v.ventaDia
                        ), textView4
                    )
                    Util.getTextStyleHtml(
                        String.format(
                            "Total de Pedidos: %s", v.pedidoDia
                        ), textView5
                    )
                }

                reporteSupervisorAdapter.addItems(it)
            }
        })
//        fabMap.setOnClickListener(this)
//        fabResumen.setOnClickListener(this)
    }

    private fun dialogReporteResumen(id: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_reporte_resumen, null)
        val tableVenta: TableLayout = view.findViewById(R.id.tableVenta)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        ventasViewModel.clearReporteMes()
        ventasViewModel.syncReporteMes(id)
        ventasViewModel.reporteMes.observe(this, {
            if (it != null) {
                getData(tableVenta, it)
            }
        })
    }

    private fun getData(table: TableLayout, data: List<VentaMes>) {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0
        val textSize = 15
        val smallTextSize = 15
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
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize.toFloat())
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

    @SuppressLint("RestrictedApi")
    private fun showPopupMenu(v: View, s: VentaSupervisor) {
        val popup = PopupMenu(v.context, v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.supervisor, popup.menu)

        val menuBuilder = popup.menu as MenuBuilder
        menuBuilder.setOptionalIconsVisible(true)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.map -> {
                    startActivity(
                        Intent(
                            this@ReporteSupervisorActivity,
                            VendedorMapActivity::class.java
                        )
                            .putExtra("title", s.vendedor)
                            .putExtra("id", s.vendedorId)
                    )
                    true
                }
                else -> {
                    dialogReporteResumen(s.vendedorId)
                    true
                }
            }
        }
        popup.show()
    }
}