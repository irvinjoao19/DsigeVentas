package com.dsige.dsigeventas.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Stock
import com.dsige.dsigeventas.data.viewModel.ProductoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.CheckProductoPagingAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_producto.*
import javax.inject.Inject

class ProductoActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var productoViewModel: ProductoViewModel
    var pedidoId: Int = 0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.filter).setVisible(false).isEnabled = false
        menu.findItem(R.id.add).setVisible(false).isEnabled = false
        menu.findItem(R.id.search).setVisible(false).isEnabled = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ok -> productoViewModel.savePedido(pedidoId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)
        val b = intent.extras
        if (b != null) {
            pedidoId = b.getInt("pedidoId")
            bindUI()
        }
    }

    private fun bindUI() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Productos"

        productoViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProductoViewModel::class.java)

        val checkProductoPagingAdapter =
            CheckProductoPagingAdapter(object : OnItemClickListener.CheckProductoListener {
                override fun onCheckedChanged(s: Stock, p: Int, b: Boolean) {
                    if (b) {
                        s.isSelected = true
                        productoViewModel.updateCheckPedido(s)
                    } else {
                        s.isSelected = false
                        productoViewModel.updateCheckPedido(s)
                    }
                }
            })
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = checkProductoPagingAdapter
        productoViewModel.getProductos()
            .observe(this, Observer(checkProductoPagingAdapter::submitList))
        productoViewModel.searchProducto.value = ""
        productoViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
            }
        })
        productoViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
                finish()
            }
        })
    }
}