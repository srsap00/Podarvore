package com.example.podarvore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.toRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.podarvore.DetalhesDenunciaScreen
import com.example.podarvore.InformacoesUsuarioScreen
import com.example.podarvore.NovaDenunciaScreen
import com.example.podarvore.SobreNosScreen
import com.example.podarvore.TelaDeCadastro
import com.example.podarvore.TelaDeLogin
import com.example.podarvore.TelaHome
import com.example.podarvore.ui.theme.Historico_denuncias
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val logado = FirebaseAuth.getInstance().currentUser != null

    fun paraHome() {
        nav.navigate(HomeRoute) {
            popUpTo(nav.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
    fun paraLogin() {
        nav.navigate(LoginRoute) {
            popUpTo(nav.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
    fun voltar() { if (!nav.popBackStack()) paraHome() }

    NavHost(navController = nav, startDestination = if (logado) HomeRoute else LoginRoute) {
        composable<LoginRoute> {
            TelaDeLogin(onIrParaCadastro = { nav.navigate(CadastroRoute) }, onLoginSucesso = { paraHome() })
        }
        composable<CadastroRoute> {
            TelaDeCadastro(onIrParaLogin = { voltar() }, onCadastroSucesso = { paraHome() })
        }
        composable<HomeRoute> {
            TelaHome(
                onIrParaDenuncias = { nav.navigate(HistoricosDenunciasRoute) },
                onIrParaSobre = { nav.navigate(SobreRoute) },
                onIrParaPerfil = { nav.navigate(UsuarioRoute) },
                onNovaDenuncia = { nav.navigate(NovaDenunciaRoute) },
                onDetalhes = { nav.navigate(DetalhesDenunciaRoute(it)) }
            )
        }
        composable<HistoricosDenunciasRoute> {
            Historico_denuncias(onIrParaHome = { voltar() }, onDetalhes = { nav.navigate(DetalhesDenunciaRoute(it)) })
        }
        composable<SobreRoute> { SobreNosScreen(onBack = { voltar() }) }
        composable<UsuarioRoute> {
            InformacoesUsuarioScreen(onVoltar = { voltar() }, onLogout = { paraLogin() })
        }
        composable<NovaDenunciaRoute> {
            NovaDenunciaScreen(
                onVoltar = { voltar() },
                onConcluida = { id ->
                    nav.navigate(DetalhesDenunciaRoute(id)) {
                        popUpTo(NovaDenunciaRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<DetalhesDenunciaRoute> { entrada ->
            val argumentos = entrada.toRoute<DetalhesDenunciaRoute>()
            DetalhesDenunciaScreen(denunciaId = argumentos.id, onBack = { voltar() })
        }
    }
}
