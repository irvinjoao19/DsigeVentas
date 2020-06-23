package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Departamento {
    @PrimaryKey
    var departamentoId: Int = 0
    var codigo: String = ""
    var departamento: String = ""
}