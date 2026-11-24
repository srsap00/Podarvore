package com.example.podarvore.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun Historico_denuncias(onIrParaHome: () -> Unit, onDetalhes: (String) -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var denuncias by remember { mutableStateOf<List<Denuncia>?>(null) }
    var erro by remember { mutableStateOf("") }
    DisposableEffect(uid) {
        if (uid == null) { erro = "Entre na sua conta para acessar o histórico."; onDispose {} }
        else {
            val listener = RepositorioDenuncias.observarMinhas(uid) { lista, falha ->
                denuncias = lista; erro = falha.orEmpty()
            }
            onDispose { listener.remove() }
        }
    }
    Column(Modifier.fillMaxSize().background(FundoArvore), horizontalAlignment = Alignment.CenterHorizontally) {
        CabecalhoArvore("Minhas denúncias", onIrParaHome, altura = 165)
        Text("Histórico", color = VerdeArvore, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))
        when {
            erro.isNotBlank() -> MensagemArvore(erro)
            denuncias == null -> CircularProgressIndicator(color = VerdeArvore)
            denuncias!!.isEmpty() -> {
                Spacer(Modifier.height(75.dp))
                Icon(Icons.Default.Forest, "Árvore", tint = VerdeArvore, modifier = Modifier.size(80.dp))
                Text("Nenhuma denúncia foi realizada", modifier = Modifier.padding(18.dp),
                    fontSize = 18.sp, color = CorTextoArvore, textAlign = TextAlign.Center)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(denuncias!!, key = { it.id }) { item ->
                    CartaoArvore(modifier = Modifier.fillMaxWidth(.94f), onClick = { onDetalhes(item.id) }) {
                        Text("Denúncia realizada\nnúmero ${item.numero}", fontStyle = FontStyle.Italic,
                            color = Color.Black, fontSize = 16.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(6.dp))
                        Text("${item.criadaEm.emPortugues()}  •  ${item.status}",
                            color = CorTextoArvore, fontSize = 12.sp, textAlign = TextAlign.Center)
                        if (item.localizacao.isNotBlank()) Text(item.localizacao,
                            fontSize = 12.sp, color = CorTextoArvore, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
