package com.example.calcup.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calcup.Objetos.Laderboard
import com.example.calcup.Objetos.NivelUIPruebas
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.time.Instant

class Consejo : Fragment(R.layout.fragment_consejo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val infoNivel = requireArguments().getSerializable("infoNivel", NivelUIPruebas::class.java)!!

        val btnAtras = view.findViewById<Button>(R.id.btn_Ejercicio01)
        val numeroNivel = view.findViewById<TextView>(R.id.nivelActual)
        val contenedor = view.findViewById<LinearLayout>(R.id.listaConsejos)
        numeroNivel.text = "${infoNivel.nivel}"

        infoNivel.consejos.forEach { consejo ->
            val textView = TextView(requireContext())
            textView.text = consejo
            textView.textSize = 18f
            textView.setPadding(0, 16, 0, 16)
            contenedor.addView(textView)
        }

        btnAtras.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val nuevoIntento = Laderboard(
                        id_usuario = supabase.auth.retrieveUserForCurrentSession().id,
                        id_nivel = infoNivel.nivel,
                        comienzo = Instant.now().toString()
                    )
                    supabase.from("Laderboard").upsert(nuevoIntento) {
                        onConflict = "id_usuario,id_nivel"
                    }
                    val bundle = bundleOf("infoNivel" to infoNivel)
                    findNavController().navigate(R.id.action_consejo1_to_ejericicio01, bundle)
                } catch (e: Exception) {
                    print(e)
                }
            }
        }
    }
}