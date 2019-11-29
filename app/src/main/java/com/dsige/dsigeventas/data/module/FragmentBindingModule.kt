package com.dsige.dsigeventas.data.module

import com.dsige.dsigeventas.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

abstract class FragmentBindingModule {

    @Module
    abstract class Main {
        @ContributesAndroidInjector
        internal abstract fun providClientFragment(): ClientFragment

        @ContributesAndroidInjector
        internal abstract fun providMapsFragment(): MapsFragment

        @ContributesAndroidInjector
        internal abstract fun providPedidoFragment(): PedidoFragment

        @ContributesAndroidInjector
        internal abstract fun providOrdersFragment(): OrdersFragment

        @ContributesAndroidInjector
        internal abstract fun providProductsFragment(): ProductsFragment


        @ContributesAndroidInjector
        internal abstract fun providInfoFragment(): InfoFragment
    }
}