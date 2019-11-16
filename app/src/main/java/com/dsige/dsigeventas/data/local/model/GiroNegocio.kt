package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class GiroNegocio {
    @PrimaryKey(autoGenerate = true)
    var negocioId: Int = 0
    var nombre: String = ""
}