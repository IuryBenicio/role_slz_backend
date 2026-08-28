# SpotRolê - Backend API (Spring Boot + PostGIS)

Este repositório contém a API RESTful do ecossistema **SpotRolê**, desenvolvida em Java com Spring Boot. O backend é responsável pela gestão de usuários, autenticação segura, gestão de eventos com imagens, consultas geoespaciais avançadas para o Mapa de Calor (Heatmap) e tarefas agendadas de manutenção de dados.

---

## 🚀 1. Tecnologias & Arquitetura

- **Linguagem & Framework:** Java 21 (obrigatório — ver seção 6) + Spring Boot 3.3.x
- **Banco de Dados Principal:** PostgreSQL com extensão **PostGIS** (processamento espacial e cálculos geográficos)
- **Cache & Armazenamento Temporário:** **Redis** (gestão de códigos de recuperação de senha e controle de sessões)
- **Segurança:** Spring Security + JWT (JSON Web Tokens)
- **Gestão de Imagens:** Cloudinary API (armazenamento e otimização de imagens)
- **Notificações & E-mail:** Spring Mail (Google SMTP)
- **Controle de Migrações:** Flyway
- **Produtividade:** Lombok

---

## 🛠️ 2. Principais Módulos & Funcionalidades

### 📌 Módulo de Eventos & Consulta Geoespacial (`/evento`)
- **Lista de eventos na área do mapa:** `POST /evento/get_events_in_map_area` recebe a caixa delimitadora (*Bounding Box*) enviada pelo mobile e devolve a lista de eventos públicos visíveis na tela (com distância até o centro), usando `ST_MakeEnvelope`/`ST_Intersects` do PostGIS. **Não é o heatmap** — é uma listagem por evento.
- **Mapa de Calor (Heatmap):** `GET /evento/heatmap?minLat=&minLon=&maxLat=&maxLon=` devolve pontos agregados `{ lat, lng, peso }` (um por spot) prontos para uma camada de heatmap (Google Maps `HeatmapLayer`, Mapbox `heatmap-layer`). O `peso` é a contagem de **check-ins ativos** (`tb_presenca_ativa`) naquele spot, considerando apenas eventos `DURANTE` ou `PRE` a até 90 min de começar, e só eventos `PUBLICO`. A query fica em `PresencaAtivaRepository.findHeatmapData` (native query com `ST_MakeEnvelope`).
- **Presença Ativa (Geofencing em tempo real):** `POST /api/checkin/{eventoId}` e `DELETE /api/checkin/{eventoId}` — disparados pelo SO do celular ao entrar/sair da geofence do evento. O usuário vem do JWT (`UserUtils.getUserLogado()`), não do corpo/atributo da request. Alimenta a tabela `tb_presenca_ativa`, que é a fonte de dados do heatmap (ver `PresencaAtivaService`/`PresencaAtivaController`). No check-in novo, dispara um push *best-effort* de proximidade (`GEOFENCING`) para o próprio usuário — falha de push nunca aborta o check-in.
- **Galeria de Fotos:** Estrutura relacional 1:N (`PhotosEventoEntity` / `evento_galery`) associada a cada evento com carregamento via Cloudinary.
- **Paginação:** Utilização nativa do `Pageable` do Spring Data JPA para prevenir *memory leaks* e otimizar listagens infinitas no feed.
- **Agendadores (Schedulers):** `EventoSchedullers` roda a cada 60s e cobre 4 responsabilidades: (1) notificar 30min/5min antes do evento e transicionar `PRE→DURANTE`; (2) transicionar `DURANTE→ENCERRADO` e convidar quem confirmou presença a avaliar o rolê (push `AVALIACAO_POS_ROLE`, best-effort); (3) limpar `tb_presenca_ativa` do evento encerrado (`presencaLimpa=true`, idempotente) para o heatmap não acumular contagem de eventos já terminados.

