package com.dsige.dsigeventas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Departamento
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*

class DepartamentoAdapter(private val listener: OnItemClickListener.DepartamentoListener) :
    RecyclerView.Adapter<DepartamentoAdapter.ViewHolder>() {

    private var count = emptyList<Departamento>()

    fun addItems(list: List<Departamento>) {
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
        fun bind(m: Departamento, listener: OnItemClickListener.DepartamentoListener) = with(itemView) {
            textViewTitulo.text = m.departamento
            itemView.setOnClickListener { v -> listener.onItemClick(m, v, adapterPosition) }
        }
    }
}