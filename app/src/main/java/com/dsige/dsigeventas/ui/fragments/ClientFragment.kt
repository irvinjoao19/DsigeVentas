package com.dsige.dsigeventas.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
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
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.activities.ClientGeneralMapActivity
import com.dsige.dsigeventas.ui.activities.ClientMapActivity
import com.dsige.dsigeventas.ui.activities.FileClientActivity
import com.dsige.dsigeventas.ui.activities.RegisterClientActivity
import com.dsige.dsigeventas.ui.adapters.*
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_client.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"

class ClientFragment : DaggerFragment(), View.OnClickListener, TextView.OnEditorActionListener {

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (v.text.isNotEmpty()) {
            f.search = v.text.toString()
            val json = Gson().toJson(f)
            clienteViewModel.search.value = json
        }
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editTextDepartamento -> dialogSpinner("Departamento", 1)
            R.id.editTextProvincia -> dialogSpinner("Provincia", 2)
            R.id.editTextDistrito -> dialogSpinner("Distrito", 3)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var clienteViewModel: ClienteViewModel

    private var usuarioId: Int = 0
    var activeOrClose: Int = 0
    var topMenu: Menu? = null
    lateinit var f: Filtro

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        topMenu = menu
        menu.findItem(R.id.ok).setVisible(false).isEnabled = false
        menu.findItem(R.id.search).setVisible(false).isEnabled = false
        menu.findItem(R.id.logout).setVisible(false).isEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                when (activeOrClose) {
                    0 -> startActivity(
                        Intent(context, RegisterClientActivity::class.java)
                            .putExtra("clienteId", 0)
                            .putExtra("usuarioId", usuarioId)
                    )
                    1 -> {
                        f.search = editTextSearch.text.toString()
                        val json = Gson().toJson(f)
                        clienteViewModel.search.value = json
                    }
                }
            }
            R.id.filter -> {
                f = Filtro()
                editTextDepartamento.text = null
                editTextProvincia.text = null
                editTextDistrito.text = null
                editTextSearch.text = null
                val menu = topMenu!!.findItem(R.id.filter)
                val menu2 = topMenu!!.findItem(R.id.add)
                when (activeOrClose) {
                    0 -> {
                        cardviewFiltro.visibility = View.VISIBLE
                        menu.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_close)
                        menu2.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_done)
                        activeOrClose = 1
                    }
                    1 -> {
                        cardviewFiltro.visibility = View.GONE
                        menu.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_search_white)
                        menu2.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_add)
                        clienteViewModel.search.value = ""
                        activeOrClose = 0
                    }
                }
            }
            R.id.map -> startActivity(Intent(context, ClientGeneralMapActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        f = Filtro()
        arguments?.let {
            usuarioId = it.getInt(ARG_PARAM1)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_client, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        editTextDepartamento.setOnClickListener(this)
        editTextProvincia.setOnClickListener(this)
        editTextDistrito.setOnClickListener(this)
        editTextSearch.setOnEditorActionListener(this)
        bindUI()
    }

    private fun bindUI() {
        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)

        val clientePagingAdapter =
            ClientePagingAdapter(object : OnItemClickListener.ClienteListener {
                override fun onItemClick(c: Cliente, v: View, position: Int) {
                    when (v.id) {
                        R.id.imageViewMap -> if (c.latitud.isNotEmpty() && c.longitud.isNotEmpty()) {
                            startActivity(
                                Intent(context, ClientMapActivity::class.java)
                                    .putExtra("latitud", c.latitud)
                                    .putExtra("longitud", c.longitud)
                                    .putExtra("title", c.nombreCliente)
                            )
                        } else
                            clienteViewModel.setError("No cuenta con ubicación")
                        else -> startActivity(
                            Intent(context, FileClientActivity::class.java)
                                .putExtra("clienteId", c.clienteId)
                        )
                    }
                }
            })

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = clientePagingAdapter
        clienteViewModel.getCliente()
            .observe(viewLifecycleOwner, Observer(clientePagingAdapter::submitList))
        clienteViewModel.search.value = ""

        clienteViewModel.mensajeError.observe(viewLifecycleOwner, Observer {
            Util.toastMensaje(context!!, it)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            ClientFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

    private fun dialogSpinner(title: String, tipo: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val editTextSearch: TextInputEditText = view.findViewById(R.id.editTextSearch)
        builder.setView(view)
        val dialogSpinner = builder.create()
        dialogSpinner.setCanceledOnTouchOutside(false)
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

        f.departamentoId = "15"
        f.provinciaId = "1"

        when (tipo) {
            1 -> {
                val departamentoAdapter =
                    DepartamentoAdapter(object : OnItemClickListener.DepartamentoListener {
                        override fun onItemClick(d: Departamento, v: View, position: Int) {
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
                    override fun beforeTextChanged(
                        charSequence: CharSequence, i: Int, i1: Int, i2: Int
                    ) {

                    }

                    override fun onTextChanged(
                        charSequence: CharSequence, i: Int, i1: Int, i2: Int
                    ) {

                    }

                    override fun afterTextChanged(editable: Editable) {
                        departamentoAdapter.getFilter().filter(editable)
                    }
                })
            }
            2 -> {
                val provinciaAdapter =
                    ProvinciAdapter(object : OnItemClickListener.ProvinciaListener {
                        override fun onItemClick(p: Provincia, v: View, position: Int) {
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
                    override fun beforeTextChanged(
                        charSequence: CharSequence, i: Int, i1: Int, i2: Int
                    ) {

                    }

                    override fun onTextChanged(
                        charSequence: CharSequence, i: Int, i1: Int, i2: Int
                    ) {

                    }

                    override fun afterTextChanged(editable: Editable) {
                        provinciaAdapter.getFilter().filter(editable)
                    }
                })
            }
            3 -> {
                val distritoAdapter =
                    DistritoAdapter(object : OnItemClickListener.DistritoListener {
                        override fun onItemClick(d: Distrito, v: View, position: Int) {
                            f.distritoId = d.distritoId.toString()
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
        }
    }
}