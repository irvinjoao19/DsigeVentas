package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
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
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.local.model.MenuPrincipal
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.databinding.ActivityRegisterClientBinding
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.MenuAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import java.util.ArrayList
import javax.inject.Inject

class RegisterClientActivity : AppCompatActivity(), OnItemClickListener {

    override fun onClick(view: View) {
        when (view.id) {
            R.id.editTextTipo -> dialogSpinner()
            R.id.editTextVisita ->
                Util.getDateDialog(this, view, binding.editTextVisita)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var clienteViewModel: ClienteViewModel
    lateinit var cliente: Cliente

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
            cliente = Cliente()
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
        binding.cliente = clienteViewModel

        clienteViewModel.getClienteById(id).observe(this, Observer<Cliente> { c ->
            if (c != null) {

            }
        })
    }

    private fun message() {
        clienteViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
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

    private fun formRegisterCliente() {

    }
}
