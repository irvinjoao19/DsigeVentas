package com.dsige.dsigeventas.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Pedido
import com.dsige.dsigeventas.data.viewModel.ProductoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.ui.activities.ClientMapActivity
import com.dsige.dsigeventas.ui.activities.OrdenActivity
import com.dsige.dsigeventas.ui.adapters.PedidoPagingAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_pedido.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PedidoFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var productoViewModel: ProductoViewModel

    private var param1: String? = null
    private var param2: String? = null


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.ok).setVisible(false).isEnabled = false
        menu.findItem(R.id.filter).setVisible(false).isEnabled = false
        menu.findItem(R.id.logout).setVisible(false).isEnabled = false
        menu.findItem(R.id.map).setVisible(false).isEnabled = false
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        search(searchView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> startActivity(
                Intent(context, OrdenActivity::class.java)
                    .putExtra("pedidoId", 0)
                    .putExtra("clienteId", 0)
            )
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
        return inflater.inflate(R.layout.fragment_pedido, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        bindUI()
    }

    private fun bindUI() {
        productoViewModel =
            ViewModelProvider(this, viewModelFactory).get(ProductoViewModel::class.java)

        val pedidoAdapter = PedidoPagingAdapter(object : OnItemClickListener.PedidoListener {
            override fun onItemClick(p: Pedido, v: View, position: Int) {
                when (v.id) {
                    R.id.imageViewMap -> if (p.latitud.isNotEmpty() && p.longitud.isNotEmpty()) {
                        startActivity(
                            Intent(context, ClientMapActivity::class.java)
                                .putExtra("latitud", p.latitud)
                                .putExtra("longitud", p.longitud)
                                .putExtra("title", p.nombreCliente)
                        )
                    } else
                        productoViewModel.setError("No cuenta con ubicación")
                    else -> if (p.estado == 0) {
                        val popupMenu = PopupMenu(context!!, v)
                        popupMenu.menu.add(0, 1, 0, getText(R.string.goPedido))
                        popupMenu.menu.add(0, 2, 0, getText(R.string.delete))
                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                1 -> goOrdenActivity(p)
                                2 -> deletePedidoDialog(p)
                            }
                            false
                        }
                        popupMenu.show()
                    } else goOrdenActivity(p)
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
        recyclerView.adapter = pedidoAdapter
        productoViewModel.getPedido().observe(viewLifecycleOwner, Observer(pedidoAdapter::submitList))
        productoViewModel.searchPedido.value = ""
    }

    private fun goOrdenActivity(p: Pedido) {
        startActivity(
            Intent(context, OrdenActivity::class.java)
                .putExtra("pedidoId", p.pedidoId)
                .putExtra("clienteId", p.clienteId)
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PedidoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun deletePedidoDialog(p: Pedido) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Mensaje")
            .setMessage("Deseas eliminar el producto ?")
            .setPositiveButton("SI") { dialog, _ ->
                productoViewModel.deletePedido(p)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                productoViewModel.searchPedido.value = newText
                return true
            }
        })
    }
}