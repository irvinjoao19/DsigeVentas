package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Provincia {
    @PrimaryKey
    var codigo: String = ""
    var provinciaId: String = ""
    var codigoDeparmento: String = ""
}