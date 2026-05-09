package com.example.calcup.Fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.calcup.Objetos.NivelUIPruebas
import com.example.calcup.R
import androidx.navigation.fragment.findNavController
import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager


class Ejericicio01 : Fragment(R.layout.fragment_ejericicio01) {

    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val infoNivel = requireArguments().getSerializable("infoNivel", NivelUIPruebas::class.java)!!
        val btnAtras = view.findViewById<Button>(R.id.btn_Final)
        val operacion = view.findViewById<TextView>(R.id.tvOperacionMatematica)
        val solucion = view.findViewById<EditText>(R.id.etSolucion)
        val numero = (0..4).random()
        operacion.text = infoNivel.EJ01[numero]

        btnAtras.setOnClickListener {
            if (solucion.text.toString() == infoNivel.SolucionEJ01[numero]) {
                val bundle = bundleOf("infoNivel" to infoNivel)
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(context, R.raw.correcto)
                mediaPlayer?.start()
                findNavController().navigate(R.id.action_ejericicio01_to_ejercicio02,bundle)
            } else {

                solucion.text.clear()
                val vibratorManager = context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                val tiempos = longArrayOf(0, 50, 70, 150)
                val amplitudes = intArrayOf(0, 255, 0, 255)
                val efecto = VibrationEffect.createWaveform(tiempos, amplitudes, -1)
                vibrator.vibrate(efecto)

                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(context, R.raw.fail)
                mediaPlayer?.start()
                Toast.makeText(requireContext(), "Casi, vuelve a intentarlo", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}