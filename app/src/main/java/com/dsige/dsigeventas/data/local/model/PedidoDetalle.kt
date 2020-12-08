package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class PedidoDetalle {

    @PrimaryKey(autoGenerate = true)
    var pedidoDetalleId: Int = 0
    var pedidoId: Int = 0
    var productoId: Int = 0
    //    var categoriaId: Int = 0
    var codigo: String = ""
    //    var codigoBarra: String = ""
    var nombre: String = ""
    var descripcion: String = ""
    //    var precioCompra: Double = 0.0
    var abreviaturaProducto: String = ""
    var unidadMedida: Double = 0.0
    //    var urlFoto: String = ""
//    var peso: Double = 0.0
    var stockMinimo: Double = 0.0
    var subTotal: Double = 0.0

    //    var fecha: String = ""
    var pedidoItem: Int = 0
    var precioVenta: Double = 0.0 // se enviara como guardado
    var porcentajeDescuento: Double = 0.0
    var descuentoPedido: Double = 0.0
    var cantidad: Double = 0.0
    var porcentajeIGV: Double = 0.0
    var totalPedido: Double = 0.0
    var numeroPedido: String = ""
    var estado: Int = 0

    var factor: Double = 0.0
    var precio1: Double = 0.0
    var precio2: Double = 0.0
    var precioMayMenor: Double = 0.0
    var precioMayMayor: Double = 0.0
    var rangoCajaHorizontal: Double = 0.0
    var rangoCajaMayorista: Double = 0.0

    var identity: Int = 0
    var identityDetalle: Int = 0
    var active: Int = 0
    var localId: Int = 0
    // este campo permitira agregar los productos que se enviaron y llegaron al servidor correctamente
}