package com.dsige.dsigeventas.data.local.model

open class Resumen {

    var totalVenta: Double = 0.0
    var countPedidoVenta: Int = 0
    var countClientes: Int = 0
    var vendedorId: Int = 0
    var mejorVendedor: String = ""
    var mejorVendedorSoles: Double = 0.0
    var productoId: Int = 0
    var mejorProducto: String = ""
    var mejorProductoSoles: Double = 0.0
    var totalDevolucion: Double = 0.0
    var peorVendedorId: Int = 0
    var peorVendedor: String = ""
    var peorVendedorSoles: Double = 0.0
}