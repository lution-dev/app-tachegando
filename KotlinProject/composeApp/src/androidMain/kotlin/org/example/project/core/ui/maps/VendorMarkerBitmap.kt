package org.example.project.core.ui.maps

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.ui.graphics.toArgb
import org.example.project.core.data.VendorTransport
import org.example.project.core.theme.AppColors

internal fun decodeMarkerAvatar(bytes: ByteArray): Bitmap? =
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = 3 })

internal fun renderVendorMarker(photo: Bitmap?, transport: VendorTransport?, size: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    val center = size * 0.48f
    val radius = size * 0.38f
    val border = size * 0.035f
    val orange = AppColors.Primary.toArgb()
    paint.color = orange
    canvas.drawCircle(center, center, radius + border, paint)
    paint.color = Color.WHITE
    canvas.drawCircle(center, center, radius, paint)

    if (photo != null) {
        val diameter = radius * 2
        val scale = diameter / minOf(photo.width, photo.height)
        val transform = Matrix().apply {
            setScale(scale, scale)
            postTranslate(center - photo.width * scale / 2, center - photo.height * scale / 2)
        }
        paint.shader = BitmapShader(photo, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
            setLocalMatrix(transform)
        }
        canvas.drawCircle(center, center, radius - border, paint)
        paint.shader = null
    }

    if (transport != null) {
        val badge = photo != null
        val x = if (badge) size * 0.76f else center
        val y = if (badge) size * 0.76f else center
        if (badge) {
            paint.color = orange
            canvas.drawCircle(x, y, size * 0.23f, paint)
            paint.color = Color.WHITE
            canvas.drawCircle(x, y, size * 0.20f, paint)
        }
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = size * if (badge) 0.22f else 0.39f
        canvas.drawText(transport.symbol, x, y - (paint.ascent() + paint.descent()) / 2, paint)
    }

    return bitmap
}
