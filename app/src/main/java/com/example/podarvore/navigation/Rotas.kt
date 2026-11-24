package com.example.podarvore.navigation

import kotlinx.serialization.Serializable

@Serializable object LoginRoute
@Serializable object CadastroRoute
@Serializable object HomeRoute
@Serializable object HistoricosDenunciasRoute
@Serializable object SobreRoute
@Serializable object UsuarioRoute
@Serializable object NovaDenunciaRoute
@Serializable data class DetalhesDenunciaRoute(val id: String)
