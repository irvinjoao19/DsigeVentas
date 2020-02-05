package com.dsige.dsigeventas.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.databinding.ActivityFileClientBinding
import com.dsige.dsigeventas.ui.listeners.NavigationItemSelectedListener
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class FileClientActivity : DaggerAppCompatActivity(), OnItemClickListener,
    NavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pedidos ->
                if (c.identity != 0) {
                    startActivity(
                        Intent(this, OrdenActivity::class.java).putExtra("clienteId", c.identity)
                    )
                } else {
                    startActivity(
                        Intent(this, OrdenActivity::class.java).putExtra("clienteId", c.clienteId)
                    )
                }
            R.id.foto -> {
                return true
            }
            R.id.reubica -> {
                return true
            }
        }
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabEdit -> startActivity(
                Intent(this, RegisterClientActivity::class.java)
                    .putExtra("clienteId", c.clienteId)
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var clienteViewModel: ClienteViewModel
    lateinit var c: Cliente
    lateinit var binding: ActivityFileClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_file_client)
        binding.lifecycleOwner = this
        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)

        val b = intent.extras
        if (b != null) {
            c = Cliente()
            bindUI(b.getInt("clienteId"))
        }
    }

    private fun bindUI(id: Int) {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Cliente"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.listener = this
        binding.navigate = this
        binding.c = clienteViewModel

        clienteViewModel.getClienteById(id).observe(this, Observer<Cliente> { cliente ->
            if (cliente != null) {
                c = cliente
                clienteViewModel.setCliente(c)
            }
        })
    }
}
