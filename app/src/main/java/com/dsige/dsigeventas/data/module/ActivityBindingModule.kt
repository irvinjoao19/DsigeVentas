package com.dsige.dsigeventas.data.module

import com.dsige.dsigeventas.ui.activities.FileClientActivity
import com.dsige.dsigeventas.ui.activities.MainActivity
import com.dsige.dsigeventas.ui.activities.LoginActivity
import com.dsige.dsigeventas.ui.activities.RegisterClientActivity
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

}