package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Cliente {

    @PrimaryKey(autoGenerate = true)
    var clienteId: Int = 0
    var tipo: String = ""
    var documento: String = ""
    var nombre: String = ""
    var pago: String = ""
    var departamento: String = ""
    var distrito: String = ""
    var direccion: String = ""
    var telefono: String = ""
    var email: String = ""
    var fechaVisita: String = ""
    var estado: Int = 0
}