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
import kotlinx.android.synthetic.main.cardview_combo.view.*

class RepartoSearchAdapter(private val listener: OnItemClickListener.RepartoListener) :
    PagedListAdapter<Reparto, RepartoSearchAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(s: Reparto, listener: OnItemClickListener.RepartoListener) =
            with(itemView) {
                textViewTitulo.text = s.apellidoNombreCliente
                itemView.setOnClickListener { v -> listener.onItemClick(s, v, adapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Reparto>() {
            override fun areItemsTheSame(oldItem: Reparto, newItem: Reparto): Boolean =
                oldItem.clienteId == newItem.clienteId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Reparto, newItem: Reparto): Boolean =
                oldItem == newItem
        }
    }
}