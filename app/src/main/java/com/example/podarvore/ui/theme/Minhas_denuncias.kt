package com.example.podarvore.ui.theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.podarvore.R

// Definição das cores baseadas na imagem
val VerdeEscuro = Color(0xFF6B8E42)
val VerdeBotao = Color(0xFF5A7A37)
val FundoClaro = Color(0xFFF4F9EE)

@Composable
fun Historico_denuncias(
    onIrParaHome: () -> Unit
) {
    // Lista com os números das denúncias exatamente como na imagem
    val denuncias = listOf(
        "567768768",
        "567768769",
        "567768769",
        "567768769"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoClaro)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_padrao),
            contentDescription = "fundo da home"

        )
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 60.dp)
                .fillMaxWidth()
                .height(30.dp)
            )

        // Conteúdo Principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botão de voltar superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                contentAlignment = Alignment.TopStart
            ) {
                IconButton(onClick = {
                   //onIrParaHome()
                }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }


            }

            Spacer(modifier = Modifier.height(24.dp))



            // Botão "Minhas denúncias"
            Button(
                onClick = { /* Ação */ },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBotao),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(50.dp)

            ) {
                Text(
                    text = "Minhas denúncias",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título "Histórico"
            Text(
                text = "Histórico",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de cartões das denúncias realizada
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(denuncias) { numero ->
                    CardDenuncia(numeroDenuncia = numero)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}



@Composable
fun CardDenuncia(
    numeroDenuncia: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(65.dp)
    ) {
        // Sombra artificial (efeito de deslocamento preto atrás do card)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(Color.Black.copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp))
        )

        // Card principal branco com borda pontilhada/tracejada
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(16.dp),
                    // Aplica o efeito tracejado/pontilhado na borda
                  //  pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Denúncia realizada\nnúmero $numeroDenuncia",
                color = Color.Black,
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

// Formato customizado para gerar a curvatura côncava do fundo verde
fun CustomWaveShape() = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height * 0.7f)

    // Curva que faz o efeito arredondado subir no centro
    quadraticTo(
        size.width / 2f, size.height,
        0f, size.height * 0.7f
    )
    close()
}
