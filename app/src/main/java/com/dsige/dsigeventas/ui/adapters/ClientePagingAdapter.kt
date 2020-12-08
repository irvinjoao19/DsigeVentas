package com.dsige.dsigeventas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_client.view.*

class ClientePagingAdapter(private var listener: OnItemClickListener.ClienteListener) :
    RecyclerView.Adapter<ClientePagingAdapter.ViewHolder>() {

    private var count = emptyList<Cliente>()

    fun addItems(list: List<Cliente>) {
        count = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_client, parent, false)
        return ViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(count[position], listener)
    }

    override fun getItemCount(): Int {
        return count.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Cliente, listener: OnItemClickListener.ClienteListener) =
            with(itemView) {
                textViewNombre.text = s.nombreCliente
                textViewDocumento.text = s.documento
                textViewDireccion.text = s.direccion
                textViewDistrito.text = s.nombreDistrito
                textViewVisita.text = String.format("Ultima Visita : %s", s.fechaVisita)
                itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
                imageViewMap.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
            }
    }
}