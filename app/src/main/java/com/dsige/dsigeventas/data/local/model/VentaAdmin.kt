package com.dsige.dsigeventas.data.local.model

open class VentaAdmin {
    var localId: Int = 0
    var vendedorId: Int = 0
    var vendedor: String = ""
    var vtaMes: Double = 0.0
    var devMes: Double = 0.0
    var vtaRealMes: Double = 0.0
    var vtaDia: Double = 0.0
    var pedidoDia: Int = 0
    var tipo :Int = 0 // diferenciar si es vendedor(2) o supervisor(1)
}