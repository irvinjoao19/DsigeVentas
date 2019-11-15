package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Categoria {

    @PrimaryKey(autoGenerate = true)
    var categoriaId: Int = 0
    var nombre: String = ""

    @Ignore
    var productos: List<Producto>? = null
}