package com.dsige.dsigeventas.data.module

import com.dsige.dsigeventas.ui.activities.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Main::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindRegisterClientActivity(): RegisterClientActivity

    @ContributesAndroidInjector
    internal abstract fun bindFileClientActivity(): FileClientActivity

    @ContributesAndroidInjector
    internal abstract fun bindFileProductoActivity(): FileProductoActivity

    @ContributesAndroidInjector
    internal abstract fun bindProductoActivity(): ProductoActivity

    @ContributesAndroidInjector
    internal abstract fun bindOrdenActivity(): OrdenActivity

    @ContributesAndroidInjector
    internal abstract fun bindMapActivity(): MapsActivity

    @ContributesAndroidInjector
    internal abstract fun bindPersonalMapActivity(): PersonalMapActivity

    @ContributesAndroidInjector
    internal abstract fun bindClientMapActivity(): ClientMapActivity

    @ContributesAndroidInjector
    internal abstract fun bindPreviewCameraActivity(): PreviewCameraActivity

    @ContributesAndroidInjector
    internal abstract fun bindClientGeneralMapActivity(): ClientGeneralMapActivity

    @ContributesAndroidInjector
    internal abstract fun bindRepartoGeneralMapActivity(): RepartoGeneralMapActivity

    @ContributesAndroidInjector
    internal abstract fun bindRepartoActivity(): RepartoActivity
}