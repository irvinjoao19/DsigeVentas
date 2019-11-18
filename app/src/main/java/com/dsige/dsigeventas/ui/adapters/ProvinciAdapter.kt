package com.dsige.dsigeventas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Provincia
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*

class ProvinciAdapter(private val listener: OnItemClickListener.ProvinciaListener) :
    RecyclerView.Adapter<ProvinciAdapter.ViewHolder>() {

    private var count = emptyList<Provincia>()

    fun addItems(list: List<Provincia>) {
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
        fun bind(m: Provincia, listener: OnItemClickListener.ProvinciaListener) = with(itemView) {
            textViewTitulo.text = m.provincia
            itemView.setOnClickListener { v -> listener.onItemClick(m, v, adapterPosition) }
        }
    }
}