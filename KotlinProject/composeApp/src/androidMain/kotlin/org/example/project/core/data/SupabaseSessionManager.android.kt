package org.example.project.core.data

import io.github.jan.supabase.auth.SessionManager
import org.example.project.features.auth.AndroidBiometricSessionStore

internal actual fun platformSessionManager(): SessionManager? = AndroidBiometricSessionStore
