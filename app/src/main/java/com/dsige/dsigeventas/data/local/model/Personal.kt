package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Personal {

    @PrimaryKey(autoGenerate = true)
    var personalId: Int = 0
    var cargoId: Int = 0
    var nombrePersonal: String = ""
    var nombreCargo: String = ""
    var estado: Int = 0

}