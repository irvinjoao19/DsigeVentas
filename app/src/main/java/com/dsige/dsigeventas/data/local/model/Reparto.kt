package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Reparto {

    @PrimaryKey(autoGenerate = true)
    var repartoId: Int = 0
    var numeroPedido: String = ""
    var almacenId: Int = 0
    var descripcion: String = ""
    var personalVendedorId: Int = 0
    var apellidoPersonal: String = ""
    var clienteId: Int = 0
    var apellidoNombreCliente: String = ""
    var direccion: String = ""
    var fechaEntrega: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var numeroDocumento: String = ""
    var subTotal: Double = 0.0
    var estado: Int = 0
    var nombreEstado: String = ""
    var motivoId: Int = 0
    var motivo: String = ""
    var docVTA: String = ""
    var localId: Int = 0

    @Ignore
    var detalle: List<RepartoDetalle>? = null
}