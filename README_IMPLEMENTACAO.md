# PodÁrvore — implementação do TCC

Este projeto mantém o pacote original `com.example.podarvore`, as imagens/fontes de `res` e as telas Android nativas em Kotlin + Jetpack Compose. Nenhuma denúncia de demonstração é criada pelo aplicativo.

## Telas e fluxos implementados

1. **Login**: e-mail/senha, validação, recuperação de senha, Google/Facebook (dependem de configuração externa), acesso ao cadastro.
2. **Cadastro**: nome, e-mail, senha, localização opcional; Firebase Auth + coleção `usuarios`, `criadoEm`, `ultimoLogin` e verificação por e-mail (envio não obrigatório para entrar).
3. **Home**: boas-vindas, três botões em `LazyRow`, duas denúncias recentes reais, botão de nova denúncia, acesso aos detalhes.
4. **Histórico**: todas as denúncias do usuário, ordenadas por data, status, local, data e identificador; estado vazio verdadeiro.
5. **Detalhes**: leitura da denúncia selecionada via ID, foto, local, descrição, data, área, órgão, status, resolução/andamento; abre aplicativo de mapas.
6. **Nova denúncia**: três páginas independentes na interface com dados preservados no fluxo; foto por galeria/câmera, localização atual ou manual, área, órgão, envio de foto ao Storage e dos metadados ao Firestore.
7. **Informações do usuário**: nome, e-mail, localização, data de criação, edição, recuperação de senha, logout.
8. **Sobre nós**: mantém tela original e inclui botão Voltar funcional.

## Estrutura Firestore

`usuarios/{uid}`:
- `nome`: String
- `email`: String
- `localizacao`: String
- `criadoEm`: Timestamp (novos cadastros)
- `ultimoLogin`: Timestamp
- `role`: String, **opcional**, somente atribuído manualmente por administrador confiável

`denuncias/{documentId}`:
- `usuarioId`: uid do Firebase Auth
- `numero`: identificador legível, derivado do ID único
- `descricao`: String
- `localizacao`: endereço digitado ou coordenadas
- `latitude`, `longitude`: Double opcionais (quando GPS usado)
- `area`: `Urbana` ou `Rural`
- `orgaoResponsavel`: `Prefeitura`, `Companhia elétrica` ou `Departamento de trânsito`
- `fotoUrl`: URL para exibir a fotografia enviada
- `fotoCaminho`: caminho original do objeto no Storage
- `status`: começa com `Pendente`
- `criadaEm`, `atualizadaEm`: Timestamp do servidor
- `resolucao`, `andamento`: opcionais, preenchidos por administração, caso existam

Armazenamento de fotos: `denuncias/{uid}/{documentId}` no Firebase Storage.

O app usa listeners em tempo real do Firestore. Para evitar necessidade de índice composto adicional, carrega apenas denúncias do UID autenticado e ordena por data no cliente. Para muitos milhares de registros, pode ser útil um índice e paginação futuramente.

## Configuração externa obrigatória antes de testar

1. No **Firebase Console** do projeto vinculado ao `app/google-services.json`, ative **Authentication > E-mail/senha**, **Cloud Firestore** e **Cloud Storage** e confira a configuração de cobrança e região do Storage, quando aplicável.
2. **Implante as regras** fornecidas nos arquivos `firestore.rules` e `storage.rules` usando Firebase CLI (`firebase deploy --only firestore:rules,storage`) ou cole-as nos consoles respectivos. `firebase.json` está incluído.
3. **Google Login:** ative o provedor Google no Authentication. Adicione ao app no Firebase as impressões digitais SHA-1/SHA-256 das chaves de assinatura utilizadas e **baixe novamente `google-services.json`**. O arquivo original não possui `oauth_client`, portanto não fornece `default_web_client_id`; até atualizar, o app mostrará uma instrução clara ao tocar no botão Google.
4. **Facebook Login:** crie um app no Meta for Developers, habilite Facebook Login e vincule o provedor no Firebase Authentication. Preencha `facebook_app_id` e `facebook_client_token` em `app/src/main/res/values/strings.xml` e configure chave hash e URI de redirecionamento OAuth exigidos pela Meta/Firebase. Antes disso o botão mostra uma mensagem de configuração pendente.
5. Acessos de administração a status e resoluções **não** foram colocados no aplicativo do usuário: o arquivo de regras permite atualização dessas informações por contas com `usuarios/{uid}.role = 'admin'`, concedido somente por um administrador de confiança via Console/Admin SDK. A própria pessoa usuária não pode se promover a admin.
6. A visualização de localização abre um aplicativo de mapas instalado ou o navegador, e não exige uma chave da Google Maps API.

## Como executar / validar

1. Abra `Podarvore-main` no Android Studio, sincronize o Gradle, selecione um emulador/dispositivo com Android API 24+ e conectividade.
2. Crie uma conta com e-mail/senha, confirme que o documento `usuarios/{uid}` aparece.
3. Entre, abra o perfil, edite nome/localização e confira se os dados são atualizados no Firestore.
4. Confira o estado vazio na Home e no Histórico sem registros.
5. Crie uma denúncia com foto, localização e descrição. Avance para área, selecione o órgão, envie. Confira foto no Storage e documento em `denuncias` com `usuarioId` correto.
6. Abra a denúncia pela Home e Histórico, verifique data, foto, status e dados persistidos.
7. Saia e entre com outra conta: confirme que a segunda conta **não** pode listar ou abrir denúncias da primeira.
8. Teste recuperar senha. Depois de configurar OAuth, teste Google e Facebook em dispositivo.
9. Opcional: atualize a denúncia via conta admin, definindo `status`, `resolucao`, `andamento` e `atualizadaEm`; os listeners devem atualizar a tela.

## Segurança e limitações

- Nunca deixe o banco de produção em modo de teste. As regras de acesso por UID precisam ser aplicadas antes de publicar.
- URLs de download de imagens do Firebase Storage são URLs de acesso por token: não compartilhe essas URLs publicamente. As regras do Storage protegem acessos via SDK, mas um link com token pode ser aberto por terceiros que o possuam. Para armazenamento privado mais estrito, carregar imagens pelo SDK autenticado usando `fotoCaminho`, sem URL pública.
- Novas denúncias criam status `Pendente`, mas não há integração de envio automático para prefeituras/concessionárias: a seleção registra **a indicação do órgão** no banco, não envia uma solicitação externa a ele.
- A disponibilidade de `Google`/`Facebook` depende de cadastro OAuth, segredos e impressão digital no console, não pode ser concluída usando apenas o ZIP.
- **Build de APK não foi executado neste ambiente** por falta de Android SDK/Gradle provisionados. É necessária sincronização e build no Android Studio para comprovar a compilação e testes reais com o projeto Firebase.

## Arquivos alterados / adicionados

- Modificados: `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/res/values/strings.xml`, `app/src/main/java/com/example/podarvore/{cadastrar.kt,login.kt,Status_denuncia.kt,sobre_nos.kt}`, `app/src/main/java/com/example/podarvore/navigation/{Rotas.kt,AppNavigation.kt}`, `app/src/main/java/com/example/podarvore/ui/theme/Minhas_denuncias.kt`.
- Adicionados: `app/src/main/java/com/example/podarvore/{Home.kt,InformacoesUsuario.kt,NovaDenuncia.kt}`, `app/src/main/java/com/example/podarvore/{data/Denuncia.kt,ui/componentes/PodarvoreUi.kt}`, `app/src/main/res/xml/file_paths.xml`, `firestore.rules`, `storage.rules`, `firebase.json`, `README_IMPLEMENTACAO.md`.
