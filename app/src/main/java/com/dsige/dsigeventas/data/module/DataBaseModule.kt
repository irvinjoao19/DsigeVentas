package com.dsige.dsigeventas.data.module

import android.app.Application
import androidx.room.Room
import com.dsige.dsigeventas.data.local.AppDataBase
import com.dsige.dsigeventas.data.local.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(application: Application): AppDataBase {
        if (AppDataBase.INSTANCE == null) {
            synchronized(AppDataBase::class.java) {
                if (AppDataBase.INSTANCE == null) {
                    AppDataBase.INSTANCE = Room.databaseBuilder(
                        application.applicationContext,
                        AppDataBase::class.java, AppDataBase.DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return AppDataBase.INSTANCE!!
    }

    @Provides
    internal fun provideUsuarioDao(appDataBase: AppDataBase): UsuarioDao {
        return appDataBase.usuarioDao()
    }

    @Provides
    internal fun provideDetalleGrupoDao(appDataBase: AppDataBase): ClienteDao {
        return appDataBase.clienteDao()
    }

    @Provides
    internal fun provideRegistroDao(appDataBase: AppDataBase): PersonalDao {
        return appDataBase.personalDao()
    }

    @Provides
    internal fun provideRegistroPhotoDao(appDataBase: AppDataBase): PedidoDetalleDao {
        return appDataBase.pedidoDetalleDao()
    }

    @Provides
    internal fun provideCategoriaDao(appDataBase: AppDataBase): CategoriaDao {
        return appDataBase.categoriaDao()
    }

    @Provides
    internal fun provideDepartamentoDao(appDataBase: AppDataBase): DepartamentoDao {
        return appDataBase.departamentoDao()
    }

    @Provides
    internal fun provideDistritoDao(appDataBase: AppDataBase): DistritoDao {
        return appDataBase.distritoDao()
    }

    @Provides
    internal fun provideGiroNegocioDao(appDataBase: AppDataBase): GiroNegocioDao {
        return appDataBase.giroNegocioDao()
    }

    @Provides
    internal fun provideIdentidadDao(appDataBase: AppDataBase): IdentidadDao {
        return appDataBase.identidadDao()
    }

    @Provides
    internal fun provideProvinciaDao(appDataBase: AppDataBase): ProvinciaDao {
        return appDataBase.provinciaDao()
    }

    @Provides
    internal fun provideStockDao(appDataBase: AppDataBase): StockDao {
        return appDataBase.stockDao()
    }

    @Provides
    internal fun provideInspeccionAdicionalesDao(appDataBase: AppDataBase): PedidoDao {
        return appDataBase.pedidoDao()
    }

    @Provides
    internal fun provideFormaPagoDao(appDataBase: AppDataBase): FormaPagoDao {
        return appDataBase.formaPagoDao()
    }
}