### 🔐 Módulo de Autenticação & Senha (`/auth`)
- **JWT Authentication:** Emissão e validação de tokens JWT passados via header `Authorization: Bearer <token>`.
- **Recuperação de Senha com Redis:** Envio de código de 6 dígitos por e-mail com TTL (tempo de vida) gerenciado via Redis. A redefinição valida o e-mail, o código e a nova senha numa única operação atômica.

### 👤 Módulo de Usuários (`/usuario`)
- Consultas otimizadas de perfil de usuário.
- Atualização parcial de perfil (`PATCH`).
- **Registro de token FCM:** `POST /usuario/add_f_token` grava o `fcmToken` do dispositivo no usuário logado (`UserService.addFirebaseToken`).

### 🔔 Módulo de Notificações Push (FCM)
- **Único ponto de saída:** `NotificationsService.dispararPushFCM(evento, título, corpo, tipo, tokensAlvo)` — monta o `MulticastMessage`, injeta `tipoPush`/`eventoId`/`estadoDoEvento` no *data payload* e envia via `FirebaseMessaging`. Lança `NotifyFail` quando o FCM falha.
- **Wrappers de domínio (`UtillsNotification`):** `notificarInicioDeEvento`, `notificarAtualizacaoCritica`, `notificarUsuarioProximoGeofence`, `notificarAvaliacaoPosRole`. Chame sempre por aqui, nunca `dispararPushFCM` direto.
- **Tipos (`TipoNotificacao`):** `ALERTA_TEMPO` (30/5 min antes), `ATUALIZACAO_CRITICA` (edição/exclusão de evento), `GEOFENCING` (proximidade no check-in), `AVALIACAO_POS_ROLE` (convite pós-evento).
- **Limpeza de tokens mortos:** quando o FCM responde `UNREGISTERED`/`INVALID_ARGUMENT` para um token, o `fcmToken` do usuário é zerado (`UserRepository.limparFcmTokens`) para não seguir disparando push para aparelhos que desinstalaram o app.
- **Bean do Firebase:** `FireBaseConfiguration` expõe `FirebaseApp` (carrega o service account de `firebase.credentials.classpath`, default `firebasekey.json`) **e** `FirebaseMessaging`. Sem o `@Bean FirebaseApp` o contexto não sobe.
- **Só existe FCM.** O antigo canal WebSocket (`SimpMessagingTemplate`) foi removido.

---

## 💡 3. Boas Práticas e Padrões de Projeto Aplicados

1. **Evitar N+1 e `LazyInitializationException`:** Uso de *Class-based Projections* (Java Records) e `@Query` JPQL direcionadas para retornos leves em listagens de eventos e marcadores do mapa.
2. **Separação DTO vs Entidade:** Utilização de DTOs específicos para *Requests* e *Responses*, evitando expor diretamente as entidades JPA no Jackson.
3. **Indexação Geoespacial:** Utilização de índices `GIST` no PostgreSQL para as colunas do tipo `Point` do PostGIS.

---

## 🧪 4. Estratégia de Testes

A suíte de testes foi focada estrategicamente nas regras de negócio críticas do domínio (*Core Domain*) e em validações de segurança (Prevenção de IDOR e Autenticação), garantindo a estabilidade das funcionalidades principais (Geofencing e JWT) sem acoplamento desnecessário de testes em código *boilerplate*.

### Cobertura atual

| Classe de teste | Tipo | O que cobre |
| --- | --- | --- |
| `EventoServiceTest` | Unitário (Mockito) | Prevenção de IDOR ao deletar evento; regras de check-in por estado do evento (`DURANTE` / `PRE` + janela de 90 min). |
| `PresencaAtivaServiceTest` | Unitário (Mockito) | Check-in feliz grava em `tb_presenca_ativa`. |
| `UserServiceTest` | Unitário (Mockito) | Regras do domínio de usuário. |
| `NotificationsServiceTest` | Unitário (Mockito) | `dispararPushFCM`: envio com tokens, no-op sem tokens/lista nula, propagação de `NotifyFail` e limpeza de tokens `UNREGISTERED`. |
| `EventoRepositoryTest` | Integração (Testcontainers) | Sobe um `postgis/postgis:15-3.3` real e roda as *queries* nativas de `buscarEventosEmUmaArea` e `findHeatmapData` para pegar erros de sintaxe/parâmetro do Hibernate. |
| `RoleslzBackendApplicationTests` | Smoke (Testcontainers + perfil `test`) | Sobe o `ApplicationContext` inteiro (Firebase, JPA/PostGIS, security, schedulers, websocket) para pegar erros de fiação antes de rodar a app. Usa um service account **fake** em `src/test/resources/firebasekey-test.json`. |

