package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Pedido {

    @PrimaryKey(autoGenerate = true)
    var pedidoId: Int = 0
    var usuarioId: Int = 0
    var total: Double = 0.0

    @Ignore
    var productos: List<Producto>? = null
}