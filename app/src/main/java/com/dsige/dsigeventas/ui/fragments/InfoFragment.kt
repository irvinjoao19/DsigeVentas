package com.dsige.dsigeventas.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.local.model.Resumen
import com.dsige.dsigeventas.data.local.model.Usuario
import com.dsige.dsigeventas.data.viewModel.UsuarioViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.dsige.dsigeventas.ui.activities.LoginActivity
import com.dsige.dsigeventas.ui.activities.PersonalMapActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_info.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InfoFragment : DaggerFragment(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var usuarioViewModel: UsuarioViewModel

    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null

    var login: String = ""
    private var cargo: String = ""
    private var param2: String? = null


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.ok).setVisible(false).isEnabled = false
        menu.findItem(R.id.add).setVisible(false).isEnabled = false
        menu.findItem(R.id.search).setVisible(false).isEnabled = false
        menu.findItem(R.id.filter).setVisible(false).isEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cargo = it.getString(ARG_PARAM1)!!
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        usuarioViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(UsuarioViewModel::class.java)
        usuarioViewModel.user.observe(this, Observer<Usuario> { u ->
            if (u != null) {
                login = u.login
                toolbar.title = u.apellidos
                textViewDni.setText(Util.getTextHTML(u.documento), TextView.BufferType.SPANNABLE)
                textViewTelefono.setText(
                    Util.getTextHTML(u.telefono), TextView.BufferType.SPANNABLE
                )
            }
        })

        usuarioViewModel.mensajeSuccess.observe(this, Observer<String> { s ->
            if (s != null) {
                loadFinish()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity!!.finish()
            }
        })

        usuarioViewModel.mensajeError.observe(this, Observer<String> { s ->
            if (s != null) {
                loadFinish()
                Util.toastMensaje(context!!, s)
            }
        })

        if (cargo != "Administrador") {
            linearLayout.visibility = View.GONE
            fabResumen.visibility = View.GONE
        } else {
            usuarioViewModel.getResumen(Util.getFecha())
            usuarioViewModel.resumen.observe(this, Observer<Resumen> { r ->
                if (r != null) {
                    textView1.setText(
                        Util.getTextHTML("<strong>S/.</strong> " + r.totalVenta),
                        TextView.BufferType.SPANNABLE
                    )
                    textView2.setText(
                        Util.getTextHTML(r.countPedidoVenta.toString()),
                        TextView.BufferType.SPANNABLE
                    )
                    textView3.setText(
                        Util.getTextHTML(r.countClientes.toString()),
                        TextView.BufferType.SPANNABLE
                    )
                    textView4.setText(
                        Util.getTextHTML(r.mejorVendedor),
                        TextView.BufferType.SPANNABLE
                    )
                    textView5.setText(
                        Util.getTextHTML("<strong>S/.</strong> " + r.mejorProductoSoles),
                        TextView.BufferType.SPANNABLE
                    )
                    textView6.setText(
                        Util.getTextHTML(r.mejorProducto),
                        TextView.BufferType.SPANNABLE
                    )
                    textView7.setText(
                        Util.getTextHTML("<strong>S/.</strong> " + r.mejorProductoSoles),
                        TextView.BufferType.SPANNABLE
                    )
                    textView8.setText(
                        Util.getTextHTML("<strong>S/.</strong> " + r.totalDevolucion),
                        TextView.BufferType.SPANNABLE
                    )
                    textView9.setText(
                        Util.getTextHTML(r.peorVendedor),
                        TextView.BufferType.SPANNABLE
                    )
                    textView10.setText(
                        Util.getTextHTML("<strong>S/.</strong> " + r.peorVendedorSoles),
                        TextView.BufferType.SPANNABLE
                    )
                    linearLayout.visibility = View.VISIBLE
                }
            })
        }
        fabResumen.setOnClickListener(this)
    }

    private fun loadFinish() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun logout() {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Mensaje")
            .setMessage(
                String.format(
                    "%s",
                    "Al cerrar sesión estaras eliminando todo tus avances. Deseas salir del Aplicativo ?."
                )
            )
            .setPositiveButton("SI") { dialog, _ ->
                load()
                usuarioViewModel.logout(login)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textViewLado)
        textViewTitle.text = String.format("%s", "Cerrando Sesión")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabResumen -> startActivity(Intent(context, PersonalMapActivity::class.java))
            //            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}