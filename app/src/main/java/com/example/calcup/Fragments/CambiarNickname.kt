package com.example.calcup.Fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calcup.Activity.MainActivity
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch


class CambiarNickname : Fragment(R.layout.fragment_cambiar_nickname) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val botonCambio = view.findViewById<Button>(R.id.buttonCambioNickname)
        botonCambio.setOnClickListener{
            cambiarNombre()
        }
    }

    private fun cambiarNombre() {

        val nickname = view?.findViewById<TextView>(R.id.editTextNickname) ?: return
        val nicknameRepetido = view?.findViewById<TextView>(R.id.editTextNicknameRepetido) ?: return

        if (nickname.text.toString() == nicknameRepetido.text.toString()){
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val idUsuario = supabase.auth.retrieveUserForCurrentSession().id
                    supabase.from("usuarios")
                        .update({
                            set("usuario",nickname.text.toString())
                        }) {
                            filter {
                                eq("id", idUsuario)
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                (activity as? MainActivity)?.cargarDatosMenuLateral()

                AlertDialog.Builder(requireContext())
                    .setMessage("Nickname cambiado correctamente")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        findNavController().navigate(R.id.action_global_niveles)
                    }
                    .show()
            }
        }else{
            Toast.makeText(requireContext(), "El nickname no coincide", Toast.LENGTH_SHORT).show()
        }
    }
}