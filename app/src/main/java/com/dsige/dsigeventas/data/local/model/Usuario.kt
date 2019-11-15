package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Usuario {

    @PrimaryKey(autoGenerate = true)
    var usuarioId: Int = 0
    var documento: String = ""
    var nombre: String = ""
    var email: String = ""
    var login: String = ""
    var pass: String = ""
    var estado: Int = 0
    var mensaje: String = ""
}