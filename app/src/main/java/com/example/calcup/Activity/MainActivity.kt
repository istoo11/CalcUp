package com.example.calcup.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.calcup.Objetos.ClienteSupabase
import com.example.calcup.Objetos.Usuario
import com.example.calcup.R
import com.google.android.material.navigation.NavigationView
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

val supabase = ClienteSupabase.supabase
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.main)
        val navView: NavigationView = findViewById(R.id.nav_listaNivel)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val btnCerrarSesion = findViewById<Button>(R.id.btn_cerrar_sesion)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "MI APLICACIÓN TFG"

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        cargarDatosMenuLateral()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inventario -> {
                    // Codigo para ir al inventario
                }
                R.id.nav_tienda -> {
                    // Codigo para ir a la tienda
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        btnCerrarSesion.setOnClickListener {
            lifecycleScope.launch {
                try {
                    supabase.auth.signOut()
                    val intent = Intent(this@MainActivity, LoginMainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun cargarDatosMenuLateral() {
        val navView: NavigationView = findViewById(R.id.nav_listaNivel)
        val headerView = navView.getHeaderView(0)

        val imgPerfil = headerView.findViewById<ImageView>(R.id.fotoPerfil)
        val tvNombre = headerView.findViewById<TextView>(R.id.textView_nombre)
        val tvPuntos = headerView.findViewById<TextView>(R.id.textView_puntos)

        lifecycleScope.launch {
            try {
                val uuid = supabase.auth.currentUserOrNull()!!.id

                val response = supabase.from("usuarios").select {
                    filter {
                        eq("id", uuid)
                    }
                }.decodeSingle<Usuario>()

                tvNombre.text = response.usuario
                tvPuntos.text = "${response.puntos} Puntos"
                imgPerfil.setImageResource(resources.getIdentifier("icono_${response.id_icono}", "drawable", packageName))

            } catch (e: Exception) {
                Log.e("SupabaseError", "Fallo al cargar cabecera: ${e.message}")
            }
        }
    }
}