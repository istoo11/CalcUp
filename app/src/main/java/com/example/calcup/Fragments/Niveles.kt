package com.example.calcup.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import com.google.gson.Gson
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.calcup.Objetos.*
import com.example.calcup.Objetos.Usuario
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken


class Niveles : Fragment(R.layout.fragment_niveles) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaNiveles = view.findViewById<ListView>(R.id.listViewNiveles)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val user = supabase.auth.retrieveUserForCurrentSession()
                val uuid = user.id
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

                val listType = object : TypeToken<List<NivelUI>>() {}.type
                val nivelesUI: List<NivelUI> = Gson().fromJson(json, listType)

                nivelesUI.forEach { nivelUI ->
                    nivelUI.desbloqueado = nivelUI.nivel <= nivelUsuario
                }

                listaNiveles.adapter = object : ArrayAdapter<NivelUI>(requireContext(), 0, nivelesUI) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val viewItem = convertView ?: LayoutInflater.from(context)
                            .inflate(R.layout.nivel, parent, false)

                        val nivel = nivelesUI[position]

                        val fila = viewItem.findViewById<LinearLayout>(R.id.layoutFila)
                        val txtNivel = viewItem.findViewById<TextView>(R.id.txtNivel)
                        val txtDesc = viewItem.findViewById<TextView>(R.id.txtDescripcion)
                        val btn = viewItem.findViewById<Button>(R.id.buttonJugar)

                        txtNivel.setText("Nivel ${nivel.nivel}")
                        txtDesc.text = nivel.descripcion

                        if (nivel.desbloqueado) {
                            if(nivel.nivel<=10){
                                fila.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_facil)
                                btn.isEnabled = true
                            }else if(nivel.nivel<=20){
                                fila.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_intermedio)
                                btn.isEnabled = true
                            }else{
                                fila.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_dificil)
                                btn.isEnabled = true
                            }
                        } else {
                            fila.setBackgroundResource(R.drawable.estilo_botones_bloqueados)
                            btn.isEnabled = false
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