package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Local {

    @PrimaryKey(autoGenerate = true)
    var localId: Int = 0
    var nombre: String = ""
    var direccion: String = ""
    var estado: Int = 0
}