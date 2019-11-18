package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Distrito {
    @PrimaryKey
    var distritoId: Int = 0
    var codigoProvincia: String = ""
    var codigoDepartamento: String = ""
    var codigoDistrito: String = ""
    var nombre: String = ""
}