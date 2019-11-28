package com.dsige.dsigeventas.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Stock
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_product.view.*

class CheckProductoPagingAdapter(
    private var listener: OnItemClickListener.CheckProductoListener
) :
    PagedListAdapter<Stock, CheckProductoPagingAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_product, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Stock, listener: OnItemClickListener.CheckProductoListener) =
            with(itemView) {
                textViewName.text = s.nombreProducto
                textViewCodigo.text = s.codigoProducto
                textViewPrecio.text = s.precio.toString()
                textViewStock.text = s.stock.toString()
                checkboxAdd.visibility = View.VISIBLE
                checkboxAdd.isChecked = s.isSelected
                checkboxAdd.setOnCheckedChangeListener { _, isChecked ->
                    if (checkboxAdd.isPressed) {
                        listener.onCheckedChanged(s, adapterPosition, isChecked)
                    }
                }
                itemView.setOnClickListener{
                    listener.onCheckedChanged(s, adapterPosition, !s.isSelected)
                }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean =
                oldItem.productoId == newItem.productoId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean =
                oldItem == newItem
        }
    }
}