### Como rodar

```bash
# Unitários + integração (precisa do Docker ligado para o EventoRepositoryTest)
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./mvnw test
```

> ⚠️ O Surefire só executa classes cujo nome casa com `*Test` / `*Tests` / `Test*`. Um nome como `EventoRepositoryTest2` é **ignorado silenciosamente** — nunca nomeie testes assim.

---

## ⚙️ 5. Variáveis de Ambiente & Configuração (`application.properties`)

Crie ou ajuste o arquivo `src/main/resources/application.properties` com as seguintes variáveis de ambiente configuradas no seu sistema ou IDE:

```properties
server.port=8080

# PostgreSQL + PostGIS
spring.datasource.url=jdbc:postgresql://localhost:5432/spotrole
spring.datasource.username=SEU_USUARIO_POSTGRES
spring.datasource.password=SUA_SENHA_POSTGRES
spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.postgis.PostgisDialect
spring.jpa.hibernate.ddl-auto=update

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Segurança & JWT
jwt.secret=SUA_CHAVE_SECRETA_JWT_SUPER_SEGURA

# Configuração de E-mail (Google SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seuemail@gmail.com
spring.mail.password=sua_senha_de_app_google
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Cloudinary (Upload de Fotos)
cloudinary.cloud-name=spotrole
cloudinary.api-key=SUA_API_KEY
cloudinary.api-secret=SEU_API_SECRET

💻 6. Como Executar a Aplicação Localmente

Pré-requisitos
Java 21 — obrigatório. O Lombok 1.18.32 (versão fixada) quebra em JDKs mais novos (ex.: 26) com o erro
`java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`.
O `pom.xml` tem um `maven-enforcer-plugin` (`requireJavaVersion [21,22)`) que falha o build cedo,
com mensagem clara, se você estiver em outro JDK.
Se o `java` padrão da sua máquina não for o 21, rode o Maven assim:
`JAVA_HOME=$(/usr/libexec/java_home -v 21) ./mvnw ...`
Docker — necessário para os testes de integração de repositório (Testcontainers sobe um PostGIS).
PostgreSQL rodando com a extensão espacial ativada (CREATE EXTENSION postgis;).
Redis rodando localmente (brew services start redis no macOS M1/M2).
Passos:

1. Clonar o Repositório:

git clone [https://github.com/IuryBenicio/role_slz_backend.git](https://github.com/IuryBenicio/role_slz_backend.git)
cd role_slz_backend

2. Ativar a Extensão PostGIS no Banco de Dados:
No pgAdmin, DBeaver ou psql, dentro do banco de dados spotrole:
CREATE EXTENSION IF NOT EXISTS postgis;

3. Compilar e Executar a Aplicação:
./mvnw clean install
./mvnw spring-boot:run

🤖 7. Guia de Contexto para Agentes de IA (AI Agent Guide)
Aviso para Agentes de IA (LLMs, Copilot, Cursor, Gemini):
Quando estiver alterando ou criando novas funcionalidades neste projeto, siga obrigatoriamente as seguintes diretrizes:
Versão do Java (Build):
O projeto compila e roda SOMENTE em Java 21. NÃO tente "consertar" uma falha do maven-enforcer-plugin (requireJavaVersion [21,22)) subindo a regra, trocando a versão do Lombok ou removendo o enforcer — a solução é apontar o JAVA_HOME para um JDK 21. O compiler-plugin usa <release>${java.version}</release>; não reintroduza <compilerArgs>--add-exports ...</compilerArgs> (conflita com --release).
Testes:
Nomeie classes de teste como *Test (o Surefire ignora silenciosamente nomes como FooTest2). Testes de repositório usam Testcontainers + PostGIS (precisam de Docker); testes de service usam Mockito puro.
Modificações em Entidades JPA:
NÃO utilize @Data em entidades JPA que contenham relacionamentos bi-direcionais (@OneToMany / @ManyToMany). Utilize @Getter, @Setter e @EqualsAndHashCode(of = "id") para evitar StackOverflowError e recursão infinita no Hibernate.
Mantenha as exclusões necessárias de @ToString(exclude = ...) nos relacionamentos.
Consultas Geoespaciais:
Para consultas no mapa, utilize sempre as funções nativas do PostGIS (ST_MakeEnvelope, ST_Distance, ST_DWithin).
Garanta que a rota /evento/get_events_in_map_area se mantém como POST para receber a caixa delimitadora (minLat, maxLat, minLng, maxLng) no corpo do JSON — essa rota retorna LISTA DE EVENTOS, não é o heatmap.
Heatmap (Mapa de Calor):
A rota do heatmap é GET /evento/heatmap (query params minLat, minLon, maxLat, maxLon), servida por PresencaAtivaService.getHeatmapData → PresencaAtivaRepository.findHeatmapData. Existe UMA ÚNICA query de agregação do heatmap — ela vive em PresencaAtivaRepository, não duplique em EventoRepository. Toda alteração nessa query deve manter o filtro AND e.event_privacity = 'PUBLICO' (eventos privados não podem vazar localização/lotação no heatmap público) e os nomes de tabela reais (eventos, spot, tb_presenca_ativa).
O "peso" do heatmap vem de tb_presenca_ativa (check-in/check-out via geofence, ver PresencaAtivaService), não de eventos_confirmacoes (que é "confirmou presença", estático) — não confunda os dois conceitos.
Sempre que o evento for ENCERRADO, o scheduler EventoSchedullers.EventoFinalizadoHeatMap precisa limpar tb_presenca_ativa daquele evento (marcando presencaLimpa=true) — se essa limpeza for removida/quebrada, o heatmap acumula contagem de eventos já terminados.
Gestão de Sessão e Autenticação:
Lembre-se de que a redefinição de senha recorre ao Redis para validação de códigos temporários com tempo de expiração ativado.
Notificações Push (FCM):
Todo envio de push passa por NotificationsService.dispararPushFCM, que lança NotifyFail quando o FCM falha — NÃO reverta isso para um catch silencioso. Use sempre os wrappers de UtillsNotification (notificarInicioDeEvento, notificarAtualizacaoCritica, notificarUsuarioProximoGeofence, notificarAvaliacaoPosRole) em vez de chamar dispararPushFCM diretamente.
Como NotifyFail é lançada, qualquer chamador cujo fluxo principal NÃO pode falhar por causa de um push (ex.: editar evento, editar preço, deletar evento, check-in de presença, transicionar estado do evento no scheduler) deve envolver a chamada de notificação em try/catch e apenas logar — nunca deixe a notificação abortar uma operação de negócio crítica.
NÃO remova a limpeza de tokens mortos em NotificationsService.limparTokensInvalidos (zera fcmToken quando o FCM devolve UNREGISTERED/INVALID_ARGUMENT) — sem ela o backend fica disparando push para sempre em aparelhos que desinstalaram o app.
FireBaseConfiguration DEVE expor tanto o @Bean FirebaseApp quanto o @Bean FirebaseMessaging; se você juntar tudo num @PostConstruct sem registrar o FirebaseApp como bean, o contexto não sobe (foi o bug corrigido).
O antigo envio via WebSocket (SimpMessagingTemplate) foi removido; hoje só existe o canal Firebase (FCM).
Antes de dar por "pronto" qualquer mudança que toque em beans/config, rode o smoke test RoleslzBackendApplicationTests (precisa de Docker).
Projeções e Performance:
Sempre que criar listagens leves (para feed ou mapa), crie Java Records como Projeções em vez de retornar a entidade EventoEntity completa.