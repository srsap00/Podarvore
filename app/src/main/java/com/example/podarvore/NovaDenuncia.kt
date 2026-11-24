package com.example.podarvore

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.podarvore.data.RepositorioDenuncias
import com.example.podarvore.ui.componentes.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.File
import java.util.Locale

/** As três páginas preservam os campos em rememberSaveable até a denúncia ser enviada. */
@Composable
fun NovaDenunciaScreen(onVoltar: () -> Unit, onConcluida: (String) -> Unit) {
    val context = LocalContext.current
    var pagina by rememberSaveable { mutableIntStateOf(1) }
    var fotoString by rememberSaveable { mutableStateOf("") }
    var uriCamera by remember { mutableStateOf<Uri?>(null) }
    var descricao by rememberSaveable { mutableStateOf("") }
    var endereco by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var longitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var area by rememberSaveable { mutableStateOf("") }
    var orgao by rememberSaveable { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }
    var denunciaSalva by remember { mutableStateOf<String?>(null) }
    var escolherFoto by remember { mutableStateOf(false) }
    val uri = fotoString.takeIf { it.isNotBlank() }?.let(Uri::parse)
    val client = remember(context) { LocationServices.getFusedLocationProviderClient(context) }

    fun solicitarCoordenadas() {
        try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { local ->
                    if (local != null) {
                        latitude = local.latitude; longitude = local.longitude
                        endereco = String.format(Locale.US, "%.6f, %.6f", local.latitude, local.longitude)
                        erro = ""
                    } else erro = "Não foi possível obter a localização. Informe o endereço manualmente."
                }.addOnFailureListener { erro = it.localizedMessage ?: "Falha ao buscar localização." }
        } catch (_: SecurityException) {
            erro = "Permita o acesso à localização ou informe o endereço."
        }
    }
    val permissao = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
        if (resultado.values.any { it }) solicitarCoordenadas()
        else erro = "Permissão negada. Você pode informar o endereço manualmente."
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { escolhida ->
        escolhida?.let { fotoString = it.toString(); erro = "" }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { sucesso ->
        if (sucesso) uriCamera?.let { fotoString = it.toString(); erro = "" }
    }
    fun iniciarCamera() {
        try {
            val dir = File(context.cacheDir, "images").apply { mkdirs() }
            val arquivo = File.createTempFile("arvore_", ".jpg", dir)
            val destino = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", arquivo)
            uriCamera = destino
            camera.launch(destino)
        } catch (e: Exception) { erro = "Não foi possível abrir a câmera: ${e.localizedMessage}" }
    }
    fun voltar() { if (!enviando) { if (pagina > 1) { pagina--; erro = "" } else onVoltar() } }
    BackHandler(enabled = pagina > 1 || enviando) { voltar() }

    Column(Modifier.fillMaxSize().background(FundoArvore), horizontalAlignment = Alignment.CenterHorizontally) {
        CabecalhoArvore("Nova denúncia", onVoltar = { voltar() }, altura = 133)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            when (pagina) {
                1 -> {
                    Spacer(Modifier.height(17.dp))
                    CartaoArvore(modifier = Modifier.size(158.dp), onClick = { escolherFoto = true }) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (uri != null) AsyncImage(model = uri, contentDescription = "Foto selecionada",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(9.dp)), contentScale = ContentScale.Crop)
                            else Icon(Icons.Default.AddAPhoto, "Adicionar foto", modifier = Modifier.size(78.dp), tint = Color.LightGray)
                        }
                    }
                    TextButton(onClick = { escolherFoto = true }) { Text(if (uri == null) "Adicionar fotografia" else "Trocar fotografia", color = VerdeArvore) }
                    Spacer(Modifier.height(14.dp))
                    Text("Localização onde a árvore deve ser podada", color = CorTextoArvore,
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    CampoArvore(endereco, { endereco = it; latitude = null; longitude = null }, "Endereço ou localização")
                    TextButton(onClick = {
                        val autorizado = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (autorizado) solicitarCoordenadas()
                        else permissao.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                    }, modifier = Modifier.align(Alignment.End)) {
                        Icon(Icons.Default.LocationOn, null, tint = VerdeArvore)
                        Text("Usar localização atual", color = VerdeArvore)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Descreva o motivo da poda da árvore", color = CorTextoArvore,
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    CampoArvore(descricao, { descricao = it }, "Digite seu texto...", linhaUnica = false, minLinhas = 5)
                    Spacer(Modifier.height(14.dp))
                    BotaoArvore("Próximo: tipo de área  →", modifier = Modifier.fillMaxWidth(), onClick = {
                        erro = when {
                            uri == null -> "Selecione ou capture a fotografia da árvore."
                            endereco.isBlank() -> "Informe onde a árvore está localizada."
                            descricao.trim().length < 10 -> "Descreva o problema com pelo menos 10 caracteres."
                            else -> ""
                        }
                        if (erro.isEmpty()) pagina = 2
                    })
                }
                2 -> {
                    Spacer(Modifier.height(58.dp))
                    Text("Onde está a árvore?", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = CorTextoArvore)
                    Spacer(Modifier.height(22.dp))
                    OpcaoDenuncia("Área urbana", area == "Urbana", icon = { Icon(Icons.Default.Apartment, null, tint = VerdeArvore, modifier = Modifier.size(43.dp)) }) { area = "Urbana" }
                    Spacer(Modifier.height(18.dp))
                    OpcaoDenuncia("Área rural", area == "Rural", icon = { Icon(Icons.Default.Landscape, null, tint = VerdeArvore, modifier = Modifier.size(43.dp)) }) { area = "Rural" }
                    Spacer(Modifier.height(28.dp))
                    BotaoArvore("Próximo: órgão responsável  →", onClick = {
                        erro = if (area.isBlank()) "Selecione uma área para continuar." else ""
                        if (erro.isEmpty()) pagina = 3
                    })
                }
                3 -> {
                    Spacer(Modifier.height(8.dp))
                    Text("Está quase lá!", color = VerdeArvore, fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, fontStyle = FontStyle.Italic)
                    Text("Para finalizar a denúncia, informe qual órgão deverá realizar a poda.",
                        color = CorTextoArvore, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(21.dp))
                    OpcaoDenuncia("Prefeitura", orgao == "Prefeitura", icon = { Icon(Icons.Default.AccountBalance, null, tint = VerdeArvore, modifier = Modifier.size(40.dp)) }) { orgao = "Prefeitura" }
                    Text("Quando o risco é direcionado a ruas, casas, calçadas, comércios e praças.",
                        textAlign = TextAlign.Center, fontSize = 12.sp, color = CorTextoArvore)
                    Spacer(Modifier.height(16.dp))
                    OpcaoDenuncia("Companhia elétrica", orgao == "Companhia elétrica", icon = { Icon(Icons.Default.ElectricBolt, null, tint = VerdeArvore, modifier = Modifier.size(40.dp)) }) { orgao = "Companhia elétrica" }
                    Text("Quando a árvore oferece risco a postes e fiações de iluminação pública.",
                        textAlign = TextAlign.Center, fontSize = 12.sp, color = CorTextoArvore)
                    Spacer(Modifier.height(16.dp))
                    OpcaoDenuncia("Departamento de trânsito", orgao == "Departamento de trânsito", icon = { Icon(Icons.Default.Traffic, null, tint = VerdeArvore, modifier = Modifier.size(40.dp)) }) { orgao = "Departamento de trânsito" }
                    Spacer(Modifier.height(23.dp))
                    BotaoArvore(if (enviando) "Enviando..." else "Enviar denúncia  →", enabled = !enviando,
                        modifier = Modifier.fillMaxWidth(), onClick = {
                            erro = if (orgao.isBlank()) "Selecione o órgão responsável." else ""
                            if (erro.isNotBlank()) return@BotaoArvore
                            val fotoFinal = uri ?: run { erro = "A fotografia não está disponível."; return@BotaoArvore }
                            enviando = true
                            RepositorioDenuncias.enviar(context, fotoFinal, descricao, endereco, latitude, longitude, area, orgao) { id, falha ->
                                enviando = false
                                if (id != null) denunciaSalva = id
                                else erro = falha ?: "Não foi possível enviar a denúncia."
                            }
                        })
                    if (enviando) CircularProgressIndicator(color = VerdeArvore, modifier = Modifier.padding(12.dp))
                }
            }
            if (erro.isNotBlank()) MensagemArvore(erro)
            Spacer(Modifier.height(30.dp))
        }
    }
    if (escolherFoto) {
        AlertDialog(
            onDismissRequest = { escolherFoto = false }, title = { Text("Fotografia da árvore") },
            text = { Text("Escolha de onde deseja obter a fotografia.") },
            confirmButton = { TextButton(onClick = { escolherFoto = false; galeria.launch("image/*") }) { Text("Galeria") } },
            dismissButton = { TextButton(onClick = { escolherFoto = false; iniciarCamera() }) { Text("Câmera") } }
        )
    }
    denunciaSalva?.let { id ->
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Denúncia enviada!") },
            text = { Text("Sua denúncia foi registrada com sucesso. Você já pode acompanhar seu status.") },
            confirmButton = { TextButton(onClick = { onConcluida(id) }) { Text("Ver denúncia") } }
        )
    }
}

@Composable
private fun OpcaoDenuncia(
    rotulo: String, selecionada: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val borda = if (selecionada) VerdeArvore else Color(0xFF999999)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            .border(if (selecionada) 3.dp else 1.dp, borda, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        icon(); Spacer(Modifier.width(12.dp))
        Text(rotulo, fontWeight = if (selecionada) FontWeight.Bold else FontWeight.Medium,
            fontSize = 18.sp, color = CorTextoArvore)
    }
}
