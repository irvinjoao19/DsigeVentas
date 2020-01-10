package com.dsige.dsigeventas.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Usuario
import com.dsige.dsigeventas.data.viewModel.UsuarioViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.ui.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var usuarioViewModel: UsuarioViewModel
    var logout: String = "off"

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindUI()
    }

    private fun bindUI() {
        usuarioViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(UsuarioViewModel::class.java)
        usuarioViewModel.user.observe(this, Observer<Usuario> { u ->
            if (u != null) {
                setSupportActionBar(toolbar)
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                when (u.nombreCargo) {
                    "Repartidor" -> {
                        bottomNavigation.menu.removeItem(R.id.client)
                        bottomNavigation.menu.removeItem(R.id.product)
                        bottomNavigation.menu.removeItem(R.id.pedido)
                        fragmentByDefault(MapsFragment.newInstance("", ""))
                    }
                    "Vendedor" -> {
                        bottomNavigation.menu.removeItem(R.id.map)
                        fragmentByDefault(ClientFragment.newInstance("", ""))
                    }
                    else -> fragmentByDefault(ClientFragment.newInstance("", ""))
                }
                bottomNavigation.setOnNavigationItemSelectedListener(object :
                    BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {
                        when (item.itemId) {
                            R.id.client -> {
                                changeFragment(ClientFragment.newInstance("", ""))
                                return true
                            }
                            R.id.product -> {
                                changeFragment(ProductsFragment.newInstance("", ""))
                                return true
                            }
                            R.id.pedido -> {
                                changeFragment(PedidoFragment.newInstance("", ""))
                                return true
                            }
                            R.id.map -> {
                                changeFragment(MapsFragment.newInstance("", ""))
                                return true
                            }
                            R.id.info -> {
                                logout = "on"
                                changeFragment(InfoFragment.newInstance(u.nombreCargo, ""))
                                return true
                            }
                        }
                        return true
                    }
                })
            } else {
                goLogin()
            }
        })
    }

    private fun fragmentByDefault(fragment: DaggerFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    private fun goLogin() {
        if (logout == "off") {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}