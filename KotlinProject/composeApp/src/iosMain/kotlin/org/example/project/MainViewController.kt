package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.core.data.GoogleLogin

fun MainViewController() = ComposeUIViewController { App() }

fun handleAuthRedirect(url: String) = GoogleLogin.handleRedirect(url)
