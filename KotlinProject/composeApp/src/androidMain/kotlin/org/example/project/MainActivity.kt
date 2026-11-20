package org.example.project

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import org.example.project.core.data.GoogleLogin
import org.example.project.features.auth.AndroidBiometricSessionStore

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidBiometricSessionStore.initialize(applicationContext)
        if (savedInstanceState == null) handleAuthIntent(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthIntent(intent)
    }

    private fun handleAuthIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.dataString?.let(GoogleLogin::handleRedirect)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
