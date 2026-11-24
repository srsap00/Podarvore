package com.example.podarvore.data

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.DocumentSnapshot
import android.os.Handler
import android.os.Looper
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FieldValue
import java.util.concurrent.Executors
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
    val temFoto: Boolean = false,
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
    temFoto = getBoolean("temFoto") == true,
    status = getString("status").orEmpty().ifBlank { "Pendente" },
    criadaEm = getTimestamp("criadaEm")?.toDate(),
    atualizadaEm = getTimestamp("atualizadaEm")?.toDate(),
    resolucao = getString("resolucao").orEmpty(),
    andamento = getString("andamento").orEmpty()
)

object RepositorioDenuncias {
    private val banco get() = FirebaseFirestore.getInstance()
    private val executor = Executors.newSingleThreadExecutor()
    private val principal = Handler(Looper.getMainLooper())
    fun observarMinhas(
        uid: String,
        resultado: (List<Denuncia>?, String?) -> Unit
    ): ListenerRegistration = banco.collection("denuncias")
        .whereEqualTo("usuarioId", uid)
        .addSnapshotListener { documentos, erro ->
            when {
                erro != null -> resultado(null, erro.localizedMessage ?: "Falha ao consultar denúncias")
                documentos != null -> resultado(
                    documentos.documents.map { it.paraDenuncia() }.sortedByDescending { it.criadaEm?.time ?: 0L }, null
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

    fun observarFoto(id: String, uid: String, resultado: (ByteArray?, String?) -> Unit): ListenerRegistration =
        banco.collection("fotos_denuncias").document(id).addSnapshotListener { documento, erro ->
            when {
                erro != null -> resultado(null, "Não foi possível carregar a fotografia.")
                documento == null || !documento.exists() -> resultado(null, "Fotografia não encontrada.")
                documento.getString("usuarioId") != uid -> resultado(null, "Fotografia indisponível.")
                else -> {
                    val bytes = documento.getBlob("dados")?.toBytes()
                    if (bytes == null || bytes.isEmpty() || bytes.size > FotoComprimida.LIMITE_BYTES)
                        resultado(null, "Fotografia inválida.")
                    else resultado(bytes, null)
                }
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
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) { concluido(null, "Entre na sua conta para denunciar"); return }
        if (descricao.trim().length !in 10..5000 || localizacao.trim().length !in 1..500 ||
            area !in listOf("Urbana", "Rural") ||
            orgao !in listOf("Prefeitura", "Companhia elétrica", "Departamento de trânsito")) {
            concluido(null, "Confira a descrição, localização, área e órgão responsável."); return
        }
        val app = context.applicationContext
        executor.execute {
            try {
                val bytes = FotoComprimida.jpeg(app, foto)
                principal.post {
                    if (FirebaseAuth.getInstance().currentUser?.uid != uid) {
                        concluido(null, "Sua sessão mudou. Entre novamente.")
                        return@post
                    }
                    val documento = banco.collection("denuncias").document()
                    val fotografia = banco.collection("fotos_denuncias").document(documento.id)
                    val dados = hashMapOf<String, Any>(
                        "usuarioId" to uid,
                        "numero" to documento.id.take(12).uppercase(Locale.ROOT),
                        "descricao" to descricao.trim(),
                        "localizacao" to localizacao.trim(),
                        "area" to area,
                        "orgaoResponsavel" to orgao,
                        "temFoto" to true,
                        "status" to "Pendente",
                        "criadaEm" to FieldValue.serverTimestamp(),
                        "atualizadaEm" to FieldValue.serverTimestamp()
                    )
                    latitude?.let { dados["latitude"] = it }
                    longitude?.let { dados["longitude"] = it }
                    val imagem = mapOf(
                        "usuarioId" to uid,
                        "dados" to Blob.fromBytes(bytes),
                        "tipo" to "image/jpeg",
                        "criadaEm" to FieldValue.serverTimestamp()
                    )
                    // O registro e a fotografia são gravados juntos, ou nenhum é confirmado.
                    banco.batch().set(documento, dados).set(fotografia, imagem).commit()
                        .addOnSuccessListener { concluido(documento.id, null) }
                        .addOnFailureListener { concluido(null, "Falha ao salvar denúncia: ${it.localizedMessage}") }
                }
            } catch (e: Exception) {
                principal.post { concluido(null, "Não foi possível preparar a foto. Selecione outra imagem. ${e.localizedMessage.orEmpty()}") }
            } catch (_: OutOfMemoryError) {
                principal.post { concluido(null, "Imagem muito grande. Selecione outra fotografia.") }
            }
        }
    }
}
