package org.example.project.core.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

// TODO: Mova estas chaves para variáveis de ambiente locais via BuildKonfig ou outro método seguro antes de ir para produção.
const val SUPABASE_URL = "https://wzgorvljvogicctpqyli.supabase.co"
const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind6Z29ydmxqdm9naWNjdHBxeWxpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY5NDY0NjUsImV4cCI6MjA5MjUyMjQ2NX0.mmpfR7vVQukdf4BrxwDn59v2KfhXxjlMMrGz1tlk_MM"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Realtime)
}
