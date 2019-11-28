package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.local.model.Pedido
import com.dsige.dsigeventas.data.local.model.PedidoDetalle
import com.dsige.dsigeventas.data.viewModel.ProductoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.ProductoPedidoAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_orden.*
import javax.inject.Inject

class OrdenActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var productoViewModel: ProductoViewModel

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null
    var topMenu: Menu? = null
    var clienteId: Int = 0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        topMenu = menu
        menu.findItem(R.id.filter).setVisible(false).isEnabled = false
        menu.findItem(R.id.ok).setVisible(false).isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> startActivity(
                Intent(this, ProductoActivity::class.java).putExtra(
                    "pedidoId",
                    clienteId
                )
            )
            R.id.ok -> productoViewModel.validatePedido(clienteId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orden)

        val b = intent.extras
        if (b != null) {
            clienteId = b.getInt("clienteId")
            bindUI()
        }
    }

    private fun bindUI() {
        productoViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProductoViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Pedido"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        productoViewModel.generarPedidoCliente(clienteId)

        productoViewModel.getPedidoCliente(clienteId).observe(this, Observer<Pedido> { c ->
            if (c != null) {
                textViewNombre.text = c.nombreCliente
                textViewSubTotal.text = String.format("Sub Total : S/. %s", c.subtotal)
                textViewTotal.text = String.format("Total : S/. %s", c.totalNeto)
            }
        })

        val productoPedidoAdapter =
            ProductoPedidoAdapter(object : OnItemClickListener.ProductoPedidoListener {
                override fun onItemClick(p: PedidoDetalle, v: View, position: Int) {
                    when (v.id) {
                        R.id.editTextCantidad -> updateCantidadProducto(p)
                        R.id.imageViewNegative -> {
                            val resta = p.cantidad
                            if (resta != 0.0) {
                                val rTotal = (resta - 1).toString()
                                val nNegative = rTotal.toDouble()
                                if (nNegative == 0.0) {
                                    p.estado = 0
                                }
                                p.cantidad = nNegative
                                p.unidadMedida = nNegative
                                p.subTotal = nNegative * p.precioCompra
                                productoViewModel.updateProducto(p)
                            }
                        }
                        R.id.imageViewPositive -> {
                            val sTotal = (p.cantidad + 1).toString()
                            val nPositive = sTotal.toDouble()
                            p.cantidad = nPositive
                            p.unidadMedida = nPositive
                            p.subTotal = nPositive * p.precioCompra
                            p.estado = 1
                            productoViewModel.updateProducto(p)
                        }
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
        recyclerView.adapter = productoPedidoAdapter

        productoViewModel.getProductoByPedido(clienteId)
            .observe(this, Observer<PagedList<PedidoDetalle>> { p ->
                if (p.size != 0) {
                    topMenu?.findItem(R.id.ok)?.setVisible(true)?.isEnabled = true
                    updateProducto(p)
                    productoPedidoAdapter.submitList(p)
                } else {
                    topMenu?.findItem(R.id.ok)?.setVisible(false)?.isEnabled = false
                }
            })

        productoViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
                loadFinish()
                Util.toastMensaje(this, s)
            }
        })

        productoViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                when (s) {
                    "Ok" -> sendPedido(clienteId)
                    "ENVIADO" -> {
                        loadFinish()
                        Util.toastMensaje(this, s)
                    }
                }
            }
        })
    }

    private fun updateProducto(pedidoDetalles: List<PedidoDetalle>) {
        var subTotal = 0.0
        val total: Double
        val igv: Double
        for (p in pedidoDetalles) {
            subTotal += p.subTotal
        }
        igv = subTotal * 0.18
        total = igv + subTotal
        productoViewModel.updateTotalPedido(clienteId, igv, total, subTotal)
    }

    private fun updateCantidadProducto(p: PedidoDetalle) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.dialog_count_producto, null)
        val editTextProducto: EditText = v.findViewById(R.id.editTextProducto)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        //editTextProducto.setText(p.cantidad.toInt().toString())
        Util.showKeyboard(editTextProducto, this)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        buttonAceptar.setOnClickListener {
            if (editTextProducto.text.toString().isNotEmpty()) {
                val nPositive = editTextProducto.text.toString().toDouble()
                when (nPositive) {
                    0.0 -> p.estado = 0
                    else -> p.estado = 1
                }
                p.cantidad = nPositive
                p.unidadMedida = nPositive
                p.subTotal = nPositive * p.precioCompra
                productoViewModel.updateProducto(p)
                Util.hideKeyboardFrom(this, v)
                dialog.dismiss()
            } else {
                productoViewModel.setError("Digite cantidad")
            }
        }
        buttonCancelar.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun sendPedido(id: Int) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas enviar el pedido ?")
            .setPositiveButton("SI") { dialog, _ ->
                load()
                productoViewModel.sendPedido(id)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textView: TextView = view.findViewById(R.id.textViewLado)
        textView.text = String.format("%s", "Enviando")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun loadFinish() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }
}