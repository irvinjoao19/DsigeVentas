package com.dsige.dsigeventas.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Stock
import com.dsige.dsigeventas.data.viewModel.ProductoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.ui.activities.FileProductoActivity
import com.dsige.dsigeventas.ui.adapters.ProductoPagingAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_products.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProductsFragment : DaggerFragment() {

    private var param1: String? = null
    private var param2: String? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var productoViewModel: ProductoViewModel

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.ok).setVisible(false).isEnabled = false
        menu.findItem(R.id.filter).setVisible(false).isEnabled = false
        menu.findItem(R.id.add).setVisible(false).isEnabled = false
        menu.findItem(R.id.logout).setVisible(false).isEnabled = false
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        search(searchView)
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
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        bindUI()
    }

    private fun bindUI() {
        productoViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ProductoViewModel::class.java)

        val productoPagingAdapter =
            ProductoPagingAdapter(object : OnItemClickListener.ProductoListener {
                override fun onItemClick(s: Stock, v: View, position: Int) {
                    startActivity(
                        Intent(context, FileProductoActivity::class.java)
                            .putExtra("productoId", s.productoId)
                    )
                }
            })
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = productoPagingAdapter
        productoViewModel.getProductos()
            .observe(this, Observer(productoPagingAdapter::submitList))
        productoViewModel.searchProducto.value = ""
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                productoViewModel.searchProducto.value = newText
                return true
            }
        })
    }
}