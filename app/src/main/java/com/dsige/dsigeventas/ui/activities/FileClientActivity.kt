package com.dsige.dsigeventas.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.databinding.ActivityFileClientBinding
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.listeners.NavigationItemSelectedListener
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_file_client.*
import java.io.File
import javax.inject.Inject

class FileClientActivity : DaggerAppCompatActivity(), OnItemClickListener,
    NavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pedidos -> mensajeCliente()
            R.id.foto -> startActivity(
                Intent(
                    this, CameraActivity::class.java
                ).putExtra("clienteId", c.clienteId)
            )
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
    private var localId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_file_client)
        binding.lifecycleOwner = this
        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)

        val b = intent.extras
        if (b != null) {
            c = Cliente()
            localId = b.getInt("localId")
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

        clienteViewModel.getClienteById(id).observe(this, Observer { cliente ->
            if (cliente != null) {
                c = cliente
                val f = File(Util.getFolder(this),c.nameImg)
                Picasso.get().load(f)
                    .error(ContextCompat.getDrawable(this,R.drawable.material_flat)!!)
                    .into(fondo)
                clienteViewModel.setCliente(c)
            }
        })
    }

    private fun mensajeCliente() {
        val material =
            MaterialAlertDialogBuilder(
                ContextThemeWrapper(this@FileClientActivity, R.style.AppTheme)
            )
                .setTitle("Mensaje")
                .setMessage("Deseas generar pedido?")
                .setPositiveButton("SI") { dialogInterface, _ ->
                    startActivity(
                        Intent(this, OrdenActivity::class.java)
                            .putExtra("clienteId", c.clienteId)
                            .putExtra("tipoPersonal", c.tipoPersonal)
                            .putExtra("localId", c.tipoPersonal)
                    )
                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface, _ ->

                    dialogInterface.dismiss()
                }
        material.show()
    }
}