package com.dsige.dsigeventas.data.module

import com.dsige.dsigeventas.ui.activities.MainActivity
import com.dsige.dsigeventas.ui.activities.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Main::class])
    internal abstract fun bindMainActivity(): MainActivity

//    @ContributesAndroidInjector
//    internal abstract fun bindStartActivity(): StartActivity
//
//    @ContributesAndroidInjector
//    internal abstract fun bindSuministroActivity(): SuministroActivity
//
//    @ContributesAndroidInjector
//    internal abstract fun bindGroupMapActivity(): GroupMapActivity
//
//    @ContributesAndroidInjector
//    internal abstract fun bindFormSuministroActivity(): FormSuministroActivity
//
//    @ContributesAndroidInjector(modules = [FragmentBindingModule.Camera::class])
//    internal abstract fun bindCameraActivity(): CameraActivity
//
//    @ContributesAndroidInjector
//    internal abstract fun bindPreviewCameraActivity(): PreviewCameraActivity
//
//    @ContributesAndroidInjector(modules = [FragmentBindingModule.Inspeccion::class])
//    internal abstract fun bindFormInspeccionActivity(): FormInspeccionActivity
}