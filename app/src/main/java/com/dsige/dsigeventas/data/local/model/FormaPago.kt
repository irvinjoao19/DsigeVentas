package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class FormaPago {
    @PrimaryKey(autoGenerate = true)
    var formaPagoId: Int = 0
    var descripcion: String = ""
    var diasVencimiento: Int = 0
    var estado: Int = 0
}