package com.example.podarvore

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.podarvore.ui.componentes.*
import com.example.podarvore.ui.theme.ArtNuvo
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

@Composable
fun TelaDeLogin(onIrParaCadastro: () -> Unit, onLoginSucesso: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val atividade = context as? MainActivity
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }
    var recuperacao by remember { mutableStateOf(false) }

    fun aposAutenticar() {
        auth.currentUser?.let { usuario ->
            val dados = mapOf("email" to (usuario.email ?: ""), "ultimoLogin" to FieldValue.serverTimestamp())
            FirebaseFirestore.getInstance().collection("usuarios").document(usuario.uid).set(dados, SetOptions.merge())
        }
        carregando = false
        onLoginSucesso()
    }

    DisposableEffect(atividade) {
        if (atividade != null && context.getString(R.string.facebook_app_id) != "0") {
            LoginManager.getInstance().registerCallback(atividade.facebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    carregando = true
                    auth.signInWithCredential(FacebookAuthProvider.getCredential(result.accessToken.token))
                        .addOnCompleteListener { tarefa ->
                            if (tarefa.isSuccessful) aposAutenticar()
                            else { carregando = false; erro = tarefa.exception?.localizedMessage ?: "Falha no login com Facebook." }
                        }
                }
                override fun onCancel() { carregando = false }
                override fun onError(error: FacebookException) { carregando = false; erro = error.localizedMessage ?: "Falha no Facebook." }
            })
        }
        onDispose { if (atividade != null && context.getString(R.string.facebook_app_id) != "0") LoginManager.getInstance().unregisterCallback(atividade.facebookCallbackManager) }
    }

    Column(
        Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())
            .padding(horizontal = 23.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(R.drawable.logo), "Logo PodÁrvore", Modifier.fillMaxWidth().height(90.dp))
        Text("Para continuar preencha abaixo", fontFamily = ArtNuvo, fontSize = 23.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        CampoArvore(email, { email = it }, "Insira seu e-mail", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(Modifier.height(10.dp))
        CampoArvore(senha, { senha = it }, "Senha", visualTransformation = PasswordVisualTransformation())
        TextButton(onClick = { recuperacao = true }, modifier = Modifier.align(Alignment.End)) {
            Text("Esqueci minha senha", color = VerdeArvore)
        }
        if (erro.isNotBlank()) MensagemArvore(erro)
        BotaoArvore("Entrar", modifier = Modifier.fillMaxWidth(), enabled = !carregando, onClick = {
            erro = when {
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Informe um e-mail válido."
                senha.isBlank() -> "Informe sua senha."
                else -> ""
            }
            if (erro.isNotEmpty()) return@BotaoArvore
            carregando = true
            auth.signInWithEmailAndPassword(email.trim(), senha).addOnCompleteListener { tarefa ->
                if (tarefa.isSuccessful) aposAutenticar()
                else { carregando = false; erro = tarefa.exception?.localizedMessage ?: "E-mail ou senha inválidos." }
            }
        })
        Spacer(Modifier.height(27.dp))
        Text("────────  Ou continue por  ────────", color = CorTextoArvore, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                if (resId == 0) {
                    erro = "Configure o provedor Google e atualize o google-services.json no Firebase."
                } else {
                    val clientId = context.getString(resId)
                    carregando = true
                    scope.launch {
                        try {
                            val opcao = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(clientId).build()
                            val pedido = GetCredentialRequest.Builder().addCredentialOption(opcao).build()
                            val credencial = CredentialManager.create(context).getCredential(context, pedido).credential
                            if (credencial is CustomCredential && credencial.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val token = GoogleIdTokenCredential.createFrom(credencial.data).idToken
                                auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null))
                                    .addOnCompleteListener { t ->
                                        if (t.isSuccessful) aposAutenticar()
                                        else { carregando = false; erro = t.exception?.localizedMessage ?: "Erro ao entrar com Google." }
                                    }
                            } else { carregando = false; erro = "Credencial Google inválida." }
                        } catch (e: Exception) {
                            carregando = false
                            erro = e.localizedMessage ?: "Login com Google cancelado ou indisponível."
                        }
                    }
                }
            }, enabled = !carregando, modifier = Modifier.fillMaxWidth().height(51.dp)
        ) {
            Icon(painterResource(R.drawable.google_icon), null, tint = Color.Unspecified)
            Spacer(Modifier.width(10.dp)); Text("Continue com Google", color = Color.Black)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                if (context.getString(R.string.facebook_app_id) == "0" || atividade == null) {
                    erro = "Configure o aplicativo Facebook no arquivo strings.xml e no Firebase Console."
                } else {
                    carregando = true
                    LoginManager.getInstance().logInWithReadPermissions(atividade, listOf("public_profile", "email"))
                }
            }, enabled = !carregando, modifier = Modifier.fillMaxWidth().height(51.dp)
        ) {
            Icon(painterResource(R.drawable.facebook_icon), null, tint = Color.Unspecified)
            Spacer(Modifier.width(10.dp)); Text("Continue com Facebook", color = Color.Black)
        }
        if (carregando) { Spacer(Modifier.height(15.dp)); CircularProgressIndicator(color = VerdeArvore) }
        Spacer(Modifier.height(22.dp))
        Text("Não tenho uma conta / Cadastrar", color = CorTextoArvore,
            modifier = Modifier.clickable { onIrParaCadastro() }.padding(10.dp))
    }
    if (recuperacao) {
        AlertDialog(
            onDismissRequest = { recuperacao = false },
            title = { Text("Recuperação de senha") },
            text = { Column {
                Text("Enviaremos um link de redefinição para seu e-mail.")
                Spacer(Modifier.height(8.dp))
                CampoArvore(email, { email = it }, "Seu e-mail")
            } },
            confirmButton = { TextButton(onClick = {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    erro = "Informe um e-mail válido."
                    recuperacao = false
                } else {
                    auth.sendPasswordResetEmail(email.trim()).addOnCompleteListener { tarefa ->
                        erro = if (tarefa.isSuccessful) "Se o e-mail estiver cadastrado, receberá instruções de recuperação."
                        else tarefa.exception?.localizedMessage ?: "Não foi possível enviar o e-mail."
                    }
                    recuperacao = false
                }
            }) { Text("Enviar") } },
            dismissButton = { TextButton(onClick = { recuperacao = false }) { Text("Cancelar") } }
        )
    }
}
