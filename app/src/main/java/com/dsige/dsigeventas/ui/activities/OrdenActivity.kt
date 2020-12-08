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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
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
                    .putExtra("usuarioId", usuarioId)
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
    private var dialog: AlertDialog? = null
    private var topMenu: Menu? = null
    private var clienteId: Int = 0
    private var pedidoId: Int = 0
    private var tipoPersonal: Int = 0
    private var localId: Int = 0
    private var usuarioId: Int = 0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        topMenu = menu
        menu.findItem(R.id.map).setVisible(false).isEnabled = false
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
                        .putExtra("localId", localId)
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
            tipoPersonal = b.getInt("tipoPersonal")
            localId = b.getInt("localId")
            usuarioId = b.getInt("usuarioId")
            bindUI()
        }
    }

    private fun bindUI() {
        productoViewModel =
            ViewModelProvider(this, viewModelFactory).get(ProductoViewModel::class.java)

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
                                calculoPedido(nNegative, p)
                            }
                        }
                        R.id.imageViewPositive -> {
                            val sTotal = (p.cantidad + 1).toString()
                            val nPositive = sTotal.toDouble()
                            p.estado = 2
                            calculoPedido(nPositive, p)
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
        productoViewModel.mensajeError.observe(this, { s ->
            if (s != null) {
                loadFinish()
                Util.toastMensaje(this, s)
            }
        })

        productoViewModel.mensajeSuccess.observe(this, { s ->
            if (s != null) {
                when (s) {
                    "Ok" -> sendPedido(pedidoId)
                    "ENVIADO" -> {
                        loadFinish()
                        Util.toastMensaje(this, s)
                        finish()
                    }
                    else -> Util.toastMensaje(this, s)
                }
            }
        })

        productoViewModel.pedidoId.observe(this, { i ->
            if (i != 0) {
                linearLayoutCliente.visibility = View.GONE
                pedidoId = i
                productoViewModel.getPedidoCliente(i)
                    .observe(this@OrdenActivity, { p ->
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
                    .observe(this@OrdenActivity, { p ->
                        if (p.size != 0) {
                            updateProducto(p)
                            productoPedidoAdapter.addItems(p)
                        }
                    })
            }
        })
        if (pedidoId == 0) {
            if (clienteId != 0) {
                load("Generando pedido...")
                generateCliente(clienteId, tipoPersonal)
            }
        }
    }

    private fun updateProducto(pedidoDetalles: List<PedidoDetalle>) {
        var total = 0.0
        val igv: Double
        for (p in pedidoDetalles) {
            total += p.subTotal
        }
        igv = total * 0.18
        productoViewModel.updateTotalPedido(pedidoId, igv, total, total)
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
                if (nPositive == 0.0)
                    p.estado = 0
                else
                    p.estado = 2

                calculoPedido(nPositive, p)
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
                load("Enviando...")
                productoViewModel.sendPedido(id)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun load(title: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textView: TextView = view.findViewById(R.id.textViewLado)
//        textView.text = String.format("%s", "Enviando")
        textView.text = title
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
                    dialogGeneratePedido(c, dialog)
                }
            })
        recyclerView.adapter = clienteAdapter
        productoViewModel.personalSearch(s).observe(this, Observer(clienteAdapter::submitList))
    }

    private fun generateCliente(id: Int, tipo: Int) {
        val gps = Gps(this@OrdenActivity)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                editTextTipo.text = null
                clienteId = id
                tipoPersonal = tipo
                productoViewModel.generarPedidoCliente(
                    gps.latitude.toString(), gps.longitude.toString(), id
                )
            }
        } else {
            gps.showSettingsAlert(this@OrdenActivity)
        }
    }


    private fun deletePedidoDialog(p: PedidoDetalle) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas eliminar el producto ?")
            .setPositiveButton("SI") { dialog, _ ->
                productoViewModel.deletePedidoDetalleOnline(p)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun calculoPedido(cantidad: Double, p: PedidoDetalle) {
        val factor = p.factor
        val abreviaturaUnidad = p.abreviaturaProducto

        val caja = if (abreviaturaUnidad.trim() == "UNIDAD" && tipoPersonal == 1) {
            cantidad / factor
        } else {
            cantidad
        }
        val precio = when (tipoPersonal) {
            1 -> if (caja >= p.rangoCajaHorizontal) p.precio2 else p.precio1
            else -> if (caja > p.rangoCajaMayorista) p.precioMayMayor else p.precioMayMenor
        }

        p.cantidad = cantidad
        p.unidadMedida = cantidad
        p.precioVenta = precio
        p.subTotal = cantidad * precio
        p.totalPedido = p.subTotal
        productoViewModel.updateProducto(p)
    }

    private fun dialogGeneratePedido(c: Cliente, d: AlertDialog) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas generar pedido con este cliente ?")
            .setPositiveButton("SI") { dialog, _ ->
                load("Generando Pedido..")
                generateCliente(c.identity, c.tipoPersonal)
                dialog.dismiss()
                d.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }
}