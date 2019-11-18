package com.dsige.dsigeventas.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_client.view.*

class ClientePagingAdapter(private var listener: OnItemClickListener.ClienteListener) :
    PagedListAdapter<Cliente, ClientePagingAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_client, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Cliente, listener: OnItemClickListener.ClienteListener) =
            with(itemView) {
                textViewNombre.text = s.nombreCliente
                textViewDocumento.text = s.documento
                textViewDireccion.text = s.direccion
                textViewVisita.text = String.format("Ultima Visita : %s", s.fechaVisita)
                itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
                imageViewMap.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Cliente>() {
            override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean =
                oldItem.clienteId == newItem.clienteId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean =
                oldItem == newItem
        }
    }
}