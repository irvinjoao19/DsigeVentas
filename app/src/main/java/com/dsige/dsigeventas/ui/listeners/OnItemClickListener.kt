package com.dsige.dsigeventas.ui.listeners

import android.view.View
import android.widget.EditText
import com.dsige.dsigeventas.data.local.model.*
import com.google.android.material.checkbox.MaterialCheckBox

interface OnItemClickListener {

    interface ClienteListener {
        fun onItemClick(c: Cliente, v: View, position: Int)
    }

    interface Order {
        fun onItemClick(p: Producto, v: View, e: EditText, position: Int)
    }


    interface Product {
        fun onItemClick(p: Producto, v: View, position: Int)

        fun onCheckedChanged(
            a: Categoria,
            p: Producto,
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

    fun onClick(v: View)
}