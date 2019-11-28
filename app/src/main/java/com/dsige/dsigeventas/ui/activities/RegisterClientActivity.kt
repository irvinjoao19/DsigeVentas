package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.*
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.databinding.ActivityRegisterClientBinding
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.*
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
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
    lateinit var c: Cliente

    lateinit var binding: ActivityRegisterClientBinding

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.register, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.register) {
            formRegisterCliente()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_client)
        binding.lifecycleOwner = this
        clienteViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ClienteViewModel::class.java)

        val b = intent.extras
        if (b != null) {
            c = Cliente()
            bindUI(b.getInt("clienteId"))
            message()
        }
    }

    private fun bindUI(id: Int) {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Cliente"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.editTextVisita.setText(Util.getFecha())
        binding.listener = this
        binding.c = clienteViewModel

        clienteViewModel.getClienteById(id).observe(this, Observer<Cliente> { cliente ->
            if (cliente != null) {
                editTextProductoInteres.visibility = View.GONE
                c = cliente
                clienteViewModel.setCliente(c)
            }
        })
    }

    private fun message() {
        clienteViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
                finish()
            }
        })

        clienteViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
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
        val builder = AlertDialog.Builder(
            android.view.ContextThemeWrapper(
                this,
                R.style.AppTheme
            )
        )
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
        textViewTitle.text = title

        when (tipo) {
            1 -> {
                val departamentoAdapter =
                    DepartamentoAdapter(object : OnItemClickListener.DepartamentoListener {
                        override fun onItemClick(d: Departamento, v: View, position: Int) {
                            c.departamentoId = d.codigo.toInt()
                            editTextDepartamento.setText(d.departamento)
                            editTextProvincia.text = null
                            editTextDistrito.text = null
                            dialogSpinner.dismiss()
                        }
                    })
                recyclerView.adapter = departamentoAdapter

                clienteViewModel.getDepartamentos()
                    .observe(this, Observer<List<Departamento>> { d ->
                        if (d != null) {
                            departamentoAdapter.addItems(d)
                        }
                    })
            }
            2 -> {
                val provinciaAdapter =
                    ProvinciAdapter(object : OnItemClickListener.ProvinciaListener {
                        override fun onItemClick(p: Provincia, v: View, position: Int) {
                            c.provinciaId = p.codigo.toInt()
                            editTextProvincia.setText(p.provincia)
                            editTextDistrito.text = null
                            dialogSpinner.dismiss()
                        }
                    })
                recyclerView.adapter = provinciaAdapter

                clienteViewModel.getProvinciasById(c.departamentoId.toString())
                    .observe(this, Observer<List<Provincia>> { d ->
                        if (d != null) {
                            provinciaAdapter.addItems(d)
                        }
                    })
            }
            3 -> {
                val distritoAdapter =
                    DistritoAdapter(object : OnItemClickListener.DistritoListener {
                        override fun onItemClick(d: Distrito, v: View, position: Int) {
                            editTextDistrito.setText(d.nombre)
                            dialogSpinner.dismiss()
                        }
                    })
                recyclerView.adapter = distritoAdapter
                clienteViewModel.getDistritosById(
                    c.departamentoId.toString(),
                    c.provinciaId.toString()
                )
                    .observe(this, Observer<List<Distrito>> { d ->
                        if (d != null) {
                            distritoAdapter.addItems(d)
                        }
                    })
            }
            4 -> {
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

    private fun formRegisterCliente() {
        c.tipo = editTextTipo.text.toString()
        c.documento = editTextDocumento.text.toString()
        c.nombreCliente = editTextNombre.text.toString()
        c.nombreGiroNegocio = editTextPago.text.toString()
        c.nombreDepartamento = editTextDepartamento.text.toString()
        c.nombreDistrito = editTextDistrito.text.toString()
        c.direccion = editTextDireccion.text.toString()
        c.nroCelular = editTextTelefono.text.toString()
        c.email = editTextEmail.text.toString()
        c.fechaVisita = editTextVisita.text.toString()
        c.motivoNoCompra = editTextMotivoNoComprar.text.toString()
        c.productoInteres = editTextProductoInteres.text.toString()
        c.nombreGiroNegocio = editTextPago.text.toString()
        clienteViewModel.validateCliente(c)
    }
}