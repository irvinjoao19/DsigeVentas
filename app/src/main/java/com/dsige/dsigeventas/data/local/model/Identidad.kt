package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Identidad {
    @PrimaryKey
    var codigo: String = ""
    var descripcion: String = ""
}