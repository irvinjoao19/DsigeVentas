package com.dsige.dsigeventas.ui.listeners

import android.view.View
import android.widget.EditText
import com.dsige.dsigeventas.data.local.model.*
import com.google.android.material.checkbox.MaterialCheckBox

interface OnItemClickListener {

    interface ClienteListener {
        fun onItemClick(c: Cliente, v: View, position: Int)
    }

    interface ProductoListener {
        fun onItemClick(s: Stock, v: View, position: Int)
    }

    interface CheckProductoListener {
        fun onCheckedChanged(s: Stock, p: Int, b: Boolean)
    }

    interface PedidoListener {
        fun onItemClick(p: Pedido, v: View, position: Int)
    }

    interface ProductoPedidoListener {
        fun onItemClick(p: PedidoDetalle, v: View, position: Int)
    }

    interface RepartoDetalleListener {
        fun onItemClick(r: RepartoDetalle, v: View, position: Int)
    }

    interface Order {
        fun onItemClick(p: PedidoDetalle, v: View, e: EditText, position: Int)
    }

    interface Product {
        fun onItemClick(p: PedidoDetalle, v: View, position: Int)

        fun onCheckedChanged(
            a: Categoria,
            p: PedidoDetalle,
            position: Int,
            b: Boolean?,
            m: MaterialCheckBox
        )
    }

    interface MenuListener {
        fun onItemClick(m: MenuPrincipal, view: View, position: Int)
    }

    interface DepartamentoListener {
        fun onItemClick(d: Departamento, v: View, position: Int)
    }

    interface ProvinciaListener {
        fun onItemClick(p: Provincia, v: View, position: Int)
    }

    interface DistritoListener {
        fun onItemClick(d: Distrito, v: View, position: Int)
    }

    interface FormaPagoListener {
        fun onItemClick(f: FormaPago, v: View, position: Int)
    }

    interface EstadoListener {
        fun onItemClick(e: Estado, v: View, position: Int)
    }

    interface GrupoListener {
        fun onItemClick(g: Grupo, v: View, position: Int)
    }

    interface RepartoListener {
        fun onItemClick(r: Reparto, v: View, position: Int)
    }

    fun onClick(v: View)
}