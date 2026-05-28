package com.example.calcup.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.calcup.Objetos.ClienteSupabase
import com.example.calcup.Objetos.Usuario
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

val supabase = ClienteSupabase.supabase

class CrearCuentaFragment : Fragment(R.layout.fragment_crear_cuenta) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boton = view.findViewById<Button>(R.id.buttonConfirmar)
        boton.setOnClickListener {

            val correo = view.findViewById<EditText>(R.id.editTextCrearCuentaEmail).text.toString()
            val contraseña = view.findViewById<EditText>(R.id.editTextCrearCuentaContraseña).text.toString()
            val contraseña2 = view.findViewById<EditText>(R.id.editTextCrearCuentaContraseñaRepetida).text.toString()
            val nickname = view.findViewById<EditText>(R.id.editTextUsuario).text.toString()

            if (correo.isBlank() || contraseña.isBlank() || contraseña2.isBlank()|| nickname.isBlank()) {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraseña != contraseña2){
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val creado = supabase.auth.signUpWith(Email) {
                        email = correo
                        password = contraseña
                    }

                    if (creado != null) {
                        val usuario = Usuario(creado.id, nickname,  0, 1,0)
                        ClienteSupabase.supabase.from("usuarios").insert(usuario)
                        Toast.makeText(requireContext(), "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        AlertDialog.Builder(requireContext())
                            .setMessage("Compruebe que la contraseña contenga al menos 7 caracteres")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                } catch (e: Exception) {
                    Log.e("Inicio_sesion", e.toString());
                    AlertDialog.Builder(requireContext())
                        .setMessage("El usuario no se ha podido crear")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }
}