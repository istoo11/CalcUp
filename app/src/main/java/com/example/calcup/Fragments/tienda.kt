package com.example.calcup.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.calcup.Activity.MainActivity
import com.example.calcup.Objetos.Usuario
import com.example.calcup.Objetos.personalizables
import com.example.calcup.Objetos.usuario_personalizable
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlin.Int


class tienda : Fragment(R.layout.fragment_tienda) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaCosmeticos = view.findViewById<ListView>(R.id.listViewCosmeticos)

        viewLifecycleOwner.lifecycleScope.launch {

            val idUsuario = supabase.auth.retrieveUserForCurrentSession().id
            val cosmeticos = supabase.from("personalizables").select().decodeList<personalizables>()

            listaCosmeticos.adapter = object : ArrayAdapter<personalizables>(requireContext(), 0, cosmeticos) {
                override fun getView(posicion: Int, convertView: View?, padre: ViewGroup): View {
                    val viewItem = convertView ?: LayoutInflater.from(context)
                        .inflate(R.layout.cosmetico, padre, false)

                    val objeto = cosmeticos[posicion]

                    val textoPrecio = viewItem.findViewById<TextView>(R.id.txtPrecio)
                    val txtDesc = viewItem.findViewById<TextView>(R.id.txtDescripcion)
                    val btn = viewItem.findViewById<Button>(R.id.buttonComprar)
                    val iconoCosmetico = viewItem.findViewById<ImageView>(R.id.iconoCosmetico)

                    if(objeto.tipo.equals("icono")){
                        val imagen = objeto.tipo + "_" + objeto.clave
                        val resID = context.resources.getIdentifier(imagen, "drawable", context.packageName)
                        iconoCosmetico.setImageResource(resID)
                        val valorPrecio = (objeto.clave.toInt() * 100)
                        textoPrecio.text = valorPrecio.toString()
                        txtDesc.text = objeto.descripcion

                        btn.setOnClickListener {
                            ejecutarCompra(objeto, valorPrecio, idUsuario)
                        }

                    }else{
                        iconoCosmetico.setImageResource(R.drawable.icono_0)
                        textoPrecio.text = "No es un icono"
                        txtDesc.text = objeto.descripcion

                        btn.setOnClickListener {

                        }
                    }
                    return viewItem
                }
            }
        }
    }

    fun ejecutarCompra(objeto: personalizables, valorPrecio: Int, idUsuario: String) {

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val idsComprados = supabase.from("usuario_personalizable").select{
                    filter {
                        eq("id_usuario",idUsuario)
                    }
                }.decodeList<usuario_personalizable>().map { it.id_cosmetico }

                val usuario = supabase.from("usuarios").select {
                    filter { eq("id", idUsuario) }
                }.decodeSingle<Usuario>()

                if (usuario.puntos >= valorPrecio ) {
                    if(objeto.id !in idsComprados) {
                        val registro = usuario_personalizable(idUsuario, objeto.id)
                        supabase.from("usuario_personalizable").upsert(registro)

                        supabase.from("usuarios")
                            .update(mapOf("puntos" to (usuario.puntos - valorPrecio))) {
                                filter { eq("id", idUsuario) }
                            }

                        (activity as? MainActivity)?.cargarDatosMenuLateral()
                    }else{
                        Toast.makeText(requireContext(), "Ya tienes el objeto", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No tienes puntos suficientes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error en la compra", Toast.LENGTH_SHORT).show()
            }
        }
    }

}