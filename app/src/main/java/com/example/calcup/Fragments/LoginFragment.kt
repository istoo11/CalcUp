package com.example.calcup.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calcup.R
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonLogin = view.findViewById<Button>(R.id.buttonLogin)
        botonLogin.setOnClickListener {
            val correo = view.findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
            val contrasena = view.findViewById<EditText>(R.id.editTextContraseña).text.toString().trim()
            lifecycleScope.launch {
                try {
                    supabase.auth.signInWith(Email) {
                        //email = "Romansami40@gmail.com"
                        //password = "Prueba"
                        email = correo
                        password = contrasena
                    }
                    val intent = Intent(requireActivity(), com.example.calcup.Activity.MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(requireContext(), "Login Correcto", Toast.LENGTH_SHORT).show()
                    activity?.finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Comprueba las credenciales o crea una cuenta", Toast.LENGTH_LONG).show()
                }
            }
        }
        val tvCrearCuenta = view.findViewById<TextView>(R.id.textViewCrearCuenta)
        tvCrearCuenta.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_crearCuentaFragment)
        }
    }

}