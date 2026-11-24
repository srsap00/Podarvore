package com.example.podarvore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.podarvore.data.Denuncia
import com.example.podarvore.data.RepositorioDenuncias
import com.example.podarvore.data.emPortugues
import com.example.podarvore.ui.componentes.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun TelaHome(
    onIrParaDenuncias: () -> Unit,
    onIrParaSobre: () -> Unit,
    onIrParaPerfil: () -> Unit,
    onNovaDenuncia: () -> Unit,
    onDetalhes: (String) -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var nome by remember { mutableStateOf("") }
    var lista by remember { mutableStateOf<List<Denuncia>?>(null) }
    var erro by remember { mutableStateOf("") }
    DisposableEffect(uid) {
        if (uid == null) { erro = "Você precisa entrar na conta."; onDispose {} }
        else {
            val perfil = FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                .addSnapshotListener { d, e ->
                    if (e != null) erro = "Não foi possível consultar seu perfil."
                    else nome = d?.getString("nome").orEmpty()
                        .ifBlank { FirebaseAuth.getInstance().currentUser?.displayName.orEmpty() }
                }
            val denuncias = RepositorioDenuncias.observarMinhas(uid) { dados, falha ->
                lista = dados
                if (falha != null) erro = falha
            }
            onDispose { perfil.remove(); denuncias.remove() }
        }
    }

    Column(Modifier.fillMaxSize().background(FundoArvore).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(315.dp)) {
            Image(painterResource(R.drawable.background_home), "Identidade visual PodÁrvore",
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onIrParaPerfil) { Icon(Icons.Default.AccountCircle, "Informações do usuário", tint = Color.White, modifier = Modifier.size(30.dp)) }
                    Image(painterResource(R.drawable.logo_cinza), "PodÁrvore", modifier = Modifier.width(112.dp))
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notificações", tint = Color(0xFF303A17))
                }
                HorizontalDivider(color = Color(0xFF526628), thickness = 2.dp)
                Spacer(Modifier.height(25.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(listOf("Sobre", "Minhas denúncias", "Informações do usuário")) { item ->
                        Button(
                            onClick = when (item) {
                                "Sobre" -> onIrParaSobre
                                "Minhas denúncias" -> onIrParaDenuncias
                                else -> onIrParaPerfil
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeBotaoArvore, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 13.dp)
                        ) { Text(item, fontWeight = FontWeight.Bold) }
                    }
                }
                Spacer(Modifier.height(17.dp))
                Text(if (nome.isBlank()) "Bem-vindo(a) ao PodÁrvore" else "Olá, $nome!",
                    color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 22.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Suas denúncias recentes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CorTextoArvore)
        when {
            erro.isNotBlank() -> MensagemArvore(erro)
            lista == null -> CircularProgressIndicator(color = VerdeArvore, modifier = Modifier.padding(24.dp))
            lista!!.isEmpty() -> MensagemArvore("Nenhuma denúncia foi realizada")
            else -> lista!!.take(2).forEach { d ->
                Spacer(Modifier.height(15.dp))
                ResumoDenuncia(d, onClick = { onDetalhes(d.id) })
            }
        }
        Spacer(Modifier.height(30.dp))
        FloatingActionButton(
            onClick = onNovaDenuncia, shape = CircleShape,
            containerColor = VerdeArvore, contentColor = Color.White
        ) { Icon(Icons.Default.Add, "Nova denúncia", modifier = Modifier.size(30.dp)) }
        Text("Nova denúncia", color = CorTextoArvore, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp, bottom = 35.dp))
    }
}

@Composable
fun ResumoDenuncia(denuncia: Denuncia, onClick: () -> Unit) {
    CartaoArvore(modifier = Modifier.fillMaxWidth(.83f), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            FotoDenuncia(denuncia, modifier = Modifier.size(54.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(7.dp)))
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Denúncia realizada\nnúmero ${denuncia.numero}", textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic, color = Color.Black, lineHeight = 19.sp,
                    modifier = Modifier.fillMaxWidth())
                Text("${denuncia.status} • ${denuncia.criadaEm.emPortugues()}",
                    fontSize = 11.sp, color = CorTextoArvore, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
