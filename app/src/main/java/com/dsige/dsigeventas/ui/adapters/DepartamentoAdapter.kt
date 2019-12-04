package com.dsige.dsigeventas.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Departamento
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*
import java.util.*
import kotlin.collections.ArrayList

class DepartamentoAdapter(private val listener: OnItemClickListener.DepartamentoListener) :
    RecyclerView.Adapter<DepartamentoAdapter.ViewHolder>() {

    private var count = emptyList<Departamento>()
    private var countList: ArrayList<Departamento> = ArrayList()

    fun addItems(list: List<Departamento>) {
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
        fun bind(m: Departamento, listener: OnItemClickListener.DepartamentoListener) =
            with(itemView) {
                textViewTitulo.text = m.departamento
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
                    val filteredList = ArrayList<Departamento>()
                    for (m: Departamento in count) {
                        if (m.departamento.toLowerCase(Locale.getDefault()).contains(keyword)
                        ) {
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