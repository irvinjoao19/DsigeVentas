package com.dsige.dsigeventas.data.components

import android.app.Application
import com.dsige.dsigeventas.data.App
import com.dsige.dsigeventas.data.module.ActivityBindingModule
import com.dsige.dsigeventas.data.module.DataBaseModule
import com.dsige.dsigeventas.data.module.RetrofitModule
import com.dsige.dsigeventas.data.module.ServicesModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        RetrofitModule::class,
        DataBaseModule::class,
        ServicesModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}