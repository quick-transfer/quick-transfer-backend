# Quick Transfer API

API REST para gestão de estudantes, turmas, cursos, vagas e entrevistas. O projeto usa Java 17, Spring Boot 4, PostgreSQL 16, Flyway, JWT RSA, Spring Security, Actuator e SMTP.

## Requisitos

- Java 17 para execução local;
- Docker com Compose para a execução recomendada;
- um par RSA de pelo menos 2048 bits;
- credenciais de um servidor SMTP.

## Configuração

Crie um arquivo `.env` na raiz. Ele é ignorado pelo Git e pelo contexto de build do Docker.

~~~dotenv
DATABASE_USER=quick_transfer
DATABASE_PASSWORD=troque-por-uma-senha-forte
ALLOWED_ORIGINS=https://app.exemplo.com
FRONTEND_URL=https://app.exemplo.com

PUBLIC_KEY=-----BEGIN PUBLIC KEY-----\n...\n-----END PUBLIC KEY-----
PRIVATE_KEY=-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----

MAIL_HOST=smtp.exemplo.com
MAIL_PORT=587
MAIL_USERNAME=notificacoes@exemplo.com
MAIL_PASSWORD=troque-por-um-segredo
MAIL_FROM=notificacoes@exemplo.com

COOKIE_SECURE=true
~~~

Para gerar um par de chaves novo:

~~~bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:3072 -out private.pem
openssl pkey -in private.pem -pubout -out public.pem
~~~

Nunca versione as chaves ou o `.env`. Chaves e senhas expostas anteriormente devem ser rotacionadas no provedor antes da implantação.

### Primeiro administrador

Não há conta padrão. Em um banco vazio, habilite o bootstrap somente na primeira inicialização:

~~~dotenv
BOOTSTRAP_ADMIN_ENABLED=true
BOOTSTRAP_ADMIN_NAME=Administrador
BOOTSTRAP_ADMIN_USERNAME=admin
BOOTSTRAP_ADMIN_EMAIL=admin@exemplo.com
BOOTSTRAP_ADMIN_PASSWORD=uma-senha-forte-com-14-ou-mais
~~~

Após a criação, remova essas variáveis e reinicie com `BOOTSTRAP_ADMIN_ENABLED=false`. O bootstrap falha deliberadamente se a tabela de usuários não estiver vazia.

## Execução

Com Docker:

~~~bash
docker compose up --build -d
docker compose ps
~~~

Por padrão, a API fica disponível apenas na interface local em `http://127.0.0.1:8081/api`. Em produção, publique-a atrás de um proxy HTTPS. O PostgreSQL não é exposto pelo Compose.

Localmente:

~~~bash
./mvnw clean verify
./mvnw spring-boot:run
~~~

No Windows, use `mvnw.cmd`.

## Banco de dados

O Flyway aplica as migrations de `src/main/resources/db/migration`. O Hibernate usa `ddl-auto=validate`: ele valida o mapeamento, mas não altera o schema.

Para adotar o Flyway uma única vez em um banco legado que ainda não possui `flyway_schema_history`, execute a primeira inicialização com `FLYWAY_BASELINE_ON_MIGRATE=true`. Depois que as migrations forem aplicadas, volte a variável para `false`. Bancos novos devem mantê-la em `false` desde o início.

Não use `flyway clean` em produção. Faça backup e teste restauração antes de cada implantação que contenha migration.

## Autenticação

Os perfis autenticáveis são `ADMIN`, `MANAGER` e `COORDINATOR`. Estudantes são entidades de domínio e não possuem login.

- `POST /api/auth/login`: autentica pelo `username` e cria o cookie HttpOnly `JWT`;
- `POST /api/auth/first-access`: troca a senha temporária;
- `POST /api/auth/logout`: revoga os tokens atuais e remove o cookie;
- `POST /api/auth/password-reset`: troca a senha do próprio usuário após validar a senha atual;
- `POST /api/auth/change-password`: alternativa autenticada para troca da própria senha;
- `GET /api/auth/csrf`: obtém o token CSRF.

Para operações mutáveis autenticadas por cookie, primeiro consulte `/api/auth/csrf` e envie o valor retornado no cabeçalho `X-XSRF-TOKEN`. O cliente também deve enviar cookies (`credentials: include`). Tokens antigos deixam de ser válidos após logout ou troca de senha.

## Recursos de domínio

- `POST /api/student/create/multiple` importa estudantes a partir de um arquivo JSON;
- `/api/vacancy-skill` gerencia competências exigidas pelas vagas;
- listagens e pesquisas aceitam paginação pelos parâmetros do Spring Data.

## Observabilidade e documentação

- Health check público: `GET /api/actuator/health`;
- Swagger UI: `/api/swagger-ui/index.html`;
- OpenAPI: `/api/v3/api-docs`;
- métricas e documentação exigem autenticação HTTP Basic de um administrador.

Cada resposta inclui `X-Correlation-ID`; o mesmo identificador aparece nos logs. Logs são enviados para stdout, apropriados para coleta pelo runtime do container.

## Testes e CI

~~~bash
./mvnw clean verify
docker compose config --quiet
~~~

A suíte contém testes unitários, teste de inicialização completa do contexto e persistência em H2, validação de autorização por método e testes de emissão/revogação de JWT. O workflow em `.github/workflows/ci.yml` executa o Maven e constrói a imagem Docker a cada push ou pull request.

## Checklist de implantação

1. Rotacione credenciais e gere chaves RSA exclusivas do ambiente.
2. Configure HTTPS no proxy e mantenha `COOKIE_SECURE=true`.
3. Restrinja `ALLOWED_ORIGINS` ao frontend de produção.
4. Faça backup do PostgreSQL e valide a restauração.
5. Execute `./mvnw clean verify` e construa a imagem por digest.
6. Suba uma instância, aguarde o health check e confirme as migrations.
7. Valide login, CSRF, autorização, envio SMTP e lembrete de entrevista.
8. Desabilite e remova as variáveis de bootstrap.

## Limitação operacional conhecida

O envio SMTP e a confirmação no banco não formam uma transação distribuída. O scheduler usa bloqueio pessimista para impedir processamento simultâneo por réplicas e registra o envio, mas uma falha entre a aceitação do e-mail pelo SMTP e o commit pode causar uma nova tentativa. Para garantia estrita de entrega, evolua o fluxo para outbox transacional com provedor idempotente.
