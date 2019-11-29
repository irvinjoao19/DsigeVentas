package com.dsige.dsigeventas.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
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
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.local.model.Pedido
import com.dsige.dsigeventas.data.local.model.PedidoDetalle
import com.dsige.dsigeventas.data.viewModel.ProductoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.activities.ProductoActivity
import com.dsige.dsigeventas.ui.activities.RegisterClientActivity
import com.dsige.dsigeventas.ui.adapters.ClienteAdapter
import com.dsige.dsigeventas.ui.adapters.ProductoPedidoAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_orders.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OrdersFragment : DaggerFragment(), View.OnClickListener, TextView.OnEditorActionListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imageViewAddPerson -> startActivity(
                Intent(context, RegisterClientActivity::class.java)
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

    private var param1: String? = null
    private var param2: String? = null

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null
    lateinit var topMenu: Menu

    var clienteId: Int = 0

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        topMenu = menu
        menu.findItem(R.id.filter).setVisible(false).isEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add ->
                if (clienteId != 0) {
                    startActivity(
                        Intent(context, ProductoActivity::class.java)
                            .putExtra("pedidoId", clienteId)
                    )
                } else {
                    productoViewModel.setError("Eliga un cliente")
                }
            R.id.ok -> productoViewModel.validatePedido(clienteId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageViewAddPerson.setOnClickListener(this)
        editTextTipo.setOnEditorActionListener(this)
        bindUI()
    }

    private fun bindUI() {
        productoViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProductoViewModel::class.java)

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
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = productoPedidoAdapter

        productoViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(context!!, s)
            }
        })

        productoViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                when (s) {
                    "Ok" -> sendPedido(clienteId)
                    "ENVIADO" -> {
                        loadFinish()
                        Util.toastMensaje(context!!, s)
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
        textViewSubTotal.text = String.format("Sub Total : S/. %s", subTotal)
        textViewTotal.text = String.format("Total : S/. %s", total)
    }

    private fun updateCantidadProducto(p: PedidoDetalle) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.dialog_count_producto, null)
        val editTextProducto: EditText = v.findViewById(R.id.editTextProducto)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        //editTextProducto.setText(p.cantidad.toInt().toString())
        Util.showKeyboard(editTextProducto, context!!)
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
                Util.hideKeyboardFrom(context!!, v)
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
        val dialog = MaterialAlertDialogBuilder(context)
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
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
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
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        val layoutManager = LinearLayoutManager(context)
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
                    editTextTipo.text = null
                    clienteId = c.clienteId
                    productoViewModel.generarPedidoCliente(clienteId)
                    productoViewModel.getPedidoCliente(clienteId)
                        .observe(this@OrdersFragment, Observer<Pedido> { p ->
                            if (p != null) {
                                textViewNombre.text = p.nombreCliente
                                textViewIgv.text =
                                    String.format("I.G.V. %s%s", p.porcentajeIGV, "%")
                                textViewSubTotal.text =
                                    String.format("Sub Total : S/. %s", p.subtotal)
                                textViewTotal.text = String.format("Total : S/. %s", p.totalNeto)
                            }
                        })
                    productoViewModel.getProductoByPedido(clienteId)
                        .observe(this@OrdersFragment, Observer<PagedList<PedidoDetalle>> { p ->
                            if (p != null) {
                                if (p.size == 0) {
                                    topMenu.findItem(R.id.ok).setVisible(false).isEnabled = false
                                }
                                updateProducto(p)
                                productoPedidoAdapter.submitList(p)
                            }
                        })
                    dialog.dismiss()
                }
            })
        recyclerView.adapter = clienteAdapter
        productoViewModel.personalSearch(s).observe(this, Observer(clienteAdapter::submitList))
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}