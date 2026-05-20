package com.example.calcup.Fragments

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calcup.Activity.MainActivity
import com.example.calcup.Objetos.NivelUIPruebas
import com.example.calcup.Objetos.Usuario
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class ejercicio03 : Fragment(R.layout.fragment_ejercicio03) {

    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val infoNivel = requireArguments().getSerializable("infoNivel", NivelUIPruebas::class.java)!!
        val btnAtras = view.findViewById<Button>(R.id.btn_Final)
        val operacion = view.findViewById<TextView>(R.id.tvOperacionMatematica)
        val solucion = view.findViewById<EditText>(R.id.etSolucion)
        val numero = (0..4).random()
        operacion.text = infoNivel.EJ03[numero]

        btnAtras.setOnClickListener {
            if (solucion.text.toString() == infoNivel.SolucionEJ03[numero]) {

                viewLifecycleOwner.lifecycleScope.launch {
                    val idUsuario = supabase.auth.retrieveUserForCurrentSession().id
                    try {
                        val fechaFin = java.time.Instant.now().toString()
                        supabase.from("Laderboard").update(mapOf("fin" to fechaFin)) {
                            filter {
                                eq("id_usuario", idUsuario)
                                eq("id_nivel", infoNivel.nivel)
                            }
                        }
                        val usuario = supabase.from("usuarios").select {
                            filter { eq("id", idUsuario) }
                        }.decodeSingle<Usuario>()
                        if(usuario.nivel == infoNivel.nivel) {
                            supabase.from("usuarios").update(mapOf("nivel" to (usuario.nivel+1),"puntos" to (usuario.puntos + (infoNivel.nivel*10)))) {
                                filter {
                                    eq("id", idUsuario)
                                }
                            }
                        }
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(context, R.raw.correcto)
                        mediaPlayer?.start()
                        (activity as? MainActivity)?.cargarDatosMenuLateral()
                        val bundle = bundleOf("infoNivel" to infoNivel)
                        findNavController().navigate(R.id.action_ejercicio03_to_leaderboard,bundle)
                    } catch (e: Exception) {
                        print(e)
                    }
                }
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