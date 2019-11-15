package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Producto {

    @PrimaryKey(autoGenerate = true)
    var productoId: Int = 0
    var categoriaId: Int = 0
    var pedidoId: Int = 0
    var codigo: String = ""
    var codigoBarra: String = ""
    var nombre: String = ""
    var descripcion: String = ""
    var precioCompra: Double = 0.0
    var precioVenta: Double = 0.0
    var abreviaturaProducto: String = ""
    var unidadMedida: Double = 0.0
    var urlFoto: String = ""
    var peso: Double = 0.0
    var stockMinimo: Int = 0
    var estado: Int = 0
    var subTotal: Double = 0.0
    var fecha: String = ""
    var isSelected: Boolean = false

}