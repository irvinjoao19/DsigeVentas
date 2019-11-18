package com.dsige.dsigeventas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Distrito
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*

class DistritoAdapter(private val listener: OnItemClickListener.DistritoListener) :
    RecyclerView.Adapter<DistritoAdapter.ViewHolder>() {

    private var count = emptyList<Distrito>()

    fun addItems(list: List<Distrito>) {
        count = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(count[position], listener)
    }

    override fun getItemCount(): Int {
        return count.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: Distrito, listener: OnItemClickListener.DistritoListener) = with(itemView) {
            textViewTitulo.text = m.nombre
            itemView.setOnClickListener { v -> listener.onItemClick(m, v, adapterPosition) }
        }
    }
}