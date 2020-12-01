package com.dsige.dsigeventas.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.PedidoDetalle
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_pedidos.view.*
import java.text.DecimalFormat

class ProductoPedidoAdapter(private var listener: OnItemClickListener.ProductoPedidoListener) :
    PagedListAdapter<PedidoDetalle, ProductoPedidoAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_pedidos, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(p: PedidoDetalle, listener: OnItemClickListener.ProductoPedidoListener) =
            with(itemView) {
                textViewNombre.text = p.nombre
                textViewCodigo.text = p.codigo
                textViewPrecio.text = p.precioVenta.toString()
                textViewSubTotal.text =
                    String.format("S/. %.6f", p.subTotal)
                textViewStock.text = p.stockMinimo.toString()
                editTextCantidad.setText(p.cantidad.toString())

                if (p.estado == 1) {
                    imageViewPositive.visibility = View.GONE
                    imageViewNegative.visibility = View.GONE
                } else {
                    editTextCantidad.setOnClickListener { v ->
                        listener.onItemClick(p, v, adapterPosition)
                    }
                    imageViewNegative.setOnClickListener { v ->
                        listener.onItemClick(p, v, adapterPosition)
                    }
                    imageViewPositive.setOnClickListener { v ->
                        listener.onItemClick(p, v, adapterPosition)
                    }
                    itemView.setOnClickListener { v -> listener.onItemClick(p, v, adapterPosition) }
                }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PedidoDetalle>() {
            override fun areItemsTheSame(oldItem: PedidoDetalle, newItem: PedidoDetalle): Boolean =
                oldItem.productoId == newItem.productoId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: PedidoDetalle,
                newItem: PedidoDetalle
            ): Boolean =
                oldItem == newItem
        }
    }
}