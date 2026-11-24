package com.example.podarvore.ui.componentes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.podarvore.data.Denuncia
import com.example.podarvore.data.RepositorioDenuncias
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Busca somente a foto exibida. O histórico não transfere os Blobs. */
@Composable
fun FotoDenuncia(denuncia: Denuncia, modifier: Modifier = Modifier, cor: Color = VerdeArvore) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var bytes by remember(uid, denuncia.id, denuncia.temFoto) { mutableStateOf<ByteArray?>(null) }
    var falha by remember(uid, denuncia.id, denuncia.temFoto) { mutableStateOf<String?>(null) }
    var imagem by remember(uid, denuncia.id, denuncia.temFoto) { mutableStateOf<Bitmap?>(null) }
    DisposableEffect(uid, denuncia.id, denuncia.temFoto) {
        if (uid == null || !denuncia.temFoto || uid != denuncia.usuarioId) {
            falha = "Fotografia indisponível"
            onDispose { }
        } else {
            val listener = RepositorioDenuncias.observarFoto(denuncia.id, uid) { dados, erro ->
                bytes = dados
                falha = erro
                if (dados == null) imagem = null
            }
            onDispose { listener.remove() }
        }
    }
    LaunchedEffect(bytes) {
        val dados = bytes
        imagem = if (dados == null) null else withContext(Dispatchers.Default) {
            runCatching {
                val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(dados, 0, dados.size, limites)
                if (limites.outWidth !in 1..1280 || limites.outHeight !in 1..1280) null
                else BitmapFactory.decodeByteArray(dados, 0, dados.size)
            }.getOrNull()
        }
        if (dados != null && imagem == null) falha = "Fotografia inválida"
    }
    Box(modifier, contentAlignment = Alignment.Center) {
        val bitmap = imagem
        when {
            bitmap != null && falha == null -> Image(bitmap.asImageBitmap(), "Fotografia da árvore", modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop)
            falha != null || !denuncia.temFoto -> Icon(Icons.Default.Forest, falha ?: "Fotografia indisponível", tint = cor, modifier = Modifier.size(40.dp))
            else -> CircularProgressIndicator(color = cor, modifier = Modifier.size(24.dp))
        }
    }
}
