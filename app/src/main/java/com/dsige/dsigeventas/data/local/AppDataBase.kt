package com.dsige.dsigeventas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dsige.dsigeventas.data.local.dao.*
import com.dsige.dsigeventas.data.local.model.*

@Database(
    entities = [
        Usuario::class,
        Producto::class,
        Personal::class,
        Pedido::class,
        Cliente::class,
        Categoria::class,
        Departamento::class,
        Distrito::class,
        GiroNegocio::class,
        Identidad::class,
        Provincia::class,
        Stock::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
    abstract fun personalDao(): PersonalDao
    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun departamentoDao(): DepartamentoDao
    abstract fun distritoDao(): DistritoDao
    abstract fun giroNegocioDao(): GiroNegocioDao
    abstract fun identidadDao(): IdentidadDao
    abstract fun provinciaDao(): ProvinciaDao
    abstract fun stockDao(): StockDao

    companion object {
        @Volatile
        var INSTANCE: AppDataBase? = null
        val DB_NAME = "ventas_db"
    }


    fun getDatabase(context: Context): AppDataBase {
        if (INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java, "ventas_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return INSTANCE!!
    }
}