package com.dsige.dsigeventas.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Reparto
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_reparto.view.*
import kotlinx.android.synthetic.main.cardview_reparto.view.imageViewMap
import kotlinx.android.synthetic.main.cardview_reparto.view.textViewCliente
import kotlinx.android.synthetic.main.cardview_reparto.view.textViewDireccion
import kotlinx.android.synthetic.main.cardview_reparto_map.view.*

class RepartoAdapter(private var listener: OnItemClickListener.RepartoListener) :
    PagedListAdapter<Reparto, RepartoAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_reparto, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(r: Reparto, listener: OnItemClickListener.RepartoListener) =
            with(itemView) {
                textViewNroDocumento.text = String.format("Nro : %s", r.numeroPedido)
                textViewCliente.text = r.apellidoNombreCliente
                textViewDireccion.text = r.direccion
                textViewOrden.text = String.format("Orden %s", adapterPosition)
                imageViewFactura.visibility = View.GONE
                imageViewMap.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Reparto>() {
            override fun areItemsTheSame(oldItem: Reparto, newItem: Reparto): Boolean =
                oldItem.repartoId == newItem.repartoId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Reparto, newItem: Reparto): Boolean =
                oldItem == newItem
        }
    }
}