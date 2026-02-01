package com.example.calcup.Objetos

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object ClienteSupabase {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://kswqnyklxviwpqmvfbls.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtzd3FueWtseHZpd3BxbXZmYmxzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjkxMDg0NjEsImV4cCI6MjA4NDY4NDQ2MX0.xOCuPOSJ57wExMh4ke8E0VyhefpHg3laS98nqmvdY3U"
    ) {
        install(Auth.Companion)
        install(Postgrest.Companion)
    }
}