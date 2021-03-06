package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Stock {
    @PrimaryKey(autoGenerate = true)
    var productoId: Int = 0
    var codigoProducto: String = ""
    var nombreProducto: String = ""
    var descripcionProducto: String = ""
    var abreviaturaProducto: String = ""
    var stock: Double = 0.0
    var precio: Double = 0.0
    var nombreCategoria: String = ""
    var nombreMarca: String = ""
    var factor: Double = 0.0
    var precio2: Double = 0.0
    var precioMayMenor: Double = 0.0
    var precioMayMayor: Double = 0.0
    var rangoCajaHorizontal: Double = 0.0
    var rangoCajaMayorista: Double = 0.0
    var isSelected: Boolean = false
}