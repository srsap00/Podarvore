package com.example.podarvore

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.podarvore.navigation.AppNavigation
import com.example.podarvore.ui.componentes.*
import com.example.podarvore.ui.theme.ArtNuvo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    val facebookCallbackManager: com.facebook.CallbackManager = com.facebook.CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppNavigation() }
    }

    @Deprecated("O SDK do Facebook necessita do callback de Activity")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

@Composable
fun TelaDeCadastro(onIrParaLogin: () -> Unit, onCadastroSucesso: () -> Unit) {
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var localizacao by rememberSaveable { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf("") }
    val auth = remember { FirebaseAuth.getInstance() }

    Column(
        modifier = Modifier.fillMaxSize().background(FundoArvore).verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 45.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo(a) ao", fontFamily = ArtNuvo, fontSize = 25.sp)
        Image(painterResource(R.drawable.logo), "Logo PodÁrvore", Modifier.fillMaxWidth().height(105.dp))
        Spacer(Modifier.height(16.dp))
        CampoArvore(nome, { nome = it }, "Nome completo")
        Spacer(Modifier.height(9.dp))
        CampoArvore(email, { email = it }, "E-mail", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(Modifier.height(9.dp))
        CampoArvore(senha, { senha = it }, "Senha (mínimo de 6 caracteres)", visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(9.dp))
        CampoArvore(localizacao, { localizacao = it }, "Cidade/localização (opcional)")
        if (erro.isNotEmpty()) MensagemArvore(erro)
        Spacer(Modifier.height(16.dp))
        BotaoArvore("Cadastrar", enabled = !carregando, modifier = Modifier.fillMaxWidth(), onClick = {
            erro = when {
                nome.trim().length < 2 -> "Informe seu nome completo."
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Informe um e-mail válido."
                senha.length < 6 -> "A senha deve conter pelo menos 6 caracteres."
                else -> ""
            }
            if (erro.isNotEmpty()) return@BotaoArvore
            carregando = true
            auth.createUserWithEmailAndPassword(email.trim(), senha).addOnCompleteListener { tarefa ->
                val usuario = auth.currentUser
                if (!tarefa.isSuccessful || usuario == null) {
                    erro = tarefa.exception?.localizedMessage ?: "Não foi possível cadastrar."
                    carregando = false
                } else {
                    usuario.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(nome.trim()).build())
                    val dados = mapOf(
                        "nome" to nome.trim(), "email" to email.trim(),
                        "localizacao" to localizacao.trim(),
                        "criadoEm" to FieldValue.serverTimestamp(),
                        "ultimoLogin" to FieldValue.serverTimestamp()
                    )
                    FirebaseFirestore.getInstance().collection("usuarios").document(usuario.uid).set(dados)
                        .addOnSuccessListener { carregando = false; usuario.sendEmailVerification(); onCadastroSucesso() }
                        .addOnFailureListener {
                            carregando = false
                            erro = "Conta criada, mas não foi possível salvar o perfil: ${it.localizedMessage}. Tente novamente."
                        }
                }
            }
        })
        if (carregando) CircularProgressIndicator(color = VerdeArvore)
        Spacer(Modifier.height(14.dp))
        Text("Já tenho uma conta / Entrar", color = CorTextoArvore,
            modifier = Modifier.clickable { onIrParaLogin() }.padding(12.dp))
    }
}
