package com.dsige.dsigeventas.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.data.viewModel.ClienteViewModel
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
import com.dsige.dsigeventas.helper.Util
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_preview_camera.*
import java.io.File
import javax.inject.Inject

class PreviewCameraActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabClose -> {
                startActivity(
                    Intent(this, CameraActivity::class.java)
                        .putExtra("clienteId", clienteId)
                )
                finish()
            }
            R.id.fabOk -> clienteViewModel.updatePhotoCliente(clienteId, nameImg)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var clienteViewModel: ClienteViewModel
    private var clienteId = 0
    private var nameImg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)
        val b = intent.extras
        if (b != null) {
            bindUI(b.getInt("clienteId"), b.getString("nameImg")!!)
        }
    }

    private fun bindUI(id: Int, name: String) {
        clienteViewModel =
            ViewModelProvider(this, viewModelFactory).get(ClienteViewModel::class.java)

        clienteId = id
        nameImg = name
        fabClose.setOnClickListener(this)
        fabOk.setOnClickListener(this)
        textViewImg.text = name
        Handler().postDelayed({
            val f = File(Util.getFolder(this), name)
            Picasso.get().load(f)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        Log.i("TAG", e.toString())
                    }
                })
        }, 200)

        clienteViewModel.mensajeSuccess.observe(this, Observer { s ->
            if (s != null) {
                finish()
            }
        })

        clienteViewModel.mensajeError.observe(this, Observer { s ->
            if (s != null) {
                Util.toastMensaje(this, s)
            }
        })
    }
}