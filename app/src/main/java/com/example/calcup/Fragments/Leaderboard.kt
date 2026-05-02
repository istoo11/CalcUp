package com.example.calcup.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calcup.Objetos.NivelUIPruebas
import com.example.calcup.Objetos.Usuario
import com.example.calcup.Objetos.puntuacion
import com.example.calcup.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class Leaderboard : Fragment(R.layout.fragment_leaderboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaNiveles = view.findViewById<ListView>(R.id.listViewClasificacion)
        val infoNivel = requireArguments().getSerializable("infoNivel", NivelUIPruebas::class.java)!!

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = supabase.from("Laderboard").select {
                    filter {
                        eq("id_nivel", infoNivel.nivel)
                    }
                }.decodeList<puntuacion>()

                val listaOrdenada = response.sortedBy {
                    val inicio = java.time.Instant.parse(it.comienzo)
                    val fin = java.time.Instant.parse(it.fin)
                    java.time.Duration.between(inicio, fin)
                }

                val usuarios = supabase.from("usuarios").select().decodeList<Usuario>()
                val mapa = usuarios.associateBy({ it.id }, { it.usuario })

                listaNiveles.adapter = object : ArrayAdapter<puntuacion>(requireContext(), 0, listaOrdenada) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                        val viewItem = convertView ?: LayoutInflater.from(context)
                            .inflate(R.layout.puntuacion, parent, false)

                        val currentUserId = supabase.auth.currentUserOrNull()?.id
                        val item = getItem(position)!!
                        val tvPosicion = viewItem.findViewById<TextView>(R.id.tvRank)
                        val tvUser = viewItem.findViewById<TextView>(R.id.tvUsername)
                        val tvTiempo = viewItem.findViewById<TextView>(R.id.tvPuntuacion)
                        val container = viewItem.findViewById<CardView>(R.id.lineaPuntuacion)

                        if (item.id_usuario == currentUserId) {
                            container.setBackgroundResource(R.drawable.estilo_botones_desbloqueados_facil)
                        }

                        when (position) {
                            0 -> container.setBackgroundResource(R.drawable.color_primero)
                            1 -> container.setBackgroundResource(R.drawable.color_segundo)
                            2 -> container.setBackgroundResource(R.drawable.color_tercero)
                        }

                        tvPosicion.text = "${position + 1}"
                        tvUser.text = mapa[item.id_usuario] ?: "No encontrado"
                        tvTiempo.text = calcularDuracion(item.comienzo, item.fin)

                        return viewItem
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Inicio_sesion", e.toString());
            }
        }
    }
    private fun calcularDuracion(comienzo: String?, fin: String?): String {
        if (comienzo == null || fin == null) return "00:00"
        return try {
            val inicio = java.time.Instant.parse(comienzo)
            val final = java.time.Instant.parse(fin)
            val duracion = java.time.Duration.between(inicio, final)

            val minutos = duracion.toMinutes()
            val segundos = duracion.seconds % 60
            String.format("%02d:%02d", minutos, segundos)
        } catch (e: Exception) {
            "00:00"
        }
    }
}