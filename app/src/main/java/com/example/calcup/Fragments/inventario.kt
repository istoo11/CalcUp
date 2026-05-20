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
import com.example.calcup.Objetos.personalizables
import com.example.calcup.Objetos.usuario_personalizable
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class inventario : Fragment(R.layout.fragment_inventario) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarDatos()
    }

    private fun cargarDatos() {

        val listaCosmeticos = view?.findViewById<ListView>(R.id.listViewAdquiridos) ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val idUsuario = supabase.auth.retrieveUserForCurrentSession().id

                val cosmeticos = supabase.from("personalizables").select().decodeList<personalizables>()
                val idsComprados = supabase.from("usuario_personalizable").select {
                    filter { eq("id_usuario", idUsuario) }
                }.decodeList<usuario_personalizable>().map { it.id_cosmetico }

                val cosmeticosAdquiridos = cosmeticos.filter { it.id in idsComprados }
                if(cosmeticosAdquiridos.isEmpty()){
                    val cabecera = requireView().findViewById<TextView>(R.id.tituloInventario)
                    cabecera.text = "\n\nNO DISPONES DE COSMETICOS\nVE A LA SECCION TIENDA PARA ADQUIRIR ARTICULOS"
                }else {
                    listaCosmeticos.adapter = object :
                        ArrayAdapter<personalizables>(requireContext(), 0, cosmeticosAdquiridos) {
                        override fun getView(
                            posicion: Int,
                            convertView: View?,
                            padre: ViewGroup
                        ): View {
                            val viewItem = convertView ?: LayoutInflater.from(context)
                                .inflate(R.layout.cosmetico, padre, false)

                            val objeto = cosmeticosAdquiridos[posicion]

                            val btn = viewItem.findViewById<Button>(R.id.buttonComprar)
                            val txtTitulo = viewItem.findViewById<TextView>(R.id.txtPrecio)
                            val iconoCosmetico =
                                viewItem.findViewById<ImageView>(R.id.iconoCosmetico)

                            if (objeto.tipo.equals("icono")) {
                                txtTitulo.text = objeto.descripcion
                                val imagen = objeto.tipo + "_" + objeto.clave
                                val resID = context.resources.getIdentifier(
                                    imagen,
                                    "drawable",
                                    context.packageName
                                )
                                iconoCosmetico.setImageResource(resID)

                                btn.setOnClickListener {
                                    val id = objeto.clave
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        supabase.from("usuarios").update(mapOf("id_icono" to id)) {
                                            filter { eq("id", idUsuario) }
                                        }
                                    }
                                    (activity as? MainActivity)?.cargarDatosMenuLateral()
                                    Toast.makeText(requireContext(), "Icono cambiado", Toast.LENGTH_SHORT).show()
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
}