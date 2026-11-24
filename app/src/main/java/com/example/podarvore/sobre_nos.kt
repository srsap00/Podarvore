package com.example.podarvore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection

private val VerdePodarvore = Color(0xFF788A38)
private val FundoPodarvore = Color(0xFFF8FFF0)
private val FundoCartao = Color(0xFFE9EBE7).copy(alpha = 0.75f)
private val VerdeBotao = Color(0xFF8F9F5B)

@Preview
@Composable
fun SobreNosScreen(onBack: () -> Unit = {}) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoPodarvore),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HeaderSection(onBack)

        AboutCard(
            modifier = Modifier
                .offset(y = (-178).dp)
                .padding(horizontal = 32.dp)
        )

        Image(
            painter = painterResource(
                id = R.drawable.logo
            ),
            contentDescription = "Logo PodÁrvore",
            modifier = Modifier
                .offset(y = (-165).dp)
                .width(150.dp)
                .height(55.dp)
        )
    }
}
@Composable
private fun HeaderSection(onBack: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .background(
                color = VerdePodarvore,
                shape = HeaderShape()
            )
    ) {

        Text(
            text = "‹",
            fontSize = 48.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp)
                .clickable(onClick = onBack)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 53.dp)
                .width(200.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(VerdeBotao),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Sobre nós",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun AboutCard(
    modifier: Modifier = Modifier
) {

    val texto = """
        Pod'árvore é um aplicativo que possui a iniciativa de possibilitar a população de denunciar árvores aos órgãos necessários e posteriormente para podá-las devidamente, protegendo os civis de perigos proporcionados por árvores não podadas, promovendo o bem em biossistemas rurais e urbanos.

        Seja bem-vindo a este aplicativo e auxilie com sua denúncia para um futuro melhor!
    """.trimIndent()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FundoCartao)
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 19.sp,
            lineHeight = 29.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

private class HeaderShape : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {

        val path = Path().apply {

            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.82f)

            cubicTo(
                size.width * 0.80f,
                size.height * 1.02f,
                size.width * 0.65f,
                size.height,
                size.width * 0.50f,
                size.height
            )

            cubicTo(
                size.width * 0.35f,
                size.height,
                size.width * 0.20f,
                size.height * 1.02f,
                0f,
                size.height * 0.82f
            )

            close()
        }

        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

