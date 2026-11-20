package org.example.project.features.vendor

import androidx.compose.runtime.Composable

@Composable
internal actual fun rememberProductPhotoPicker(
    allowCamera: Boolean,
    onPhoto: (ByteArray) -> Unit,
    onError: (String) -> Unit
): PhotoActions? = null
