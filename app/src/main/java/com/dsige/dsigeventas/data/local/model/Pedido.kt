package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Pedido {

    @PrimaryKey(autoGenerate = true)
    var pedidoId: Int = 0
    var localId: Int = 0
    var empresaId: Int = 0
    var numeroPedido: String = ""
    var codigoInternoSuministro: String = ""
    var almacenId: Int = 0
    var tipoDocumento: Int = 0
    var puntoVentaId: Int = 0
    var cuadrillaId: Int = 0
    var personalVendedorId: Int = 0
    var formaPagoId: Int = 0
    var monedaId: Int = 0
    var tipoCambio: Double = 0.0
    var codigoInternoCliente: String = ""
    var clienteId: Int = 0
    var direccionPedido: String = ""
    var porcentajeIGV: Double = 0.0
    var observacion: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var estado: Int = 0
    var subtotal: Double = 0.0
    var totalIgv: Double = 0.0
    var totalNeto: Double = 0.0
    var numeroDocumento: String = ""
    var fechaFacturaPedido: String = ""

    var nombreCliente: String = ""

    @Ignore
    var detalles: List<PedidoDetalle>? = null
}