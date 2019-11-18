package com.dsige.dsigeventas.data.local.model

open class Sync {
    var identidades: List<Identidad> = ArrayList()
    var departamentos: List<Departamento> = ArrayList()
    var provincias: List<Provincia> = ArrayList()
    var distritos: List<Distrito> = ArrayList()
    var negocios: List<GiroNegocio> = ArrayList()
    var productos: List<Stock> = ArrayList()
    var clientes: List<Cliente> = ArrayList()
    var mensaje: String = ""
}