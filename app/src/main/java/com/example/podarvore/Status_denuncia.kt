package com.example.podarvore

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.podarvore.data.Denuncia
import com.example.podarvore.data.RepositorioDenuncias
import com.example.podarvore.data.emPortugues
import com.example.podarvore.ui.componentes.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DetalhesDenunciaScreen(denunciaId: String, onBack: () -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current
    var denuncia by remember(denunciaId) { mutableStateOf<Denuncia?>(null) }
    var erro by remember(denunciaId) { mutableStateOf("") }
    DisposableEffect(denunciaId, uid) {
        if (uid == null) { erro = "Você precisa entrar na sua conta."; onDispose {} }
        else {
            val listener = RepositorioDenuncias.observarUma(denunciaId, uid) { dado, falha ->
                denuncia = dado
                erro = falha.orEmpty()
            }
            onDispose { listener.remove() }
        }
    }

    Column(Modifier.fillMaxSize().background(FundoArvore).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        CabecalhoArvore("Status da denúncia", onBack, altura = 148)
        when {
            erro.isNotBlank() -> MensagemArvore(erro)
            denuncia == null -> CircularProgressIndicator(color = VerdeArvore, modifier = Modifier.padding(30.dp))
            else -> {
                val d = denuncia!!
                Box(Modifier.offset(y = (-5).dp).size(175.dp).clip(CircleShape).background(VerdeArvore), contentAlignment = Alignment.Center) {
                    if (d.fotoUrl.isNotBlank())
                        AsyncImage(model = d.fotoUrl, contentDescription = "Fotografia da árvore denunciada",
                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Icon(Icons.Default.Forest, "Fotografia não disponível", tint = Color.White, modifier = Modifier.size(70.dp))
                }
                Text("Número ${d.numero}", fontStyle = FontStyle.Italic, fontSize = 14.sp, color = Color.Black)
                Spacer(Modifier.height(12.dp))
                Text("Status: ${d.status}", color = VerdeArvore, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(15.dp))
                CartaoArvore(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text("Localização", color = CorTextoArvore, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(5.dp))
                    Icon(Icons.Default.LocationOn, "Localização", tint = VerdeArvore)
                    Text(d.localizacao.ifBlank { "Localização não informada" }, color = Color.Black, textAlign = TextAlign.Center)
                    if (d.latitude != null && d.longitude != null)
                        Text("${d.latitude}, ${d.longitude}", fontSize = 12.sp, color = CorTextoArvore)
                    TextButton(onClick = {
                        val pesquisa = if (d.latitude != null && d.longitude != null)
                            "${d.latitude},${d.longitude}" else d.localizacao
                        val uri = Uri.parse("geo:0,0?q=${Uri.encode(pesquisa)}")
                        try { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                        catch (_: ActivityNotFoundException) {
                            context.startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(pesquisa)}")))
                        }
                    }, enabled = d.localizacao.isNotBlank() || d.latitude != null) {
                        Text("Visualizar localização", color = VerdeArvore)
                    }
                }
                Spacer(Modifier.height(15.dp))
                CartaoArvore(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    CampoDetalhe("Data da denúncia", d.criadaEm.emPortugues())
                    CampoDetalhe("Descrição do usuário", d.descricao)
                    CampoDetalhe("Área", d.area)
                    CampoDetalhe("Órgão responsável", d.orgaoResponsavel)
                }
                Spacer(Modifier.height(15.dp))
                CartaoArvore(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    CampoDetalhe("Resolução do problema", d.resolucao.ifBlank { "Aguardando resolução do órgão responsável." })
                    if (d.andamento.isNotBlank()) CampoDetalhe("Andamento", d.andamento)
                    if (d.atualizadaEm != null) CampoDetalhe("Última atualização", d.atualizadaEm.emPortugues())
                }
                Spacer(Modifier.height(35.dp))
            }
        }
    }
}

@Composable
private fun CampoDetalhe(titulo: String, valor: String) {
    Text(titulo, fontWeight = FontWeight.Bold, color = CorTextoArvore, fontSize = 13.sp)
    Text(valor.ifBlank { "Não informado" }, color = Color.Black, fontSize = 15.sp,
        fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp, bottom = 13.dp))
}
