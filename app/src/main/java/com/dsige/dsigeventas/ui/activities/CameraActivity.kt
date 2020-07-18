package com.dsige.dsigeventas.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dsige.dsigeventas.R
import com.dsige.dsigeventas.ui.fragments.CameraFragment

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val b = intent.extras
        if (b != null) {
            savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    CameraFragment.newInstance(b.getInt("clienteId"))
                )
                .commit()
        }
    }
}
