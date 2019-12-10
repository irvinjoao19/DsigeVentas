package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Grupo {
    @PrimaryKey(autoGenerate = true)
    var detalleTablaId: Int = 0
    var grupoTablaId: Int = 0
    var codigoDetalle: String = ""
    var descripcion: String = ""
    var estado: Int = 0
}