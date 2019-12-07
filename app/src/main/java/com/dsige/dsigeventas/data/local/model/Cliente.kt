package com.dsige.dsigeventas.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Cliente {

    @PrimaryKey(autoGenerate = true)
    var clienteId: Int = 0
    var empresaId: Int = 0
    var codigoInterno: String = ""
    var tipoClienteId: Int = 0
    var tipo: String = ""
    var documentoIdentidad: String = ""
    var documento: String = ""
    var nombreCliente: String = ""
    var departamentoId: Int = 0
    var nombreDepartamento: String = ""
    var provinciaId: Int = 0
    var nombreProvincia: String = ""
    var distritoId: Int = 0
    var nombreDistrito: String = ""
    var giroNegocioId: Int = 0
    var nombreGiroNegocio: String = ""
    var direccion: String = ""
    var nroCelular: String = ""
    var email: String = ""
    var fechaVisita: String = ""
    var motivoNoCompra: String = ""
    var productoInteres: String = ""
    var personalVendedorId: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var condFacturacion: Int = 0
    var estado: Int = 0
    var identity: Int = 0
}