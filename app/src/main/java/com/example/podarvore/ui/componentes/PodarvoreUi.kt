package com.example.podarvore.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val VerdeArvore = Color(0xFF788B38)
val VerdeBotaoArvore = Color(0xFF91A05E)
val FundoArvore = Color(0xFFF8FFF0)
val CorTextoArvore = Color(0xFF454545)

@Composable
fun CabecalhoArvore(titulo: String, onVoltar: (() -> Unit)?, altura: Int = 142) {
    Box(Modifier.fillMaxWidth().height(altura.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val onda = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * .72f)
                quadraticTo(size.width * .50f, size.height * 1.12f, 0f, size.height * .72f)
                close()
            }
            drawPath(onda, VerdeArvore)
            drawLine(Color.White, start = androidx.compose.ui.geometry.Offset(0f, 47.dp.toPx()), end = androidx.compose.ui.geometry.Offset(size.width, 47.dp.toPx()), strokeWidth = 2.dp.toPx())
        }
        if (onVoltar != null) {
            IconButton(onClick = onVoltar, modifier = Modifier.align(Alignment.TopStart).padding(top = 1.dp, start = 4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White, modifier = Modifier.size(29.dp))
            }
        }
        Box(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 56.dp)
                .shadow(3.dp, RoundedCornerShape(11.dp))
                .background(VerdeBotaoArvore, RoundedCornerShape(11.dp))
                .padding(horizontal = 23.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun CartaoArvore(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val base = modifier
        .shadow(4.dp, RoundedCornerShape(11.dp))
        .background(Color.White, RoundedCornerShape(11.dp))
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    Column(base.padding(13.dp), horizontalAlignment = Alignment.CenterHorizontally, content = content)
}

@Composable
fun BotaoArvore(texto: String, onClick: () -> Unit, enabled: Boolean = true, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick, enabled = enabled, modifier = modifier,
        shape = RoundedCornerShape(11.dp),
        colors = ButtonDefaults.buttonColors(containerColor = VerdeArvore, contentColor = Color.White)
    ) { Text(texto, fontWeight = FontWeight.Bold) }
}

@Composable
fun MensagemArvore(texto: String, modifier: Modifier = Modifier) {
    Text(texto, modifier = modifier.fillMaxWidth().padding(16.dp), color = CorTextoArvore, textAlign = TextAlign.Center)
}

@Composable
fun CampoArvore(
    valor: String,
    aoAlterar: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    linhaUnica: Boolean = true,
    minLinhas: Int = 1,
    enabled: Boolean = true,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = valor, onValueChange = aoAlterar, label = { Text(rotulo) },
        modifier = modifier.fillMaxWidth(), singleLine = linhaUnica, minLines = minLinhas,
        enabled = enabled, visualTransformation = visualTransformation, keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdeArvore,
            unfocusedBorderColor = Color(0xFF777777),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
