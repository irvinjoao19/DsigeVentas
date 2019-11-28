package com.dsige.dsigeventas.data.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dsige.dsigeventas.data.viewModel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UsuarioViewModel::class)
    internal abstract fun bindUserViewModel(usuarioViewModel: UsuarioViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ClienteViewModel::class)
    internal abstract fun bindClienteViewModel(clienteViewModel: ClienteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProductoViewModel::class)
    internal abstract fun bindProductoViewModel(clienteViewModel: ProductoViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}