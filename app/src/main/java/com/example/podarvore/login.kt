package com.example.podarvore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.podarvore.ui.theme.ArtNuvo
import com.example.podarvore.ui.theme.componentes.caixaDeTexto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions



@Composable
fun TelaDeLogin(
    onIrParaCadastro: () -> Unit, onLoginSucesso: () -> Unit
) {
    var campo by remember { mutableStateOf(true) }
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


        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "logo do podarvore",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Para continuar preencha abaixo",
            fontFamily = ArtNuvo,
            fontSize = 27.sp,
            modifier = Modifier. padding(bottom = 60.dp))

        if (campo) {
            caixaDeTexto(
                "Insira seu e-mail", "",true, email,{email = it}, clique = {
                    campo = false
                }
            )


            Spacer(
                modifier = Modifier.height(60.dp)
            )


            Text(
                text = "Ou continue por", fontSize = 23.sp, modifier = Modifier.padding(5.dp)
            )


            OutlinedButton(
                onClick = {},
                border = BorderStroke(3.dp, Color.Gray),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(). padding(start = 50.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "icone google",
                        modifier = Modifier.padding(end = 10.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "Continue com google",
                        fontSize = 19.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }

            OutlinedButton(
                onClick = {},
                border = BorderStroke(3.dp, Color.Gray),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(). padding(start = 50.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.facebook_icon),
                        contentDescription = "icone facebook",
                        modifier = Modifier.padding(end = 10.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "Continue com facebook",
                        fontSize = 19.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        } else {
            caixaDeTexto(
                "Adicionar senha", "Senha:",false, senha,{senha = it}
            )

            Spacer(
                modifier = Modifier.height(60.dp)
            )



            OutlinedButton(onClick = {
                auth.signInWithEmailAndPassword(
                    email.trim(),senha
                )
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val usuario = auth.currentUser
                            if(usuario != null) {
                                val dados = hashMapOf( "email" to usuario.email,
                                    "ultimoLogin" to FieldValue.serverTimestamp())
                                db.collection("usuarios")
                                    .document(usuario.uid)
                                    .set(dados, SetOptions.merge())
                                usuarioLogado = true
                                onLoginSucesso()
                            }
                        }else{
                            campo = true
                        }
                    }
            }, border = BorderStroke(3.dp, Color(0xff3F4B1D)),
                modifier = Modifier.padding(5.dp)

                    .height(50.dp)) {
                Text(text = "Continuar", color = Color(0xff4A2C14))
            }
        }
        Text(
            text = "Não tenho uma conta/cadastrar",
            fontSize = 17.sp, color = Color(0xffB4B4B4),
            modifier = Modifier.clickable {
                onIrParaCadastro()
            }
        )
    }
}

