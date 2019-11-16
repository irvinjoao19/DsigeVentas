package com.dsige.dsigeventas.ui.listeners

import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.dsige.dsigeventas.data.local.model.Categoria
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.local.model.MenuPrincipal
import com.dsige.dsigeventas.data.local.model.Producto
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

    fun onClick(v: View)
}