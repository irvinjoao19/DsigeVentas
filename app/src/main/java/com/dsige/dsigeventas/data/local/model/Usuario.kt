package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Usuario {
    @PrimaryKey(autoGenerate = true)
    var usuarioId: Int = 0
    var documento: String = ""
    var apellidos: String = ""
    var nombres: String = ""
    var tipo: Int = 0
    var cargoId: Int = 0
    var nombreCargo: String = ""
    var telefono: String = ""
    var email: String = ""
    var login: String = ""
    var pass: String = ""
    var envioOnline: String = ""
    var perfil: Int = 0
    var descripcionPerfil: String = ""
    var estado: Int = 0
}