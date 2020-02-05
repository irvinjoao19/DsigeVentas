package com.dsige.dsigeventas.ui.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Stock
import com.dsige.dsigeventas.data.viewModel.ProductoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.databinding.ActivityFileProductoBinding
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class FileProductoActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var productoViewModel: ProductoViewModel
    lateinit var p: Stock
    lateinit var binding: ActivityFileProductoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_file_producto)
        binding.lifecycleOwner = this
        productoViewModel =
            ViewModelProvider(this, viewModelFactory).get(ProductoViewModel::class.java)

        val b = intent.extras
        if (b != null) {
            p = Stock()
            bindUI(b.getInt("productoId"))
        }
    }

    private fun bindUI(id:Int){
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Producto"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.s = productoViewModel
        productoViewModel.getProductoById(id).observe(this, Observer<Stock> { stock ->
            if (stock != null) {
               p = stock
                productoViewModel.setProducto(p)
            }
        })
    }
}
