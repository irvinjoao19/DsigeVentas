package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Personal {

    @PrimaryKey(autoGenerate = true)
    var personalId: Int = 0
    var nombrePersonal: String = ""
    var countPedidos: Int = 0
    var countClientes: Int = 0
    var countProductos: Int = 0
    var total: Double = 0.0
    var latitud: String = ""
    var longitud: String = ""
}