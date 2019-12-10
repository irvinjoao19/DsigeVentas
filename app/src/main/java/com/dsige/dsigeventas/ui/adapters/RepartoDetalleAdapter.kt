package com.dsige.dsigeventas.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.RepartoDetalle
import com.dsige.dsigeventas.helper.Util
import kotlinx.android.synthetic.main.cardview_reparto_detalle.view.*

class RepartoDetalleAdapter :
    PagedListAdapter<RepartoDetalle, RepartoDetalleAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_reparto_detalle, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(r: RepartoDetalle) =
            with(itemView) {
                textViewProducto.text = r.nombreProducto
                textViewPrecio.text = String.format("S/. %s", r.precioVenta)
                textViewCodigo.text = r.codigoProducto
                textViewSubTotal.setText(
                    Util.getTextHTML("<font color='red'>Sub Total : </font> S/ " + r.total),
                    TextView.BufferType.SPANNABLE
                )
                editTextCantidad.setText(r.cantidad.toString())
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<RepartoDetalle>() {
            override fun areItemsTheSame(
                oldItem: RepartoDetalle,
                newItem: RepartoDetalle
            ): Boolean =
                oldItem.productoId == newItem.productoId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: RepartoDetalle,
                newItem: RepartoDetalle
            ): Boolean =
                oldItem == newItem
        }
    }
}