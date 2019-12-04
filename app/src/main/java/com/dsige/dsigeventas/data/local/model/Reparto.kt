package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
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
}