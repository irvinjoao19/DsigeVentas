package com.dsige.dsigeventas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Departamento
import com.dsige.dsigeventas.data.local.model.Distrito
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*
import java.util.*
import kotlin.collections.ArrayList

class DistritoAdapter(private val listener: OnItemClickListener.DistritoListener) :
    RecyclerView.Adapter<DistritoAdapter.ViewHolder>() {

    private var count = emptyList<Distrito>()
    private var countList: ArrayList<Distrito> = ArrayList()

    fun addItems(list: List<Distrito>) {
        count = list
        countList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(countList[position], listener)
    }

    override fun getItemCount(): Int {
        return countList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: Distrito, listener: OnItemClickListener.DistritoListener) = with(itemView) {
            textViewTitulo.text = m.nombre
            itemView.setOnClickListener { v -> listener.onItemClick(m, v, adapterPosition) }
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                countList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    countList.addAll(count)
                } else {
                    val filteredList = ArrayList<Distrito>()
                    for (m: Distrito in count) {
                        if (m.nombre.toLowerCase(Locale.getDefault()).contains(keyword)) {
                            filteredList.add(m)
                        }
                    }
                    countList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }
}