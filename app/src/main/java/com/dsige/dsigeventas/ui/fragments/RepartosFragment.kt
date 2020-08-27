package com.dsige.dsigeventas.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.RepartoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Gps
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.activities.MapsActivity
import com.dsige.dsigeventas.ui.activities.RepartoGeneralMapActivity
import com.dsige.dsigeventas.ui.adapters.*
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_repartos.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RepartosFragment : DaggerFragment(), View.OnClickListener, TextView.OnEditorActionListener {

    override fun onEditorAction(v: TextView, p1: Int, p2: KeyEvent?): Boolean {
        if (v.text.isNotEmpty()) {
            f.search = v.text.toString()
            val json = Gson().toJson(f)
            clienteViewModel.search.value = json
        }
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextLocal -> dialogCombo("Local", 1)
            R.id.editTextDistrito -> dialogCombo("Distrito", 2)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var clienteViewModel: ClienteViewModel

    private var param1: String? = null
    private var param2: String? = null

    var activeOrClose: Int = 0
    var topMenu: Menu? = null
    lateinit var f: Filtro
    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        topMenu = menu
        menu.findItem(R.id.add).setVisible(false).isEnabled = false
        menu.findItem(R.id.ok).setVisible(false).isEnabled = false
        menu.findItem(R.id.search).setVisible(false).isEnabled = false
        menu.findItem(R.id.logout).setVisible(false).isEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ok -> {
                f.search = editTextSearch.text.toString()
                val json = Gson().toJson(f)
                repartoViewModel.search.value = json
            }
            R.id.filter -> {
                f = Filtro()
                editTextLocal.text = null
                editTextDistrito.text = null
                editTextSearch.text = null
                val menu = topMenu!!.findItem(R.id.filter)
                val menu2 = topMenu!!.findItem(R.id.ok)
                when (activeOrClose) {
                    0 -> {
                        cardviewFiltro.visibility = View.VISIBLE
                        menu.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_close)
                        menu2.setVisible(true).isEnabled = true
                        activeOrClose = 1
                    }
                    1 -> {
                        cardviewFiltro.visibility = View.GONE
                        menu.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_search_white)
                        menu2.setVisible(false).isEnabled = false
                        repartoViewModel.search.value = null
                        activeOrClose = 0
                    }
                }
            }
            R.id.map -> calculando()
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
        return inflater.inflate(R.layout.fragment_repartos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        bindUI()
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)
        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)

        editTextLocal.setOnClickListener(this)
        editTextDistrito.setOnClickListener(this)
        editTextSearch.setOnEditorActionListener(this)

        val repartoMapAdapter =
            RepartoMapAdapter(object : OnItemClickListener.RepartoListener {
                override fun onItemClick(r: Reparto, v: View, position: Int) {
                    when (v.id) {
                        R.id.imageViewFactura -> dialogFactura(r)
                        R.id.imageViewMap -> if (r.latitud.isNotEmpty() && r.longitud.isNotEmpty()) {
                            dialogResumen(r)
                        } else
                            repartoViewModel.setError("No cuenta con ubicación")
                    }
                }
            })

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = repartoMapAdapter
        repartoViewModel.getListReparto()
            .observe(viewLifecycleOwner, Observer(repartoMapAdapter::submitList))
        repartoViewModel.search.value = null

        repartoViewModel.mensajeError.observe(viewLifecycleOwner, Observer {
            closeLoad()
            Util.toastMensaje(context!!, it)
        })

        repartoViewModel.mensajeSuccess.observe(viewLifecycleOwner, Observer {
            closeLoad()
            Util.toastMensaje(context!!, it)
        })

        repartoViewModel.goMap.observe(viewLifecycleOwner, Observer {
            closeLoad()
            startActivity(Intent(context, RepartoGeneralMapActivity::class.java))
        })

    }

    private fun dialogCombo(title: String, tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val editTextSearch: TextInputEditText = view.findViewById(R.id.editTextSearch)
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = layoutManager
        textViewTitle.text = title
        when (tipo) {
            1 -> {
                editTextSearch.visibility = View.GONE
                val localAdapter = LocalAdapter(object : OnItemClickListener.LocalListener {
                    override fun onItemClick(l: Local, v: View, position: Int) {
                        f.localId = l.localId
                        editTextLocal.setText(l.nombre)
                        dialog.dismiss()
                    }
                })
                recyclerView.adapter = localAdapter
                repartoViewModel.getLocales().observe(this, Observer { e ->
                    if (e != null) {
                        localAdapter.addItems(e)
                    }
                })
            }
            2 -> {
                val distritoAdapter =
                    DistritoAdapter(object : OnItemClickListener.DistritoListener {
                        override fun onItemClick(d: Distrito, v: View, position: Int) {
                            f.distritoRId = d.distritoId
                            editTextDistrito.setText(d.nombre)
                            dialog.dismiss()
                        }
                    })
                recyclerView.adapter = distritoAdapter
                clienteViewModel.getDistritosById("15", "1")
                    .observe(this, Observer { d ->
                        if (d != null) {
                            distritoAdapter.addItems(d)
                        }
                    })
                editTextSearch.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        charSequence: CharSequence, i: Int, i1: Int, i2: Int
                    ) {

                    }

                    override fun onTextChanged(
                        charSequence: CharSequence, i: Int, i1: Int, i2: Int
                    ) {

                    }

                    override fun afterTextChanged(editable: Editable) {
                        distritoAdapter.getFilter().filter(editable)
                    }
                })
            }
        }
    }

    private fun calculando() {
        val gps = Gps(context!!)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                load("Calculando Ruta")
                repartoViewModel.calculando(gps.latitude.toString(), gps.longitude.toString())
            }
        } else {
            gps.showSettingsAlert(context!!)
        }
    }

    private fun load(title : String) {
        builder = AlertDialog.Builder(
            ContextThemeWrapper(context, R.style.AppTheme)
        )
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textView: TextView = view.findViewById(R.id.textViewLado)
        textView.text = title
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun closeLoad() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RepartosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun dialogResumen(r: Reparto) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.cardview_resumen_maps, null)
        val buttonGo: MaterialButton = v.findViewById(R.id.buttonGo)
        val textViewTitle: TextView = v.findViewById(R.id.textViewTitle)
        val textViewLatitud: TextView = v.findViewById(R.id.textViewLatitud)
        val textViewLongitud: TextView = v.findViewById(R.id.textViewLongitud)
        val imageViewClose: ImageView = v.findViewById(R.id.imageViewClose)

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        textViewTitle.text = r.apellidoNombreCliente
        textViewLatitud.setText(
            Util.getTextHTML("<strong>Latitud: </strong> " + r.latitud),
            TextView.BufferType.SPANNABLE
        )
        textViewLongitud.setText(
            Util.getTextHTML("<strong>Longitud : </strong> " + r.longitud),
            TextView.BufferType.SPANNABLE
        )
        buttonGo.setOnClickListener {
            startActivity(
                Intent(context, MapsActivity::class.java)
                    .putExtra("latitud", r.latitud)
                    .putExtra("longitud", r.longitud)
                    .putExtra("title", r.apellidoNombreCliente)
                    .putExtra("localId", r.localId)
            )
            dialog.dismiss()
        }
        imageViewClose.setOnClickListener { dialog.dismiss() }
    }

    private fun dialogFactura(s: Reparto) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.dialog_reparto, null)

        val linearLayoutLoad: ConstraintLayout = v.findViewById(R.id.linearLayoutLoad)
        val linearLayoutPrincipal: LinearLayout = v.findViewById(R.id.linearLayoutPrincipal)
        val textViewRuc: TextView = v.findViewById(R.id.textViewRuc)
        val textViewDoc: TextView = v.findViewById(R.id.textViewDoc)
        val textViewNameClient: TextView = v.findViewById(R.id.textViewNameClient)
        val textViewSubTotal: TextView = v.findViewById(R.id.textViewSubTotal)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val imageViewClose: ImageView = v.findViewById(R.id.imageViewClose)
        val editTextEstado: TextInputEditText = v.findViewById(R.id.editTextEstado)
        val editTextMotivo: TextInputEditText = v.findViewById(R.id.editTextMotivo)
        val textInputMotivo: TextInputLayout = v.findViewById(R.id.textInputMotivo)
        val buttonGuardar: MaterialButton = v.findViewById(R.id.buttonGuardar)

        var re = Reparto()

        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        imageViewClose.setOnClickListener { dialog.dismiss() }
        editTextEstado.setOnClickListener { dialogSpinner(textInputMotivo, "Estado", 1, re) }
        editTextMotivo.setOnClickListener { dialogSpinner(textInputMotivo, "Grupo", 2, re) }

        buttonGuardar.setOnClickListener {
            sendDialog(dialog, re)
        }
        val repartoDetalleAdapter =
            RepartoDetalleAdapter(object : OnItemClickListener.RepartoDetalleListener {
                override fun onItemClick(r: RepartoDetalle, v: View, position: Int) {
                    when (v.id) {
                        R.id.editTextCantidad -> updateCantidadReparto(r)
                        R.id.imageViewNegative -> {
                            val resta = r.cantidad
                            if (resta != 0.0) {
                                val rTotal = (resta - 1).toString()
                                val nNegative = rTotal.toDouble()

                                r.cantidad = nNegative
                                r.total = nNegative * r.precioVenta
                                repartoViewModel.updateRepartoDetalle(r)
                            }
                        }
                        else -> {
                            val popupMenu = PopupMenu(context!!, v)
                            popupMenu.menu.add(0, 1, 0, getText(R.string.delete))
                            popupMenu.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    1 -> deleteRepartoDialog(r)
                                }
                                false
                            }
                            popupMenu.show()
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
        recyclerView.adapter = repartoDetalleAdapter

        Handler().postDelayed({

            repartoViewModel.getRepartoById(s.repartoId)
                .observe(this, Observer { r ->
                    if (r != null) {
                        textViewRuc.text = r.numeroDocumento
                        textViewDoc.setText(
                            Util.getTextHTML("<strong>Nro Doc Vta: </string>" + r.docVTA),
                            TextView.BufferType.SPANNABLE
                        )
                        textViewNameClient.text = r.apellidoNombreCliente
                        textViewSubTotal.setText(
                            Util.getTextHTML("<font color='red'>Total : </font> S/" + r.subTotal),
                            TextView.BufferType.SPANNABLE
                        )

                        editTextEstado.setText(r.nombreEstado)
                        editTextMotivo.setText(r.motivo)
                        re = r

                        linearLayoutLoad.visibility = View.GONE
                        linearLayoutPrincipal.visibility = View.VISIBLE
                    }
                })

            repartoViewModel.getDetalleRepartoById(s.repartoId)
                .observe(this, Observer { p ->
                    if (p.size != 0) {
                        updateReparto(s.repartoId, p)
                        repartoDetalleAdapter.submitList(p)
                    }
                })
        }, 800)
    }

    private fun updateReparto(repartoId: Int, repartos: List<RepartoDetalle>) {
        var total = 0.0
        for (p in repartos) {
            total += p.total
        }
        repartoViewModel.updateTotalReparto(repartoId, total)
    }

    private fun sendDialog(d: AlertDialog, r: Reparto) {
        val dialog = MaterialAlertDialogBuilder(context!!)
            .setTitle("Mensaje")
            .setMessage("Deseas enviar el reparto?")
            .setPositiveButton("SI") { dialog, _ ->
                load("Enviando...")
                repartoViewModel.updateReparto(1, r)
                dialog.dismiss()
                d.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun updateCantidadReparto(p: RepartoDetalle) {
        val builder = AlertDialog.Builder(
            ContextThemeWrapper(context, R.style.AppTheme)
        )
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.dialog_count_producto, null)
        val editTextProducto: EditText = v.findViewById(R.id.editTextProducto)
        val buttonCancelar: MaterialButton = v.findViewById(R.id.buttonCancelar)
        val buttonAceptar: MaterialButton = v.findViewById(R.id.buttonAceptar)
        //editTextProducto.setText(p.cantidad.toString())
        Util.showKeyboard(editTextProducto, context!!)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        buttonAceptar.setOnClickListener {
            if (editTextProducto.text.toString().isNotEmpty()) {
                val nPositive = editTextProducto.text.toString().toDouble()
                if (nPositive > p.cantidadExacta) {
                    repartoViewModel.setError("Cantidad no debe ser mayor al actual " + p.cantidadExacta)
                } else {
                    p.cantidad = nPositive
                    p.total = nPositive * p.precioVenta
                    repartoViewModel.updateRepartoDetalle(p)
                }
                Util.hideKeyboardFrom(context!!, v)
                dialog.dismiss()

            } else {
                repartoViewModel.setError("Digite cantidad")
            }
        }
        buttonCancelar.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun dialogSpinner(
        l: TextInputLayout, title: String, tipo: Int, re: Reparto
    ) {
        val builder = AlertDialog.Builder(
            ContextThemeWrapper(context, R.style.AppTheme)
        )
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.show()

        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = layoutManager

        textViewTitle.text = title

        when (tipo) {
            1 -> {
                val estadoAdapter = EstadoAdapter(object : OnItemClickListener.EstadoListener {
                    override fun onItemClick(e: Estado, v: View, position: Int) {
                        re.estado = e.estadoId
                        re.nombreEstado = e.nombre
                        repartoViewModel.updateReparto(0, re)
                        l.visibility = View.GONE
                        if (e.estadoId == 30) {
                            l.visibility = View.VISIBLE
                        }
                        dialogSpinner.dismiss()
                    }
                })
                recyclerView.adapter = estadoAdapter
                repartoViewModel.getEstados().observe(this, Observer { e ->
                    if (e != null) {
                        estadoAdapter.addItems(e)
                    }
                })
            }
            2 -> {
                val grupoAdapter = GrupoAdapter(object : OnItemClickListener.GrupoListener {
                    override fun onItemClick(g: Grupo, v: View, position: Int) {
                        re.motivoId = g.detalleTablaId
                        re.motivo = g.descripcion
                        repartoViewModel.updateReparto(0, re)
                        dialogSpinner.dismiss()
                    }
                })
                recyclerView.adapter = grupoAdapter
                repartoViewModel.getGrupos().observe(this, Observer { e ->
                    if (e != null) {
                        grupoAdapter.addItems(e)
                    }
                })
            }
        }
    }

    private fun deleteRepartoDialog(r: RepartoDetalle) {
        val dialog = MaterialAlertDialogBuilder(context!!)
            .setTitle("Mensaje")
            .setMessage("Deseas eliminar el producto ?")
            .setPositiveButton("SI") { dialog, _ ->
                r.estado = 0
                repartoViewModel.updateRepartoDetalle(r)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }
}