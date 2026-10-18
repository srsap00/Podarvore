package com.example.podarvore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.podarvore.navigation.AppNavigation
import com.example.podarvore.ui.theme.ArtNuvo
import com.example.podarvore.ui.theme.componentes.caixaDeTexto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           AppNavigation()

        }
    }
}



@Composable
fun TelaDeCadastro(onIrParaLogin: () -> Unit, onCadastroSucesso: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    var usuarioLogado by remember { mutableStateOf(auth.currentUser != null) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Bem vindo(a) ao",
            fontWeight = FontWeight.Medium,
            fontSize = 25.sp,
            fontFamily = ArtNuvo
        )

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "logo do podarvore",
            modifier = Modifier.padding(bottom = 20.dp)
        )

        caixaDeTexto("Nome", "Nome do Usuário:",false,nome, {nome = it})
        caixaDeTexto("Insira seu e-mail", "Email do Usuário:",false, email, {email = it})
        caixaDeTexto("Senha", "Adicionar senha:",false, senha, {senha = it})

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(onClick = {
            auth.createUserWithEmailAndPassword(
                email.trim(),senha
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val usuario = auth.currentUser
                        if (usuario != null) {
                            val dados = hashMapOf(
                                "email" to usuario.email,
                                "ultimoLogin" to FieldValue.serverTimestamp()
                            )
                            db.collection("usuarios")
                                .document(usuario.uid)
                                .set(dados, SetOptions.merge())
                            usuarioLogado = true
                            onCadastroSucesso()
                        }
                    }
                }
        }, border = BorderStroke(3.dp, Color(0xff3F4B1D)),
            modifier = Modifier.padding(5.dp)) {
            Text(text = "Cadastrar", color = Color(0xff4A2C14))
        }

        Text(
            text = "Já tenho uma conta/entrar",
            fontSize = 17.sp, color = Color(0xffB4B4B4),
            modifier = Modifier.clickable {
                onIrParaLogin()
            }
        )
    }
}


@Preview
@Composable
fun TelaHome(
    //onDeslogar: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize() .background(color = Color.White)){
        Image(
            painter = painterResource(id = R.drawable.background_home),
            contentDescription = "fundo da home"

        )
    }
    HorizontalDivider(
        modifier = Modifier
            .padding(top = 60.dp)
            .fillMaxWidth()
    )

    Column(        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top, modifier = Modifier .padding(top = 18.dp, start = 143.dp)) {
        Image(painter = painterResource(id = R.drawable.logo_cinza),
            contentDescription = "logo cinza", modifier = Modifier.width(100.dp))

    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 90.dp)
    ) {
        item {
            Button(onClick = { /* Ação do primeiro botão */ },
                border = BorderStroke(3.dp, Color.Gray),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(15.dp)) {
                Text(text = "teste 1", fontSize = 24.sp)
            }
        }
        item {
            Button(onClick = { /* Ação do segundo botão */ }) {
                Text(text = "teste 2", fontSize = 24.sp)
            }
        }
        item {
            Button(onClick = { /* Ação do terceiro botão */ }) {
                Text(text = "teste 3", fontSize = 24.sp)
            }
        }

    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(250.dp)

                .border(
                color = Color.Black,
                width = 1.dp, shape = RoundedCornerShape(8.dp)
            )
                .padding(start = 10.dp, top = 3.dp, end = 10.dp, bottom = 3.dp)
                .clip(shape = RoundedCornerShape(8.dp))

        )
        {
            Image(
                painter = painterResource(id = R.drawable.arvore_exemplo1),
                contentDescription = "cartão de denúncia", modifier = Modifier.size(100.dp)
                    .widthIn(max = 5.dp)
                    .heightIn(max = 5.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = "Denúncia realizada \n número 5146464", textAlign = TextAlign.Center)
        }

        Spacer(Modifier .height(30.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(250.dp)
                .height(IntrinsicSize.Min)
                .border(
                    color = Color.Black, width = 1.dp, shape = RoundedCornerShape(8.dp)
                )
                .padding(
                    start = 10.dp, top = 3.dp, end = 10.dp, bottom = 3.dp
                )
        )

        {
            Image(
                painter = painterResource(id = R.drawable.arvore_exemplo2),
                contentDescription = "cartão de denúncia",
                modifier = Modifier.size(100.dp))
            Text(text = "Denúncia realizada \n número 51464653",
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier .height(30.dp))

       Image(painter = painterResource(id = R.drawable.button_add),
           contentDescription = "botão de adicionar",
           modifier = Modifier.size(120.dp).padding(bottom = 50.dp)
       )

    }
}

