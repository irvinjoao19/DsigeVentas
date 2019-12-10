package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Estado {
    @PrimaryKey(autoGenerate = true)
    var estadoId: Int = 0
    var nombre: String = ""
    var descripcion: String = ""
    var tipoProceso: String = ""
    var descripcionTipoProceso: String = ""
    var moduloId: Int = 0
    var backColor: Int = 0
    var forecolor: String = ""
    var estado: Int = 0
}