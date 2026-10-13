package com.example.podarvore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Preview
@Composable
fun ListaHorizontalBotoesExemplo() {
    val categorias = listOf("Tecnologia", "Design", "Negócios", "Saúde", "Educação", "Moda")

    // Estado para guardar qual botão foi clicado por último
    var itemSelecionado by remember { mutableStateOf("Nenhum") }

    Column(modifier = Modifier.fillMaxWidth()) {

        // Texto para mostrar a ação do clique
        Text(
            text = "Selecionado: $itemSelecionado",
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // O LazyRow contendo os botões
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias) { categoria ->
                // Criamos o botão passando uma função de callback no onClick
                BotaoHorizontalItem(
                    texto = categoria,
                    onClick = { itemSelecionado = categoria }
                )
            }
        }
    }
}

@Composable
fun BotaoHorizontalItem(
    texto: String,
    onClick: () -> Unit // Callback para o evento de clique
) {
    Card(
        modifier = Modifier
            .size(width = 130.dp, height = 50.dp)
            // O Modifier.clickable transforma o Card visualmente e funcionalmente em um botão
            .clickable { onClick() },
        shape = RoundedCornerShape(25.dp), // Cantos arredondados estilo pílula/botão
        colors = CardDefaults.cardColors(containerColor = Color(0xFF03A9F4))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}