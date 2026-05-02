package com.example.calcup.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import com.google.gson.Gson
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import com.example.calcup.Objetos.*
import com.google.gson.reflect.TypeToken


class Niveles : Fragment(R.layout.fragment_niveles) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaNiveles = view.findViewById<ListView>(R.id.listViewNiveles)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val uuid = supabase.auth.retrieveUserForCurrentSession().id
                val response = supabase.from("usuarios").select {
                    filter {
                        eq("id", uuid)
                    }
                }.decodeSingle<Usuario>()
                val nivelUsuario = response.nivel

                val json = requireContext().assets
                    .open("PlatillaNiveles.json")
                    .bufferedReader()
                    .use { it.readText() }

                val listType = object : TypeToken<List<NivelUIPruebas>>() {}.type
                val nivelesUI: List<NivelUIPruebas> = Gson().fromJson(json, listType)

                nivelesUI.forEach { nivelUI ->
                    nivelUI.desbloqueado = nivelUI.nivel <= nivelUsuario
                }

                listaNiveles.adapter = object : ArrayAdapter<NivelUIPruebas>(requireContext(), 0, nivelesUI) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val viewItem = convertView ?: LayoutInflater.from(context)
                            .inflate(R.layout.nivel, parent, false)

                        val nivel = nivelesUI[position]

                        val fila = viewItem.findViewById<LinearLayout>(R.id.layoutFila)
                        val txtNivel = viewItem.findViewById<TextView>(R.id.txtNivel)
                        val txtDesc = viewItem.findViewById<TextView>(R.id.txtDescripcion)
                        val btn = viewItem.findViewById<Button>(R.id.buttonJugar)
                        val img = viewItem.findViewById<ImageView>(R.id.imgIcono)

                        txtNivel.text = "Nivel ${nivel.nivel}"
                        txtDesc.text = nivel.descripcion

                        btn.backgroundTintList = null
                        if (nivel.desbloqueado) {
                            btn.isEnabled = true
                            img.setImageResource(R.drawable.abierto)
                            if (nivel.nivel % 10 == 0) {
                                fila.setBackgroundResource(R.drawable.estilo_botones_examen_final)
                                btn.setBackgroundResource(R.drawable.estilo_botones_examen_final)
                            } else if (nivel.nivel <= 10) {
                                fila.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_facil)
                                btn.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_facil)
                            } else if (nivel.nivel <= 20) {
                                fila.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_intermedio)
                                btn.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_intermedio)
                            } else {
                                fila.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_dificil)
                                btn.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_dificil)
                            }
                        } else {
                            btn.isEnabled = false
                            img.setImageResource(R.drawable.cerrado)
                            fila.setBackgroundResource(R.drawable.estilo_botones_bloqueados)
                            btn.setBackgroundResource(R.drawable.estilo_botones_bloqueados)
                        }

                        btn.setOnClickListener {
                            val bundle = bundleOf("infoNivel" to nivel)

                            findNavController().navigate(R.id.action_niveles_to_consejo1, bundle)
                        }

                        return viewItem
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}