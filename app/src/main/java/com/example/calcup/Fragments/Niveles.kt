package com.example.calcup.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import com.example.calcup.Objetos.*
import com.example.calcup.Objetos.Usuario


class Niveles : Fragment(R.layout.fragment_niveles) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridNiveles = view.findViewById<GridLayout>(R.id.gridBotones)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = supabase.auth.retrieveUserForCurrentSession()
                val uuid = user.id
                print(uuid)
                val response = supabase.from("usuarios").select {
                    filter {
                        eq("id", uuid)
                    }
                }.decodeSingle<Usuario>()
                val nivelUsuario = response.nivel

                for (i in 0 until gridNiveles.childCount) {
                    val boton = gridNiveles.getChildAt(i) as Button
                    val nivelBoton = i + 1
                    if (nivelBoton <= nivelUsuario) {
                        boton.setBackgroundResource(R.drawable.estilo_botones_desbloqueados)
                        boton.backgroundTintList = null
                        boton.isEnabled = true
                    } else {
                        boton.setBackgroundResource(R.drawable.estilo_botones_bloqueados)
                        boton.backgroundTintList = null
                        boton.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}