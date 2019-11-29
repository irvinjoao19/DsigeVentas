package com.dsige.dsigeventas.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Reparto
import com.dsige.dsigeventas.data.viewModel.RepartoViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.ui.activities.MapsActivity
import com.dsige.dsigeventas.ui.adapters.RepartoAdapter
import com.dsige.dsigeventas.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.cardview_reparto.view.*
import kotlinx.android.synthetic.main.fragment_reparto.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RepartoFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reparto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(RepartoViewModel::class.java)

        val repartoAdapter = RepartoAdapter(object : OnItemClickListener.RepartoListener {
            override fun onItemClick(r: Reparto, v: View, position: Int) {
                when (v.id) {
                    R.id.imageViewMap -> startActivity(
                        Intent(context, MapsActivity::class.java)
                            .putExtra("latitud", r.latitud)
                            .putExtra("longitud", r.longitud)
                            .putExtra("title", r.numeroPedido)
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
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = repartoAdapter
        repartoViewModel.getRepartos().observe(this, Observer(repartoAdapter::submitList))
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RepartoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}