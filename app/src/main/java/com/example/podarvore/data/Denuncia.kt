package com.example.podarvore.data

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Campos persistidos na coleção Firestore "denuncias". Nunca são gerados registros de demonstração. */
data class Denuncia(
    val id: String = "",
    val usuarioId: String = "",
    val numero: String = "",
    val descricao: String = "",
    val localizacao: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val area: String = "",
    val orgaoResponsavel: String = "",
    val fotoUrl: String = "",
    val status: String = "Pendente",
    val criadaEm: Date? = null,
    val atualizadaEm: Date? = null,
    val resolucao: String = "",
    val andamento: String = ""
)

fun Date?.emPortugues(): String = this?.let {
    SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")).format(it)
} ?: "Aguardando registro da data"

private fun DocumentSnapshot.paraDenuncia() = Denuncia(
    id = id,
    usuarioId = getString("usuarioId").orEmpty(),
    numero = getString("numero") ?: id.take(12).uppercase(Locale.ROOT),
    descricao = getString("descricao").orEmpty(),
    localizacao = getString("localizacao").orEmpty(),
    latitude = getDouble("latitude"),
    longitude = getDouble("longitude"),
    area = getString("area").orEmpty(),
    orgaoResponsavel = getString("orgaoResponsavel").orEmpty(),
    fotoUrl = getString("fotoUrl").orEmpty(),
    status = getString("status").orEmpty().ifBlank { "Pendente" },
    criadaEm = getTimestamp("criadaEm")?.toDate(),
    atualizadaEm = getTimestamp("atualizadaEm")?.toDate(),
    resolucao = getString("resolucao").orEmpty(),
    andamento = getString("andamento").orEmpty()
)

object RepositorioDenuncias {
    private val banco get() = FirebaseFirestore.getInstance()
    fun observarMinhas(
        uid: String,
        resultado: (List<Denuncia>?, String?) -> Unit
    ): ListenerRegistration = banco.collection("denuncias")
        .whereEqualTo("usuarioId", uid)
        .addSnapshotListener { documentos, erro ->
            when {
                erro != null -> resultado(null, erro.localizedMessage ?: "Falha ao consultar denúncias")
                documentos != null -> resultado(
                    documentos.documents.mapNotNull { documento ->
                        (documento as? QueryDocumentSnapshot)?.paraDenuncia()
                    }.sortedByDescending { it.criadaEm?.time ?: 0L }, null
                )
            }
        }

    fun observarUma(
        id: String,
        uid: String,
        resultado: (Denuncia?, String?) -> Unit
    ): ListenerRegistration = banco.collection("denuncias").document(id)
        .addSnapshotListener { documento, erro ->
            when {
                erro != null -> resultado(null, erro.localizedMessage ?: "Falha ao carregar denúncia")
                documento == null || !documento.exists() -> resultado(null, "Denúncia não encontrada")
                documento.getString("usuarioId") != uid -> resultado(null, "Você não pode acessar esta denúncia")
                else -> resultado(documento.paraDenuncia(), null)
            }
        }

    fun enviar(
        context: Context,
        foto: Uri,
        descricao: String,
        localizacao: String,
        latitude: Double?,
        longitude: Double?,
        area: String,
        orgao: String,
        concluido: (String?, String?) -> Unit
    ) {
        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario == null) { concluido(null, "Entre na sua conta para denunciar"); return }
        val documento = banco.collection("denuncias").document()
        val caminho = FirebaseStorage.getInstance().reference.child("denuncias/${usuario.uid}/${documento.id}")
        val mime = context.contentResolver.getType(foto) ?: "image/jpeg"
        val meta = StorageMetadata.Builder().setContentType(mime).build()
        caminho.putFile(foto, meta)
            .addOnFailureListener { concluido(null, "Falha no envio da foto: ${it.localizedMessage}") }
            .addOnSuccessListener {
                caminho.downloadUrl
                    .addOnFailureListener { concluido(null, "Falha ao obter imagem: ${it.localizedMessage}") }
                    .addOnSuccessListener { url ->
                        val dados = hashMapOf<String, Any>(
                            "usuarioId" to usuario.uid,
                            "numero" to documento.id.take(12).uppercase(Locale.ROOT),
                            "descricao" to descricao.trim(),
                            "localizacao" to localizacao.trim(),
                            "area" to area,
                            "orgaoResponsavel" to orgao,
                            "fotoUrl" to url.toString(),
                            "fotoCaminho" to caminho.path,
                            "status" to "Pendente",
                            "criadaEm" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                            "atualizadaEm" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )
                        latitude?.let { dados["latitude"] = it }
                        longitude?.let { dados["longitude"] = it }
                        documento.set(dados)
                            .addOnSuccessListener { concluido(documento.id, null) }
                            .addOnFailureListener { concluido(null, "Falha ao salvar denúncia: ${it.localizedMessage}") }
                    }
            }
    }
}
