package com.example.podarvore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.podarvore.data.emPortugues
import com.example.podarvore.ui.componentes.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date

@Composable
fun InformacoesUsuarioScreen(onVoltar: () -> Unit, onLogout: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val usuario = auth.currentUser
    val uid = usuario?.uid
    var nome by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(usuario?.email.orEmpty()) }
    var criadoEm by remember { mutableStateOf<Date?>(null) }
    var carregando by remember { mutableStateOf(true) }
    var salvando by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf("") }
    var editar by remember { mutableStateOf(false) }
    var nomeEditado by remember { mutableStateOf("") }
    var localEditada by remember { mutableStateOf("") }

    DisposableEffect(uid) {
        if (uid == null) { carregando = false; erro = "Usuário não autenticado."; onDispose {} }
        else {
            val registro = FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                .addSnapshotListener { doc, falha ->
                    carregando = false
                    if (falha != null) erro = falha.localizedMessage ?: "Erro ao carregar seu perfil"
                    else {
                        erro = ""
                        nome = doc?.getString("nome").orEmpty().ifBlank { auth.currentUser?.displayName.orEmpty() }
                        email = doc?.getString("email").orEmpty().ifBlank { auth.currentUser?.email.orEmpty() }
                        localizacao = doc?.getString("localizacao").orEmpty()
                        criadoEm = doc?.getTimestamp("criadoEm")?.toDate() ?: auth.currentUser?.metadata?.creationTimestamp?.let { Date(it) }
                    }
                }
            onDispose { registro.remove() }
        }
    }
    Column(Modifier.fillMaxSize().background(FundoArvore).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        CabecalhoArvore("Informações do usuário", onVoltar)
        Box(Modifier.size(85.dp).clip(CircleShape).background(VerdeBotaoArvore), contentAlignment = Alignment.Center) {
            val foto = usuario?.photoUrl?.toString().orEmpty()
            if (foto.isNotBlank()) AsyncImage(foto, "Foto de perfil", modifier = Modifier.fillMaxSize())
            else Icon(Icons.Default.AccountCircle, "Usuário", modifier = Modifier.size(76.dp), tint = Color.White)
        }
        Spacer(Modifier.height(12.dp))
        if (carregando) CircularProgressIndicator(color = VerdeArvore)
        if (erro.isNotEmpty()) MensagemArvore(erro)
        CartaoPerfil("Nome do usuário", nome.ifBlank { "Nome não cadastrado" })
        CartaoPerfil("Localização", localizacao.ifBlank { "Não informada" })
        CartaoPerfil("E-mail", email.ifBlank { "Não informado" })
        if (criadoEm != null) CartaoPerfil("Conta criada em", criadoEm.emPortugues())
        Spacer(Modifier.height(13.dp))
        BotaoArvore("Editar informações", enabled = !carregando && !salvando, onClick = {
            nomeEditado = nome; localEditada = localizacao; editar = true
        }, modifier = Modifier.fillMaxWidth(.78f))
        Spacer(Modifier.height(9.dp))
        OutlinedButton(onClick = {
            if (email.isBlank()) erro = "Não há e-mail associado para redefinir a senha."
            else auth.sendPasswordResetEmail(email).addOnCompleteListener { tarefa ->
                erro = if (tarefa.isSuccessful) "Se sua conta permitir redefinição, enviaremos instruções para o e-mail cadastrado."
                else tarefa.exception?.localizedMessage ?: "Não foi possível solicitar a redefinição."
            }
        }, modifier = Modifier.fillMaxWidth(.78f)) { Text("Modificar chave de acesso", color = VerdeArvore) }
        Spacer(Modifier.height(9.dp))
        OutlinedButton(onClick = { auth.signOut(); onLogout() }, modifier = Modifier.fillMaxWidth(.78f)) {
            Icon(Icons.Default.Logout, null, tint = VerdeArvore)
            Spacer(Modifier.width(7.dp)); Text("Sair da conta", color = VerdeArvore)
        }
        Spacer(Modifier.height(30.dp))
    }
    if (editar) {
        AlertDialog(
            onDismissRequest = { if (!salvando) editar = false },
            title = { Text("Editar informações") },
            text = { Column {
                CampoArvore(nomeEditado, { nomeEditado = it }, "Nome completo")
                Spacer(Modifier.height(10.dp))
                CampoArvore(localEditada, { localEditada = it }, "Cidade/localização")
            } },
            confirmButton = { TextButton(enabled = !salvando, onClick = {
                val uidAtual = auth.currentUser?.uid
                if (uidAtual == null) { erro = "Sua sessão expirou."; editar = false }
                else if (nomeEditado.trim().length < 2) erro = "Informe um nome válido."
                else {
                    salvando = true
                    val novos = mapOf("nome" to nomeEditado.trim(), "localizacao" to localEditada.trim())
                    FirebaseFirestore.getInstance().collection("usuarios").document(uidAtual)
                        .set(novos, SetOptions.merge()).addOnSuccessListener {
                            auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(nomeEditado.trim()).build())
                            salvando = false; editar = false; erro = ""
                        }.addOnFailureListener {
                            salvando = false; erro = it.localizedMessage ?: "Não foi possível salvar."
                        }
                }
            }) { Text(if (salvando) "Salvando..." else "Salvar") } },
            dismissButton = { TextButton(onClick = { editar = false }, enabled = !salvando) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun CartaoPerfil(rotulo: String, conteudo: String) {
    CartaoArvore(Modifier.fillMaxWidth(.78f).padding(vertical = 7.dp)) {
        Text(rotulo, fontWeight = FontWeight.Bold, color = CorTextoArvore, textAlign = TextAlign.Center)
        Spacer(Modifier.height(5.dp))
        Text(conteudo, fontStyle = FontStyle.Italic, color = Color.Black, textAlign = TextAlign.Center)
    }
}
