package com.example.podarvore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.podarvore.TelaDeCadastro
import com.example.podarvore.TelaHome
import com.example.podarvore.TelaDeLogin

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // O startDestination define qual tela abre primeiro ao iniciar o app
    NavHost(navController = navController, startDestination = LoginRoute) {

        // Configuração da Tela de Login
        composable<LoginRoute> {
            TelaDeLogin(
                onIrParaCadastro = {
                    navController.navigate(CadastroRoute)
                },
                onLoginSucesso = {
                    // Navega para a Home e limpa o histórico para o usuário não voltar ao Login pelo botão físico "voltar"
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        // Configuração da Tela de Cadastro
        composable<CadastroRoute> {
            TelaDeCadastro(
                onIrParaLogin = {
                    navController.navigate(LoginRoute)
                },
                onCadastroSucesso = {
                    navController.navigate(HomeRoute) {
                        popUpTo(CadastroRoute) { inclusive = true }
                    }
                }
            )
        }

        // Configuração da Tela Principal (Home)
        composable<HomeRoute> {
            TelaHome(
                onDeslogar = {
                    navController.navigate(LoginRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}