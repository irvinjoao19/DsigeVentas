package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class RepartoDetalle {
    @PrimaryKey(autoGenerate = true)
    var detalleId: Int = 0
    var repartoId: Int = 0
    var pedidoItem: Int = 0
    var productoId: Int = 0
    var precioVenta: Double = 0.0
    var porcentajeDescuento: Double = 0.0
    var descuento: Double = 0.0
    var cantidad: Double = 0.0
    var porcentajeIGV: Double = 0.0
    var total: Double = 0.0
    var numeroPedido: String = ""
    var nombreProducto: String = ""
    var codigoProducto: String = ""
}