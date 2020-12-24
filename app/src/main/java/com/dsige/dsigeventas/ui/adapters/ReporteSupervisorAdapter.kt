package com.dsige.dsigeventas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.VentaSupervisor
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_reporte_supervisor.view.*

class ReporteSupervisorAdapter(var listener: OnItemClickListener.VentaSupervisorListener) :
    RecyclerView.Adapter<ReporteSupervisorAdapter.ViewHolder>() {

    private var count = emptyList<VentaSupervisor>()

    fun addItems(list: List<VentaSupervisor>) {
        count = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_reporte_supervisor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(count[position], listener)
    }

    override fun getItemCount(): Int {
        return count.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(
            r: VentaSupervisor, listener: OnItemClickListener.VentaSupervisorListener
        ) =
            with(itemView) {
                textView1.text = r.vendedor
                Util.getTextStyleHtml(
                    String.format(
                        "<font color='%s'>Venta del Mes: %s</font>", "#03A9F4", r.totalVtaMes
                    ), textView2
                )
                Util.getTextStyleHtml(
                    String.format(
                        "<font color='%s'>Devoluciones: %s</font>", "#B71C1C", r.totalDevMes
                    ), textView3
                )
                Util.getTextStyleHtml(
                    String.format(
                        "<font color='%s'>Ventas Reales: %s</font>", "#03A9F4", r.totalVtaRealMes
                    ), textView4
                )
                Util.getTextStyleHtml(
                    String.format(
                        "Venta del Dia: %s", r.totalVtaDia
                    ), textView5
                )
                Util.getTextStyleHtml(
                    String.format(
                        "Pedidos Dia: %s", r.totalPedidoDia
                    ), textView6
                )
                itemView.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
            }
    }
}