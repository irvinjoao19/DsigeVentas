package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.local.model.Pedido
import com.dsige.dsigeventas.data.local.model.PedidoDetalle
import com.dsige.dsigeventas.data.viewModel.ProductoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Gps
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.ClienteAdapter
import com.dsige.dsigeventas.ui.adapters.ProductoPedidoAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_orden.*
import javax.inject.Inject

class OrdenActivity : DaggerAppCompatActivity(), View.OnClickListener,
    TextView.OnEditorActionListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imageViewAddPerson -> startActivity(
                Intent(this, RegisterClientActivity::class.java)
                    .putExtra("clienteId", 0)
            )
        }
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (v.text.toString().isNotEmpty()) {
            personalSearch(v.text.toString())
        }
        return false
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var productoViewModel: ProductoViewModel

    lateinit var productoPedidoAdapter: ProductoPedidoAdapter
    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null
    var topMenu: Menu? = null
    var clienteId: Int = 0
    var pedidoId: Int = 0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        topMenu = menu
        menu.findItem(R.id.filter).setVisible(false).isEnabled = false
        menu.findItem(R.id.search).setVisible(false).isEnabled = false
        menu.findItem(R.id.logout).setVisible(false).isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> if (clienteId != 0) {
                startActivity(
                    Intent(this, ProductoActivity::class.java)
                        .putExtra("pedidoId", pedidoId)
                )
            } else {
                productoViewModel.setError("Eliga un cliente")
            }
            R.id.ok -> productoViewModel.validatePedido(pedidoId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orden)
        val b = intent.extras
        if (b != null) {
            pedidoId = b.getInt("pedidoId")
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
        imageViewAddPerson.setOnClickListener(this)
        editTextTipo.setOnEditorActionListener(this)

        productoPedidoAdapter =
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
                                p.subTotal = nNegative * p.precioVenta
                                p.totalPedido = p.subTotal
                                productoViewModel.updateProducto(p)
                            }
                        }
                        R.id.imageViewPositive -> {
                            val sTotal = (p.cantidad + 1).toString()
                            val nPositive = sTotal.toDouble()
                            p.cantidad = nPositive
                            p.unidadMedida = nPositive
                            p.subTotal = nPositive * p.precioVenta
                            p.totalPedido = p.subTotal
                            p.estado = 2
                            productoViewModel.updateProducto(p)
                        }
                        else -> {
                            val popupMenu = PopupMenu(this@OrdenActivity, v)
                            popupMenu.menu.add(0, 1, 0, getText(R.string.delete))
                            popupMenu.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    1 -> deletePedidoDialog(p)
                                }
                                false
                            }
                            popupMenu.show()
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

        productoViewModel.pedidoId.value = pedidoId

        productoViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
                loadFinish()
                Util.toastMensaje(this, s)
            }
        })

        productoViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                when (s) {
                    "Ok" -> sendPedido(pedidoId,1)
                    "Cliente" ->  sendPedido(pedidoId,0)
                    "ENVIADO" -> {
                        loadFinish()
                        Util.toastMensaje(this, s)
                        finish()
                    }
                }
            }
        })

        productoViewModel.pedidoId.observe(this, Observer<Int> { i ->
            if (i != 0) {
                linearLayoutCliente.visibility = View.GONE
                pedidoId = i
                productoViewModel.getPedidoCliente(i)
                    .observe(this@OrdenActivity, Observer<Pedido> { p ->
                        if (p != null) {
                            textViewNombre.text = p.nombreCliente
                            textViewTotal.text = String.format("Total : S/. %.2f", p.totalNeto)
                            if (p.estado == 1) {
                                topMenu?.findItem(R.id.add)?.setVisible(false)?.isEnabled = false
                                topMenu?.findItem(R.id.ok)?.setVisible(false)?.isEnabled = false
                            }
                        }
                    })
                productoViewModel.getProductoByPedido(i)
                    .observe(this@OrdenActivity, Observer<PagedList<PedidoDetalle>> { p ->
                        if (p.size != 0) {
                            updateProducto(p)
                            productoPedidoAdapter.submitList(p)
                        }
                    })
            }
        })
        if (pedidoId == 0) {
            if (clienteId != 0) {
                generateCliente(clienteId)
            }
        }
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
        productoViewModel.updateTotalPedido(pedidoId, igv, total, subTotal)
    }

    private fun updateCantidadProducto(p: PedidoDetalle) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(this).inflate(R.layout.dialog_count_producto, null)
        val editTextProducto: EditText = v.findViewById(R.id.editTextProducto)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        //editTextProducto.setText(p.cantidad.toString())
        Util.showKeyboard(editTextProducto, this)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        buttonAceptar.setOnClickListener {
            if (editTextProducto.text.toString().isNotEmpty()) {
                val nPositive = editTextProducto.text.toString().toDouble()
                if (nPositive == 0.0) {
                    p.estado = 0
                }
                p.cantidad = nPositive
                p.unidadMedida = nPositive
                p.subTotal = nPositive * p.precioVenta
                p.totalPedido = p.subTotal
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

    private fun sendPedido(id: Int,tipo:Int) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas enviar el pedido ?")
            .setPositiveButton("SI") { dialog, _ ->
                load()
                if (tipo == 0){
                    productoViewModel.validateCliente(id)
                }else{
                    productoViewModel.sendPedido(id)
                }
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

    private fun personalSearch(s: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_spinner, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = layoutManager
        textViewTitle.text = String.format("%s", "Buscar Personal")

        val clienteAdapter =
            ClienteAdapter(object : OnItemClickListener.ClienteListener {
                override fun onItemClick(c: Cliente, v: View, position: Int) {
                    generateCliente(c.clienteId)
                    dialog.dismiss()
                }
            })
        recyclerView.adapter = clienteAdapter
        productoViewModel.personalSearch(s).observe(this, Observer(clienteAdapter::submitList))
    }

    private fun generateCliente(id: Int) {
        val gps = Gps(this@OrdenActivity)
        if (gps.isLocationEnabled()) {
            editTextTipo.text = null
            clienteId = id
            productoViewModel.generarPedidoCliente(
                gps.getLatitude().toString(), gps.getLongitude().toString(), id
            )
        } else {
            gps.showSettingsAlert(this@OrdenActivity)
        }
    }

    private fun deletePedidoDialog(p: PedidoDetalle) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas eliminar el producto ?")
            .setPositiveButton("SI") { dialog, _ ->
                productoViewModel.deletePedidoDetalle(p)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }
}