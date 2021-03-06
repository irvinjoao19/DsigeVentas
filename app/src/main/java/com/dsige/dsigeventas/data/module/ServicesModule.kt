package com.dsige.dsigeventas.data.module

import com.dsige.dsigeventas.ui.broadcasts.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServicesModule {

    @ContributesAndroidInjector
    internal abstract fun provideMainReceiver(): MovilReceiver

    @ContributesAndroidInjector
    internal abstract fun provideGpsReceiver(): GpsReceiver

    //@ContributesAndroidInjector
    //internal abstract fun provideRegistroServices(): RegistroServices
}