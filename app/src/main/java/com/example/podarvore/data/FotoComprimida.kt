package com.example.podarvore.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

/** Reencoda sem metadados EXIF e limita a resolução e o tamanho do Blob. Executar fora da UI. */
object FotoComprimida {
    const val LIMITE_BYTES = 300 * 1024
    private const val LADO_MAXIMO = 1280

    fun jpeg(context: Context, uri: Uri): ByteArray {
        var bitmap = decodificar(context, uri)
        try {
            while (true) {
                for (qualidade in listOf(85, 75, 65, 55, 45, 35)) {
                    val dados = ByteArrayOutputStream().use { saida ->
                        check(bitmap.compress(Bitmap.CompressFormat.JPEG, qualidade, saida)) { "Falha ao codificar JPEG." }
                        saida.toByteArray()
                    }
                    if (dados.isNotEmpty() && dados.size <= LIMITE_BYTES) return dados
                }
                val lado = max(bitmap.width, bitmap.height)
                check(lado > 320) { "A fotografia excede o limite permitido." }
                val menor = redimensionar(bitmap, max(320, (lado * .75f).roundToInt()))
                if (menor !== bitmap) bitmap.recycle()
                bitmap = menor
            }
        } finally {
            bitmap.recycle()
        }
    }

    private fun decodificar(context: Context, uri: Uri): Bitmap {
        val resolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= 28) {
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri)) { decoder, info, _ ->
                val (w, h) = dimensoes(info.size.width, info.size.height, LADO_MAXIMO)
                decoder.setTargetSize(w, h)
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        }
        val opcoes = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opcoes) }
        require(opcoes.outWidth > 0 && opcoes.outHeight > 0) { "Arquivo de imagem inválido." }
        var amostra = 1
        while (max(opcoes.outWidth, opcoes.outHeight) / amostra > LADO_MAXIMO * 2) amostra *= 2
        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = amostra })
        } ?: error("Não foi possível ler a imagem.")
        val orientacao = runCatching {
            resolver.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
        }.getOrNull() ?: ExifInterface.ORIENTATION_NORMAL
        val matriz = Matrix().apply {
            when (orientacao) {
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> setScale(-1f, 1f)
                ExifInterface.ORIENTATION_ROTATE_180 -> setRotate(180f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> setScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> { setRotate(90f); postScale(-1f, 1f) }
                ExifInterface.ORIENTATION_ROTATE_90 -> setRotate(90f)
                ExifInterface.ORIENTATION_TRANSVERSE -> { setRotate(-90f); postScale(-1f, 1f) }
                ExifInterface.ORIENTATION_ROTATE_270 -> setRotate(-90f)
            }
        }
        val corrigido = if (matriz.isIdentity) bitmap else Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
        if (corrigido !== bitmap) bitmap.recycle()
        val final = redimensionar(corrigido, LADO_MAXIMO)
        if (final !== corrigido) corrigido.recycle()
        return final
    }

    private fun dimensoes(w: Int, h: Int, limite: Int): Pair<Int, Int> {
        val fator = minOf(1f, limite.toFloat() / max(w, h))
        return max(1, (w * fator).roundToInt()) to max(1, (h * fator).roundToInt())
    }

    private fun redimensionar(bitmap: Bitmap, limite: Int): Bitmap {
        val (w, h) = dimensoes(bitmap.width, bitmap.height, limite)
        return if (w == bitmap.width && h == bitmap.height) bitmap else Bitmap.createScaledBitmap(bitmap, w, h, true)
    }
}
