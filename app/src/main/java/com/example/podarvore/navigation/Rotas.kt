package com.example.podarvore.navigation

import android.net.Uri

internal const val LoginRoute = "login"
internal const val CadastroRoute = "cadastro"
internal const val HomeRoute = "home"
internal const val HistoricosDenunciasRoute = "historico"
internal const val SobreRoute = "sobre"
internal const val UsuarioRoute = "usuario"
internal const val NovaDenunciaRoute = "nova_denuncia"
internal const val DetalhesRoute = "detalhes/{id}"
internal fun DetalhesDenunciaRoute(id: String) = "detalhes/${Uri.encode(id)}"
