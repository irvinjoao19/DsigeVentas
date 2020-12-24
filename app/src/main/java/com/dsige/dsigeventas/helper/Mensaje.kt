package com.dsige.dsigeventas.helper

open class Mensaje {

    var codigoBase: Int = 0
    var codigoRetorno: Int = 0
    var codigoBaseCliente: Int = 0
    var codigoRetornoCliente: Int = 0
    var mensaje: String = ""
    var stock: Double = 0.0
    var detalle: List<MensajeDetalle>? = null
}