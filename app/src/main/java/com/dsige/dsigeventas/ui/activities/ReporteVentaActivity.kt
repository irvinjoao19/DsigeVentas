package com.dsige.dsigeventas.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.VentaVendedor
import com.dsige.dsigeventas.data.viewModel.ReporteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_reporte_venta.*
import javax.inject.Inject

class ReporteVentaActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var ventasViewModel: ReporteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_venta)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getString("title")!!, b.getInt("id"))
        }
    }

    private fun bindUI(title: String, id: Int) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title =title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        ventasViewModel =
            ViewModelProvider(this, viewModelFactory).get(ReporteViewModel::class.java)
        ventasViewModel.syncReporteVenta(id)

        ventasViewModel.reporte.observe(this, {
            if (it.isNotEmpty()) {
                val v: VentaVendedor? = it[0]
                if (v != null) {
                    Util.getTextStyleHtml(
                        String.format(
                            "<font color='%s'>Venta del Mes: %s</font>", "#03A9F4", v.ventaMes
                        ), textView1
                    )
                    Util.getTextStyleHtml(
                        String.format(
                            "Total Devoluciones: %s", v.devolucion
                        ), textView2
                    )
                    Util.getTextStyleHtml(
                        String.format(
                            "Venta Real: %s", v.ventaReal
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

                val count: Int = tableVenta.childCount
                for (i in 2 until count) {
                    val child: View = tableVenta.getChildAt(i)
                    if (child is TableRow) {
                        (child as ViewGroup).removeAllViews()
                        (child as ViewGroup).visibility = View.GONE
                    }
                }
                getData(it)
            }
        })
    }

    private fun getData(data: List<VentaVendedor>) {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0
        val textSize = 15
        val smallTextSize = 15
        val rows = data.size

        for (i in 0 until rows) {
            val row: VentaVendedor = data[i]
            val tv = TextView(this)
            tv.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT
            )
            tv.gravity = Gravity.CENTER
            tv.setPadding(5, 15, 0, 5)
            tv.setBackgroundColor(Color.parseColor("#f8f8f8"))
            tv.text = row.fechaEmision
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
            tableVenta.addView(tr, trParams)
        }
    }
}