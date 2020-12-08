package com.dsige.dsigeventas.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Pedido
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_pedido.view.*

class PedidoPagingAdapter(private var listener: OnItemClickListener.PedidoListener) :
    PagedListAdapter<Pedido, PedidoPagingAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_pedido, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Pedido, listener: OnItemClickListener.PedidoListener) =
            with(itemView) {
                textViewNroPedido.text = String.format("Nro Pedido : %s", s.pedidoId)
                textViewCliente.text = s.nombreCliente
                textViewTotalPedido.text = String.format("Total pedido : S/. %.2f",s.totalNeto)
                when (s.estado) {
                    0 -> textViewEstado.text = String.format("Estado : %s", "Por Enviar")
                    1 -> textViewEstado.text = String.format("Estado : %s", "Enviado")
                    else -> textViewEstado.text = String.format("Estado : %s", "Enviado")

                }
                itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
                imageViewMap.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Pedido>() {
            override fun areItemsTheSame(oldItem: Pedido, newItem: Pedido): Boolean =
                oldItem.pedidoId == newItem.pedidoId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Pedido, newItem: Pedido): Boolean =
                oldItem == newItem
        }
    }
}