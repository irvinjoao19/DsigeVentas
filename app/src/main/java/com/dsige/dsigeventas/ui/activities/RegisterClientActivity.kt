package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.databinding.ActivityRegisterClientBinding
import com.dsige.dsigeventas.helper.Gps
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.*
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_register_client.*
import kotlinx.android.synthetic.main.activity_register_client.editTextDepartamento
import kotlinx.android.synthetic.main.activity_register_client.editTextDistrito
import kotlinx.android.synthetic.main.activity_register_client.editTextProvincia
import java.util.ArrayList
import javax.inject.Inject

class RegisterClientActivity : DaggerAppCompatActivity(), OnItemClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextTipo -> dialogSpinner()
            R.id.editTextVisita -> Util.getDateDialog(this, v, binding.editTextVisita)
            R.id.editTextDepartamento -> dialogSpinner("Departamento", 1)
            R.id.editTextProvincia -> dialogSpinner("Provincia", 2)
            R.id.editTextDistrito -> dialogSpinner("Distrito", 3)
            R.id.editTextPago -> dialogSpinner("Forma de Pago", 4)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var clienteViewModel: ClienteViewModel
    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null
    lateinit var c: Cliente
    lateinit var f: Filtro
    lateinit var binding: ActivityRegisterClientBinding

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.register, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.register) {
            mensajeCliente()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_client)
        binding.lifecycleOwner = this
        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)

        val b = intent.extras
        if (b != null) {
            c = Cliente()
            f = Filtro()
            bindUI(b.getInt("clienteId"), b.getInt("usuarioId"))
            message()
        }
    }

    private fun bindUI(id: Int, usuarioId: Int) {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Cliente"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.listener = this
        binding.c = clienteViewModel

        c.personalVendedorId = usuarioId
        if (id == 0) {
            Handler().postDelayed({
                editTextVisita.setText(Util.getFecha())
                editTextTipo.setText(String.format("Natural"))
                editTextDocumento.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                c.giroNegocioId = 2
                editTextPago.setText(String.format("CONTADO C/ ENTREGA"))
                val gps = Gps(this)
                if (gps.isLocationEnabled()) {
                    Util.getLocationName(this, gps.location!!, editTextDireccion)
                }
            }, 200)
        } else {
            clienteViewModel.getClienteById(id).observe(this, Observer { cliente ->
                if (cliente != null) {
                    c = cliente
                    clienteViewModel.setCliente(c)
                }
            })
        }
    }

    private fun message() {
        clienteViewModel.mensajeSuccess.observe(this, Observer { s ->
            if (s != null) {
                loadFinish()
                Util.toastMensaje(this, s)
                finish()
            }
        })

        clienteViewModel.mensajeError.observe(this, Observer { s ->
            if (s != null) {
                loadFinish()
                Util.toastMensaje(this, s)
            }
        })
    }

    private fun dialogSpinner() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_spinner, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.setCanceledOnTouchOutside(false)
        dialogSpinner.show()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = layoutManager

        textViewTitle.text = String.format("%s", "Tipo")

        val menuAdapter = MenuAdapter(object : OnItemClickListener.MenuListener {
            override fun onItemClick(m: MenuPrincipal, view: View, position: Int) {
                binding.editTextTipo.setText(m.title)
                binding.editTextDocumento.text = null
                if (m.title == "Natural") {
                    binding.editTextDocumento.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                } else {
                    binding.editTextDocumento.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(11))
                }
                Util.hideKeyboard(this@RegisterClientActivity)
                dialogSpinner.dismiss()
            }
        })
        recyclerView.adapter = menuAdapter

        val menu = ArrayList<MenuPrincipal>()
        menu.add(MenuPrincipal(1, "Natural"))
        menu.add(MenuPrincipal(2, "Juridico"))
        menuAdapter.addItems(menu)
    }

    private fun dialogSpinner(title: String, tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_combo, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val editTextSearch: TextInputEditText = view.findViewById(R.id.editTextSearch)
        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.setCanceledOnTouchOutside(false)
        dialogSpinner.show()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.layoutManager = layoutManager
        textViewTitle.text = title

        f.departamentoId = "15"
        f.provinciaId = "1"

        when (tipo) {
            1 -> {
                val departamentoAdapter =
                    DepartamentoAdapter(object : OnItemClickListener.DepartamentoListener {
                        override fun onItemClick(d: Departamento, v: View, position: Int) {
                            c.departamentoId = d.departamentoId
                            f.departamentoId = d.codigo
                            editTextDepartamento.setText(d.departamento)
                            editTextProvincia.text = null
                            editTextDistrito.text = null
                            dialogSpinner.dismiss()
                        }
                    })
                recyclerView.adapter = departamentoAdapter

                clienteViewModel.getDepartamentos()
                    .observe(this, Observer { d ->
                        if (d != null) {
                            departamentoAdapter.addItems(d)
                        }
                    })
                editTextSearch.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun onTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun afterTextChanged(editable: Editable) {
                        departamentoAdapter.getFilter().filter(editable)
                    }
                })
            }
            2 -> {
                val provinciaAdapter =
                    ProvinciAdapter(object : OnItemClickListener.ProvinciaListener {
                        override fun onItemClick(p: Provincia, v: View, position: Int) {
                            c.provinciaId = p.provinciaId
                            f.provinciaId = p.codigo
                            editTextProvincia.setText(p.provincia)
                            editTextDistrito.text = null
                            dialogSpinner.dismiss()
                        }
                    })
                recyclerView.adapter = provinciaAdapter

                clienteViewModel.getProvinciasById(f.departamentoId)
                    .observe(this, Observer { d ->
                        if (d != null) {
                            provinciaAdapter.addItems(d)
                        }
                    })
                editTextSearch.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun onTextChanged(c: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun afterTextChanged(editable: Editable) {
                        provinciaAdapter.getFilter().filter(editable)
                    }
                })
            }
            3 -> {
                val distritoAdapter =
                    DistritoAdapter(object : OnItemClickListener.DistritoListener {
                        override fun onItemClick(d: Distrito, v: View, position: Int) {
                            c.distritoId = d.distritoId
                            editTextDistrito.setText(d.nombre)
                            dialogSpinner.dismiss()
                        }
                    })
                recyclerView.adapter = distritoAdapter
                clienteViewModel.getDistritosById(f.departamentoId, f.provinciaId)
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
            4 -> {
                editTextSearch.visibility = View.GONE
                val formaPagoAdapter =
                    FormaPagoAdapter(object : OnItemClickListener.FormaPagoListener {
                        override fun onItemClick(f: FormaPago, v: View, position: Int) {
                            c.giroNegocioId = f.formaPagoId
                            editTextPago.setText(f.descripcion)
                            dialogSpinner.dismiss()
                        }
                    })
                recyclerView.adapter = formaPagoAdapter
                clienteViewModel.getFormaPago().observe(this, Observer<List<FormaPago>> { f ->
                    if (f != null) {
                        formaPagoAdapter.addItems(f)
                    }
                })
            }
        }
    }

    private fun mensajeCliente() {
        val material =
            MaterialAlertDialogBuilder(
                ContextThemeWrapper(
                    this@RegisterClientActivity,
                    R.style.AppTheme
                )
            )
                .setTitle("Mensaje")
                .setMessage("Deseas enviar los datos ?")
                .setPositiveButton("SI") { dialogInterface, _ ->
                    formRegisterCliente(1)
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Otro momento") { dialogInterface, _ ->
                    formRegisterCliente(2)
                    dialogInterface.dismiss()
                }
        material.show()
    }

    /**
     * tipo : 1 -> enviar
     *        2 -> guardar
     *
     */
    private fun formRegisterCliente(tipo: Int) {
        val gps = Gps(this@RegisterClientActivity)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() != "0.0" || gps.longitude.toString() != "0.0") {
                c.tipo = editTextTipo.text.toString()
                c.documento = editTextDocumento.text.toString()
                c.nombreCliente = editTextNombre.text.toString()
                c.nombreGiroNegocio = editTextPago.text.toString()
                c.nombreDepartamento = "Lima"
                c.nombreProvincia = "Lima"
                c.nombreDistrito = editTextDistrito.text.toString()
                c.departamentoId = 1390
                c.provinciaId = 1390
                c.direccion = editTextDireccion.text.toString()
                c.nroCelular = editTextTelefono.text.toString()
                c.email = editTextEmail.text.toString()
                c.fechaVisita = editTextVisita.text.toString()
                c.motivoNoCompra = editTextMotivoNoComprar.text.toString()
                c.productoInteres = editTextProductoInteres.text.toString()
                c.nombreGiroNegocio = editTextPago.text.toString()
                c.latitud = gps.latitude.toString()
                c.longitud = gps.longitude.toString()
                if (tipo == 1) {
                    load()
                }
                clienteViewModel.validateCliente(c, tipo)
            }
        } else {
            gps.showSettingsAlert(this@RegisterClientActivity)
        }
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