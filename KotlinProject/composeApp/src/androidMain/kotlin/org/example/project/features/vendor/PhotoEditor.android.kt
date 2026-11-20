package org.example.project.features.vendor

import java.io.ByteArrayOutputStream
import java.io.File

import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.example.project.core.data.MAX_PHOTO_BYTES

private const val PHOTO_LOG = "TaChegandoPhoto"
private const val CAPTURE_DIRECTORY = "product-photos"

class ProductPhotoFileProvider : FileProvider()

private object CaptureProductPhoto : TakePicture() {
    override fun createIntent(context: Context, input: Uri): Intent =
        super.createIntent(context, input).apply {
            clipData = ClipData.newRawUri("Foto do produto", input)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
}

@Composable
internal actual fun rememberProductPhotoPicker(
    allowCamera: Boolean,
    onPhoto: (ByteArray) -> Unit,
    onError: (String) -> Unit
): PhotoActions? {
    val context = LocalContext.current.applicationContext
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val importer: PhotoImport = viewModel { PhotoImport() }
    val busy by importer.busyState.collectAsState()
    val deliver by rememberUpdatedState(onPhoto)
    val fail by rememberUpdatedState(onError)

    LaunchedEffect(importer) {
        for (result in importer.outcomes) {
            when (result) {
                is PhotoOutcome.Image -> deliver(result.bytes)
                is PhotoOutcome.Error -> fail(result.message)
            }
        }
    }

    val readImage: (Uri?) -> Unit = { uri ->
        if (uri == null) importer.busy = false
        else importer.read(context.contentResolver, uri)
    }
    val documents = rememberLauncherForActivityResult(OpenDocument(), onResult = readImage)
    val gallery = rememberLauncherForActivityResult(GetContent(), onResult = readImage)
    val camera = rememberLauncherForActivityResult(CaptureProductPhoto) { accepted ->
        val file = importer.captureFile
        importer.captureFile = null
        if (accepted && file != null) {
            importer.read(context.contentResolver, Uri.fromFile(file), file)
        } else {
            file?.delete()
            importer.busy = false
        }
    }

    return PhotoActions(
        select = {
            if (!importer.busy && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                importer.busy = true
                if (!tryPhotoLaunch { documents.launch(arrayOf("image/*")) } &&
                    !tryPhotoLaunch { gallery.launch("image/*") }
                ) {
                    importer.fail("Não foi possível abrir a galeria neste aparelho.")
                }
            }
        },
        capture = if (allowCamera) {
            {
                if (!importer.busy && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    importer.busy = true
                    try {
                        val directory = File(context.cacheDir, CAPTURE_DIRECTORY)
                        check(directory.isDirectory || directory.mkdirs())
                        val file = File.createTempFile("captura-", ".jpg", directory)
                        importer.captureFile = file
                        val uri = FileProvider.getUriForFile(context, context.packageName + ".product-photos", file)
                        camera.launch(uri)
                    } catch (error: Exception) {
                        Log.w(PHOTO_LOG, "Falha ao abrir a câmera: ${error.javaClass.simpleName}")
                        importer.captureFile?.delete()
                        importer.captureFile = null
                        importer.fail("Não foi possível abrir a câmera. Você pode escolher uma foto da galeria.")
                    }
                }
            }
        } else null,
        busy = busy
    )
}

private inline fun tryPhotoLaunch(launch: () -> Unit): Boolean = try {
    launch()
    true
} catch (error: RuntimeException) {
    Log.w(PHOTO_LOG, "Falha ao abrir o seletor: ${error.javaClass.simpleName}")
    false
}

private sealed interface PhotoOutcome {
    data class Image(val bytes: ByteArray) : PhotoOutcome
    data class Error(val message: String) : PhotoOutcome
}

private class PhotoImport : ViewModel() {
    val busyState = MutableStateFlow(false)
    var busy: Boolean
        get() = busyState.value
        set(value) { busyState.value = value }
    val outcomes = Channel<PhotoOutcome>(Channel.BUFFERED)
    var captureFile: File? = null

    fun read(resolver: ContentResolver, uri: Uri, temporary: File? = null) {
        busy = true
        viewModelScope.launch {
            try {
                val jpeg = withContext(Dispatchers.IO) { encodePhoto(resolver, uri) }
                outcomes.trySend(PhotoOutcome.Image(jpeg))
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(PHOTO_LOG, "Falha ao ler a imagem: ${error.javaClass.simpleName}")
                outcomes.trySend(PhotoOutcome.Error("Não foi possível abrir esta imagem. Escolha outra foto."))
            } finally {
                temporary?.delete()
                busy = false
            }
        }
    }

    fun fail(message: String) {
        outcomes.trySend(PhotoOutcome.Error(message))
        busy = false
    }

    override fun onCleared() {
        outcomes.close()
        captureFile?.delete()
    }
}

private const val MAX_SIDE = 512

private fun encodePhoto(resolver: ContentResolver, uri: Uri): ByteArray {
    val bitmap = readProductBitmap(resolver, uri)
    return try {
        val output = ByteArrayOutputStream()
        check(bitmap.compress(Bitmap.CompressFormat.JPEG, 78, output))
        if (output.size() > MAX_PHOTO_BYTES) {
            output.reset()
            check(bitmap.compress(Bitmap.CompressFormat.JPEG, 55, output))
        }
        check(output.size() <= MAX_PHOTO_BYTES)
        output.toByteArray()
    } finally {
        bitmap.recycle()
    }
}

private fun readProductBitmap(resolver: ContentResolver, uri: Uri): Bitmap {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Imagem inválida" }
    var sample = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / sample > MAX_SIDE * 2) sample *= 2
    val options = BitmapFactory.Options().apply { inSampleSize = sample }
    val bitmap = checkNotNull(resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) })
    val orientation = runCatching {
        resolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        }
    }.getOrNull()
    val transform = Matrix().apply {
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> setScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> { setRotate(90f); postScale(-1f, 1f) }
            ExifInterface.ORIENTATION_ROTATE_90 -> setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> { setRotate(-90f); postScale(-1f, 1f) }
            ExifInterface.ORIENTATION_ROTATE_270 -> setRotate(-90f)
        }
        val scale = (MAX_SIDE.toFloat() / maxOf(bitmap.width, bitmap.height)).coerceAtMost(1f)
        postScale(scale, scale)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, transform, true).also {
        if (it !== bitmap) bitmap.recycle()
    }
}
