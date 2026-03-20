package com.example.calcup.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.calcup.Objetos.NivelUIPruebas
import com.example.calcup.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup



class ejercicio02 : Fragment(R.layout.fragment_ejercicio02) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val infoNivel = requireArguments().getSerializable("infoNivel", NivelUIPruebas::class.java)!!
        val serie = view.findViewById<TextView>(R.id.tvOperacionMatematica)
        val conjuntoBotones = view.findViewById<MaterialButtonToggleGroup>(R.id.grupoBotones)
        val btn1 = view.findViewById<MaterialButton>(R.id.btnOpcion1)
        val btn2 = view.findViewById<MaterialButton>(R.id.btnOpcion2)
        val btn3 = view.findViewById<MaterialButton>(R.id.btnOpcion3)
        val botones = listOf(btn1, btn2, btn3)

        val numero = (0..4).random()
        val enunciado = infoNivel.EJ02[numero]
        serie.text = enunciado
        val solucion = infoNivel.SolucionEJ02[numero]
        var valor1 = (1..7).random()
        while (valor1.toString() == solucion) {
            valor1 = (1..7).random()
        }
        var valor2 = (7..15).random()
        while (valor2.toString() == solucion) {
            valor2 = (7..15).random()
        }
        val opciones = mutableListOf(solucion, valor1, valor2)
        opciones.shuffle()
        for (i in botones.indices) {
            botones[i].text = opciones[i].toString()
        }

        conjuntoBotones.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val botonSeleccionado = view.findViewById<MaterialButton>(checkedId)
                val valorSeleccionado = botonSeleccionado.text.toString()
                if (valorSeleccionado == solucion) {
                    val bundle = bundleOf("infoNivel" to infoNivel)
                    findNavController().navigate(R.id.action_ejercicio02_to_ejercicio03,bundle)
                }
            }
        }
    }
}