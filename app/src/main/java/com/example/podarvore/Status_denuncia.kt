package com.example.podarvore
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
// CORES DO PODÁRVORE

private val VerdePodarvore = Color(0xFF788B38)
private val FundoPodarvore = Color(0xFFF8FFF0)
private val VerdeClaro = Color(0xFFE5EBCF)
private val CinzaBorda = Color(0xFF777777)

// TELA PRINCIPAL
@Preview
@Composable
fun DetalhesDenunciaScreen(
   onBack: () -> Unit = {},
    onVisualizarLocalizacao: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoPodarvore)
            .verticalScroll(scrollState)
    ) {

        // Cabeçalho e imagem da árvore
        HeaderSection(
            onBack = onBack
        )

        // Número da denúncia
        Text(
            text = "Número 567768768",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 26.dp),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Black
        )

        // Localização
        LocationSection(
            onVisualizar = onVisualizarLocalizacao
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Descrição do usuário
        DescriptionSection()

        Spacer(modifier = Modifier.height(12.dp))

        // Resolução do problema
        ResolutionSection()

        Spacer(modifier = Modifier.height(20.dp))

        BottomButton(
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// CABEÇALHO

@Composable
fun HeaderSection(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(285.dp)
    ) {

        // Fundo verde com borda inferior curva
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.72f)

                cubicTo(
                    size.width * 0.78f,
                    size.height * 0.95f,
                    size.width * 0.25f,
                    size.height * 0.95f,
                    0f,
                    size.height * 0.68f
                )

                close()
            }

            drawPath(
                path = path,
                color = VerdePodarvore
            )
        }

        // Botão voltar
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 3.dp, top = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White,
                modifier = Modifier.size(27.dp)
            )
        }

        // Status da denúncia
        Button(
            onClick = {},
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 43.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeClaro,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Status da denúncia",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        // Imagem ilustrativa da árvore
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .size(185.dp)
                .shadow(5.dp, CircleShape)
                .clip(CircleShape)
                .background(VerdePodarvore),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Forest,
                contentDescription = "Imagem ilustrativa da árvore",
                tint = Color.White,
                modifier = Modifier.size(85.dp)
            )
        }
    }
}

// LOCALIZAÇÃO

@Composable
fun LocationSection(
    onVisualizar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {

        // Mapa ilustrativo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFE5E9E5))
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Ruas do mapa
                val roads = listOf(
                    Path().apply {
                        moveTo(0f, size.height * 0.2f)
                        lineTo(size.width * 0.3f, size.height * 0.5f)
                        lineTo(size.width * 0.6f, size.height * 0.3f)
                        lineTo(size.width, size.height * 0.7f)
                    },
                    Path().apply {
                        moveTo(size.width * 0.1f, 0f)
                        lineTo(size.width * 0.3f, size.height)
                    },
                    Path().apply {
                        moveTo(size.width * 0.8f, 0f)
                        lineTo(size.width * 0.6f, size.height)
                    },
                    Path().apply {
                        moveTo(0f, size.height * 0.8f)
                        lineTo(size.width, size.height * 0.2f)
                    }
                )

                roads.forEach { road ->
                    drawPath(
                        path = road,
                        color = Color(0xFFB9C7D0),
                        style = Stroke(width = 5f)
                    )
                }

                // Marcador ilustrativo
                drawCircle(
                    color = Color(0xFFDD6B58),
                    radius = 5f,
                    center = Offset(
                        size.width * 0.55f,
                        size.height * 0.45f
                    )
                )
            }
        }

        // Botão de localização
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .offset(y = (-1).dp)
                .shadow(3.dp, RoundedCornerShape(7.dp))
                .clip(RoundedCornerShape(7.dp))
                .background(Color.White)
                .border(
                    1.dp,
                    CinzaBorda,
                    RoundedCornerShape(7.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "LOCALIZAÇÃO",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                fontSize = 9.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onVisualizar,
                modifier = Modifier
                    .weight(1.7f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Visualizar",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// DESCRIÇÃO DO USUÁRIO

@Composable
fun DescriptionSection() {

    var descricao by remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        value = descricao,

        onValueChange = { novoTexto ->
            descricao = novoTexto
        },

        placeholder = {
            Text(
                text = "Descrição do usuário...",
                color = Color.LightGray,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic
            )
        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(220.dp),

        shape = RoundedCornerShape(8.dp),

        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = CinzaBorda,
            focusedBorderColor = VerdePodarvore,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}
// RESOLUÇÃO DO PROBLEMA

@Composable
fun ResolutionSection() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 9.dp)
    ) {

        Text(
            text = "Resolução do problema:",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color.DarkGray,
            modifier = Modifier.padding(
                start = 7.dp,
                bottom = 6.dp
            )
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                CinzaBorda
            )
        ) {

            Text(
                text = """
                   Aguarde a resolução...
                """.trimIndent(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 13.dp,
                        vertical = 34.dp
                    ),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )
        }
    }
}

@Composable
fun BottomButton(
    onBack: () -> Unit
) {
    Image(
        painter = painterResource(
            id = R.drawable.logo
        ),
        contentDescription = "Voltar para o início",
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable {
                onBack()
            },
        contentScale = ContentScale.Fit
    )
}