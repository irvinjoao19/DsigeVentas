package com.dsige.dsigeventas.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
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
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.adapters.*
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_reparto.*
import javax.inject.Inject

class RepartoActivity : DaggerAppCompatActivity(), View.OnClickListener,
    TextView.OnEditorActionListener {

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
            R.id.editTextLocal -> dialogSpinner("Local", 1)
            R.id.editTextDistrito -> dialogSpinner("Distrito", 2)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var clienteViewModel: ClienteViewModel

    lateinit var f: Filtro
    var topMenu: Menu? = null
    var activeOrClose: Int = 0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        topMenu = menu
        menu.findItem(R.id.add).setVisible(false).isEnabled = false
        menu.findItem(R.id.ok).setVisible(false).isEnabled = false
        menu.findItem(R.id.search).setVisible(false).isEnabled = false
        menu.findItem(R.id.logout).setVisible(false).isEnabled = false
        menu.findItem(R.id.map).setVisible(false).isEnabled = false
        return true
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
                        menu.icon = ContextCompat.getDrawable(this, R.drawable.ic_close)
                        menu2.setVisible(true).isEnabled = true
                        activeOrClose = 1
                    }
                    1 -> {
                        cardviewFiltro.visibility = View.GONE
                        menu.icon = ContextCompat.getDrawable(this, R.drawable.ic_search_white)
                        menu2.setVisible(false).isEnabled = false
                        clienteViewModel.search.value = ""
                        activeOrClose = 0
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reparto)
        bindUI()
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)
        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Repartos"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        editTextLocal.setOnClickListener(this)
        editTextDistrito.setOnClickListener(this)
        editTextSearch.setOnEditorActionListener(this)

        val repartoMapAdapter =
            RepartoMapAdapter(object : OnItemClickListener.RepartoListener {
                override fun onItemClick(r: Reparto, v: View, position: Int) {
                    when (v.id) {
                        R.id.imageViewMap -> if (r.latitud.isNotEmpty() && r.longitud.isNotEmpty()) {
                            startActivity(
                                Intent(this@RepartoActivity, MapsActivity::class.java)
                                    .putExtra("latitud", r.latitud)
                                    .putExtra("longitud", r.longitud)
                                    .putExtra("title", r.apellidoNombreCliente)
                                    .putExtra("localId", r.localId)
                            )
                        } else
                            repartoViewModel.setError("No cuenta con ubicación")
                    }
                }
            })

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = repartoMapAdapter
        repartoViewModel.getListReparto().observe(this, Observer(repartoMapAdapter::submitList))
        repartoViewModel.search.value = null

        repartoViewModel.mensajeError.observe(this, Observer {
            Util.toastMensaje(this, it)
        })

        repartoViewModel.mensajeSuccess.observe(this, Observer {
            Util.toastMensaje(this, it)
        })
    }

    private fun dialogSpinner(title: String, tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this).inflate(R.layout.dialog_combo, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val editTextSearch: TextInputEditText = view.findViewById(R.id.editTextSearch)
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

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
}
