package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class VentaUbicacion {
    @PrimaryKey
    var pedidoCabId: Int = 0
    var clienteId: Int = 0
    var nroDocCliente: String = ""
    var nombreCliente: String = ""
    var direccion: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var total: Double = 0.0
}