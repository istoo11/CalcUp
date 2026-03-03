package com.example.calcup.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.calcup.Objetos.NivelUIPruebas
import com.example.calcup.R
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch


class Ejericicio01 : Fragment(R.layout.fragment_ejericicio01) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val infoNivel = requireArguments().getSerializable("infoNivel", NivelUI::class.java)!!
        val infoNivel = requireArguments().getSerializable("infoNivel", NivelUIPruebas::class.java)!!
        val btnAtras = view.findViewById<Button>(R.id.btn_Final)
        val operacion = view.findViewById<TextView>(R.id.tvOperacionMatematica)
        val solucion = view.findViewById<EditText>(R.id.etSolucion)
        val numero = (0..4).random()
        operacion.text = infoNivel.EJ01[numero]


            btnAtras.setOnClickListener {
                if(solucion.text.toString() == infoNivel.SolucionEJ01[numero]){
                val fechaFin = java.time.LocalDateTime.now().toString()
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {

                            supabase.from("Laderboard").update(mapOf("fin" to fechaFin)) {
                                filter {
                                    eq("id_usuario", supabase.auth.retrieveUserForCurrentSession().id)
                                    eq("id_nivel", infoNivel.nivel)
                                }
                            }

                            val nuevoNivel = infoNivel.nivel + 1
                            supabase.from("usuarios").update(mapOf("nivel" to nuevoNivel)) {
                                filter {
                                    eq("id", supabase.auth.retrieveUserForCurrentSession().id)
                                }
                            }

                            findNavController().navigate(R.id.action_ejericicio01_to_niveles)

                        } catch (e: Exception) {
                            print(e)
                        }
                    }
                }else{
                    solucion.text.clear()
                    Toast.makeText(requireContext(), "Casi, vuelve a intentarlo", Toast.LENGTH_SHORT).show()
                }
            }
    }
}