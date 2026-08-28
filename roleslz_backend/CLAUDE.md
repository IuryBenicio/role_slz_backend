# CLAUDE.md — SpotRolê Backend

API do ecossistema **SpotRolê** (Spring Boot 3.3 + PostGIS). Antes de mexer no código,
leia o **Guia de Contexto para Agentes de IA** (seção 7 do `README.md`) — ele tem as regras
de domínio de heatmap, presença ativa, notificações e entidades JPA.

## Build & versão do Java (LEIA PRIMEIRO)

- O projeto compila e roda **somente em Java 21**. O `java` padrão da máquina do Iury é mais novo
  (JDK 26), e o Lombok 1.18.32 quebra nele com
  `ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`.
- Rode o Maven sempre com o JDK 21:
  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 21) ./mvnw <goal>
  ```
- O `pom.xml` tem um `maven-enforcer-plugin` (`requireJavaVersion [21,22)`) que falha o build
  cedo com mensagem clara. **Não** "conserte" essa falha subindo a regra, trocando a versão do
  Lombok ou removendo o enforcer — troque o `JAVA_HOME` para o JDK 21.
- O `maven-compiler-plugin` usa `<release>${java.version}</release>`. Não reintroduza
  `<compilerArgs>--add-exports jdk.compiler/...</compilerArgs>` — conflita com `--release`.

## Testes

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./mvnw test` roda tudo.
- `EventoRepositoryTest` (repositório) e `RoleslzBackendApplicationTests` (smoke do contexto
  inteiro) usam **Testcontainers + PostGIS** e precisam do **Docker ligado**. Testes de
  **service** usam Mockito puro.
- Rode o smoke test sempre que mexer em beans/config/segurança — foi ele que pegaria o bug do
  `FirebaseApp`.
- O smoke test usa o perfil `test` (`src/test/resources/application-test.properties`) e um
  service account **fake** em `src/test/resources/firebasekey-test.json`.
- Nomeie classes de teste como `*Test`. O Surefire **ignora silenciosamente** nomes como
  `FooTest2`.

## Heatmap (resumo — detalhes no README §2 e §7)

`GET /evento/heatmap` → `PresencaAtivaService.getHeatmapData` → `PresencaAtivaRepository.findHeatmapData`
(query nativa única, PostGIS). O "peso" vem de `tb_presenca_ativa` (check-in/out por geofence),
**não** de `eventos_confirmacoes`. Mantenha o filtro `e.event_privacity = 'PUBLICO'`.

## Notificações

Todo push passa por `NotificationsService.dispararPushFCM` (via os wrappers de
`UtillsNotification`: `notificarInicioDeEvento`, `notificarAtualizacaoCritica`,
`notificarUsuarioProximoGeofence`, `notificarAvaliacaoPosRole`), que lança `NotifyFail` quando o
FCM falha — não reverta para catch silencioso. Fluxos de negócio críticos (editar/deletar evento,
check-in, schedulers) envolvem a notificação em try/catch e só logam.

- `NotificationsService.limparTokensInvalidos` zera `fcmToken` quando o FCM devolve
  `UNREGISTERED`/`INVALID_ARGUMENT` — não remova.
- `FireBaseConfiguration` expõe `@Bean FirebaseApp` **e** `@Bean FirebaseMessaging`. Não junte num
  `@PostConstruct` sem registrar o `FirebaseApp` (o contexto para de subir).
- `PresencaAtivaController` pega o usuário do JWT (`UserUtils`), nunca de `@RequestAttribute`.
- O canal WebSocket (`SimpMessagingTemplate`) foi removido; hoje só existe FCM.
