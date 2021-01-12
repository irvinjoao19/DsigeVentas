package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class VentaUbicacionVendedor {
    @PrimaryKey
    var id: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var operarioId: Int = 0
    var vendedor: String = ""
    var total: Double = 0.0
    var totalPedidos: Int = 0
}