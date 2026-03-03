package com.example.calcup.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import com.example.calcup.R

class Consejo1 : Fragment(R.layout.fragment_consejo1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNivel = view.findViewById<TextView>(R.id.nivelActual)

        val nivelPasado = requireArguments().getInt("numeroNivel")

        tvNivel.text = "$nivelPasado"
    }
}