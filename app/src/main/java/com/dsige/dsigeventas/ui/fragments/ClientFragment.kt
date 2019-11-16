package com.dsige.dsigeventas.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Cliente
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.ui.activities.FileClientActivity
import com.dsige.dsigeventas.ui.activities.RegisterClientActivity
import com.dsige.dsigeventas.ui.adapters.ClientePagingAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_client.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ClientFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var clienteViewModel: ClienteViewModel

    private var param1: String? = null
    private var param2: String? = null
    var activeOrClose: Int = 0
    var topMenu: Menu? = null

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        topMenu = menu
//        menu.findItem(R.id.add).setVisible(false).isEnabled = false
        menu.findItem(R.id.pedidos).setVisible(false).isEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> startActivity(
                Intent(context, RegisterClientActivity::class.java).putExtra("clienteId", 0)
            )
            R.id.filter -> {
                val menu = topMenu!!.findItem(R.id.filter)
                when (activeOrClose) {
                    0 -> {
                        cardviewFiltro.visibility = View.VISIBLE
                        menu.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_close)
                        activeOrClose = 1
                    }
                    1 -> {
                        cardviewFiltro.visibility = View.GONE
                        menu.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_search_white)
                        activeOrClose = 0
                    }
                }
            }
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
        return inflater.inflate(R.layout.fragment_client, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        clienteViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ClienteViewModel::class.java)

        val clientePagingAdapter =
            ClientePagingAdapter(object : OnItemClickListener.ClienteListener {
                override fun onItemClick(c: Cliente, v: View, position: Int) {
                    startActivity(
                        Intent(context, FileClientActivity::class.java)
                            .putExtra("clienteId", c.clienteId)
                    )
                }
            })
        val layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = clientePagingAdapter
        clienteViewModel.getCliente()
            .observe(this, Observer(clientePagingAdapter::submitList))
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClientFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}