package org.example.project.features.vendor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.example.project.core.data.PhotoChange
import org.example.project.core.navigation.CameraIcon

internal data class PhotoActions(val select: () -> Unit, val capture: (() -> Unit)?, val busy: Boolean)

@Composable
internal expect fun rememberProductPhotoPicker(
    allowCamera: Boolean,
    onPhoto: (ByteArray) -> Unit,
    onError: (String) -> Unit
): PhotoActions?

@Composable
internal fun PhotoEditor(
    label: String,
    current: ByteArray?,
    change: PhotoChange?,
    busy: Boolean,
    allowCamera: Boolean = false,
    onPhotoBusy: (Boolean) -> Unit = {},
    onChange: (PhotoChange) -> Unit
) {
    var error by remember { mutableStateOf<String?>(null) }
    val actions = rememberProductPhotoPicker(
        allowCamera = allowCamera,
        onPhoto = { error = null; onChange(PhotoChange(it)) },
        onError = { error = it }
    )
    val photoBusy = actions?.busy == true
    val reportBusy by rememberUpdatedState(onPhotoBusy)
    LaunchedEffect(photoBusy) { reportBusy(photoBusy) }
    val photo = if (change == null) current else change.bytes
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall)
        PhotoPreview(photo, label, Modifier.size(96.dp))
        if (actions != null) {
            val enabled = !busy && !photoBusy
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { error = null; actions.select() }, enabled = enabled) {
                    Text(if (photo == null) "Selecionar foto" else "Trocar foto")
                }
                actions.capture?.let { capture ->
                    OutlinedIconButton(
                        onClick = { error = null; capture() },
                        enabled = enabled,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(CameraIcon, contentDescription = "Tirar foto", modifier = Modifier.size(20.dp))
                    }
                }
            }
            if (photo != null) {
                TextButton(onClick = { onChange(PhotoChange(null)) }, enabled = enabled) {
                    Text("Remover foto")
                }
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
internal fun PhotoPreview(bytes: ByteArray?, description: String?, modifier: Modifier) {
    val image = remember(bytes) { bytes?.let { runCatching { it.decodeToImageBitmap() }.getOrNull() } }
    if (image != null) {
        Image(image, description, modifier.clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop)
    }
}
