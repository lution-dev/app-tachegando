package org.example.project.core.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.MemoryCodeVerifierCache
import io.github.jan.supabase.auth.MemorySessionManager
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

// O site distribui a mesma chave publicável deste projeto. Permissões de dados são aplicadas via RLS.
const val SUPABASE_URL = "https://vhnkezwjknwpddqhlnfk.supabase.co"
const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_s_VPZ1wyZIbsxZ6re6s_5Q_ZIH4ER9V"

internal expect fun platformSessionManager(): SessionManager?

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_PUBLISHABLE_KEY
) {
    // Exceções do SDK podem incluir cabeçalhos de autenticação.
    Auth.setLogLevel(LogLevel.NONE)
    install(Auth) {
        if (supportsGoogleLogin) {
            flowType = FlowType.PKCE
            val protectedManager = platformSessionManager()
            sessionManager = protectedManager ?: MemorySessionManager()
            if (protectedManager != null) autoLoadFromStorage = false
            codeVerifierCache = MemoryCodeVerifierCache()
        }
    }
    install(Postgrest)
    install(Realtime)
}
