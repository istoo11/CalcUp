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

        cargarDatos()
    }

    private fun cargarDatos() {

        val listaCosmeticos = view?.findViewById<ListView>(R.id.listViewCosmeticos) ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val idUsuario = supabase.auth.retrieveUserForCurrentSession().id

                val cosmeticos =
                    supabase.from("personalizables").select().decodeList<personalizables>()
                val idsComprados = supabase.from("usuario_personalizable").select {
                    filter { eq("id_usuario", idUsuario) }
                }.decodeList<usuario_personalizable>().map { it.id_cosmetico }

                val cosmeticosDisponibles = cosmeticos.filterNot { it.id in idsComprados }
                if(cosmeticosDisponibles.isEmpty()){
                    val cabecera = requireView().findViewById<TextView>(R.id.tituloTienda)
                    cabecera.text = "\n\nENHORABUENA\nYA DISPONES DE TODOS LOS ARTICULOS"
                } else {
                    listaCosmeticos.adapter = object :
                        ArrayAdapter<personalizables>(requireContext(), 0, cosmeticosDisponibles) {
                        override fun getView(
                            posicion: Int,
                            convertView: View?,
                            padre: ViewGroup
                        ): View {
                            val viewItem = convertView ?: LayoutInflater.from(context)
                                .inflate(R.layout.cosmetico, padre, false)

                            val objeto = getItem(posicion)!!

                            val textoPrecio = viewItem.findViewById<TextView>(R.id.txtPrecio)
                            val txtDesc = viewItem.findViewById<TextView>(R.id.txtDescripcion)
                            val btn = viewItem.findViewById<Button>(R.id.buttonComprar)
                            val iconoCosmetico =
                                viewItem.findViewById<ImageView>(R.id.iconoCosmetico)
                            btn.text = "Comprar"

                            if (objeto.tipo.equals("icono")) {
                                val imagen = objeto.tipo + "_" + objeto.clave
                                val resID = context.resources.getIdentifier(
                                    imagen,
                                    "drawable",
                                    context.packageName
                                )
                                iconoCosmetico.setImageResource(resID)
                                val numeroPrecio = (objeto.clave.toInt() * 100)
                                textoPrecio.text = numeroPrecio.toString()
                                txtDesc.text = objeto.descripcion

                                btn.setOnClickListener {
                                    ejecutarCompra(objeto, numeroPrecio)
                                }
                            }

                            return viewItem
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun ejecutarCompra(objeto: personalizables, valorPrecio: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val idUsuario = supabase.auth.retrieveUserForCurrentSession().id
            try {
                val usuario = supabase.from("usuarios").select {
                    filter { eq("id", idUsuario) }
                }.decodeSingle<Usuario>()

                if (usuario.puntos >= valorPrecio) {
                    val registro = usuario_personalizable(idUsuario, objeto.id)
                    supabase.from("usuario_personalizable").upsert(registro)
                    supabase.from("usuarios")
                        .update(mapOf("puntos" to (usuario.puntos - valorPrecio))) {
                            filter { eq("id", idUsuario) }
                        }

                    cargarDatos()

                    (activity as? MainActivity)?.cargarDatosMenuLateral()
                    Toast.makeText(requireContext(), "Compra realizada!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Puntos insuficientes", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error en la compra", Toast.LENGTH_SHORT).show()
            }
        }
    }

}