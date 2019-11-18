package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Provincia {
    @PrimaryKey
    var provinciaId : Int = 0
    var codigo: String = ""
    var provincia: String = ""
    var codigoDeparmento: String = ""
}