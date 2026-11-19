package com.example.podarvore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.podarvore.TelaDeCadastro
import com.example.podarvore.TelaHome
import com.example.podarvore.TelaDeLogin
import com.example.podarvore.ui.theme.Historico_denuncias

//import com.example.podarvore.ui.theme.Minhas_denuncias

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // O startDestination define qual tela abre primeiro ao iniciar o app
    NavHost(navController = navController, startDestination = LoginRoute) {

         //Configuração da Tela de Login
        composable<LoginRoute> {
            TelaDeLogin(
                onIrParaCadastro = {
                    navController.navigate(CadastroRoute)
               },
               onLoginSucesso = {
                     //Navega para a Home e limpa o histórico para o usuário não voltar ao Login pelo botão físico "voltar"
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        //Configuração da Tela de Cadastro
        composable<CadastroRoute> {
            TelaDeCadastro(
                onIrParaLogin = {
                    navController.navigate(LoginRoute)
                },
                onCadastroSucesso = {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            )
        }

 // Configuração da Tela Principal (Home)
        composable<HomeRoute> {
            TelaHome(
                onIrParaDenuncias = {
                    navController.navigate(HistoricosDenunciasRoute) {
                        popUpTo(HistoricosDenunciasRoute) {inclusive = true}
                    }
                }
            )
        }

        composable<HistoricosDenunciasRoute> {
            Historico_denuncias(
                onIrParaHome = {
                navController.navigate(HomeRoute) {
                    popUpTo(HomeRoute) { inclusive = true }
                }
            }

            )
        }
    }
}

