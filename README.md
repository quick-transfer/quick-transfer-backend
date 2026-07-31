# Quick Transfer - Backend

## 👥 Integrantes da Equipe
- **Eduardo Lino**
- **Guilherme Dogde**
- **Bruno Athanazio**
- **Vinicius Derreti**
- **Matheus de Borba da Silva**

---

## 📌 Descrição do Problema
Em processos de transferência e alocação de estudantes/aprendizes em vagas internas ou programas de formação na empresa, a gestão manual por planilhas ou sistemas descentralizados acarreta falhas de comunicação, morosidade na triagem de candidatos, dificuldade no agendamento e acompanhamento de entrevistas, além da falta de rastreabilidade do progresso de cada participante.

---

## 🎯 Objetivo da Solução
O **Quick Transfer** é uma plataforma centralizada projetada para automatizar e otimizar o fluxo de transferência, triagem, alocação e gerenciamento de estudantes e turmas. A API backend provê regras de negócio consistentes para cadastro de usuários (Estudantes, Gestores, Coordenadores, Administradores), controle de vagas, agendamento de entrevistas, validação de requisitos por competência (skills) e envio automático de notificações por e-mail.

---

## 🛠️ Tecnologias Utilizadas
- **Linguagem & Framework Core:** Java 17, Spring Boot 4.0.7 (Spring Data JPA, Spring WebMVC, Spring Security, Spring Validation, Spring Actuator, Spring Mail)
- **Banco de Dados:** PostgreSQL 16 (Hibernate ORM)
- **Autenticação & Segurança:** JWT assinado com par de chaves RSA/PEM, cookies `HttpOnly`, proteção CSRF e BCrypt Password Encoder
- **Documentação de API:** Open API 3 / Swagger UI (`springdoc-openapi-starter-webmvc-ui` 3.0.2)
- **Gerenciamento de Dependências:** Maven
- **Conteinerização & Orquestração:** Docker, Docker Compose, Socat Bridge
- **Auxiliares:** Lombok, Spring Dotenv, Apache Commons Validator

---

## 📦 Instruções para Instalação

### Pré-requisitos
- **Java JDK 17+** instalado
- **Maven 3.8+** (ou utilizar o wrapper `./mvnw` / `mvnw.cmd`)
- **Docker** e **Docker Compose** (opcional, para execução via contêineres)
- Instância do **PostgreSQL 16** ativa (caso execute localmente sem Docker)

### Passo a Passo de Instalação
1. **Clonar o repositório:**
   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd quick-transfer-backend
   ```

2. **Configurar as Variáveis de Ambiente:**
   Crie um arquivo `.env` na raiz do projeto conforme a seção de [Variáveis de Ambiente Necessárias](#-variáveis-de-ambiente-necessárias).

3. **Compilar e baixar dependências via Maven:**
   ```bash
   ./mvnw clean verify
   ```
   *(No Windows Command Prompt / PowerShell, utilize `mvnw.cmd clean verify`)*

---

## 🚀 Instruções para Execução

### Opção A: Execução Local com Spring Boot Maven Plugin
1. Garanta que o banco de dados PostgreSQL esteja em execução e acessível com as credenciais especificadas no arquivo `.env`.
2. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
   A aplicação estará acessível em `http://localhost:8080/api` (ou conforme porta configurada).

### Opção B: Execução via Docker Compose (Recomendado)
O projeto inclui suporte completo a Docker Compose para subir o banco de dados PostgreSQL, o serviço backend e a bridge de rede externa:
```bash
docker-compose up --build -d
```
- **Backend App Container:** Porta `8081` (mapeada internamente para a `8080`).
- **PostgreSQL Container:** Porta `5432`.
- **Bridge Network (Socat):** Porta `8082`.

---

## 🔑 Variáveis de Ambiente Necessárias
As variáveis de ambiente devem ser configuradas no arquivo `.env` localizado na raiz do projeto (gerenciado via `me.paulschwarz:spring-dotenv`):

| Variável | Descrição | Exemplo / Valor Padrão |
| :--- | :--- | :--- |
| `DATABASE_URL` | URL de conexão JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/quick_transfer` |
| `DATABASE_USER` | Usuário do banco de dados | `postgres` |
| `DATABASE_PASSWORD` | Senha do banco de dados | `senha_segura` |
| `ALLOWED_ORIGINS` | Origens permitidas para requisições CORS | `http://localhost:3000,http://localhost:5173` |
| `PUBLIC_KEY` | Chave pública RSA em PEM; quebras de linha podem ser representadas por `\n` | Sem padrão |
| `PRIVATE_KEY` | Chave privada RSA PKCS#8 em PEM; nunca deve ser versionada | Sem padrão |
| `MAIL_USERNAME` | Usuário do servidor SMTP | Sem padrão |
| `MAIL_PASSWORD` | Senha ou token de aplicativo SMTP | Sem padrão |
| `MAIL_FROM` | Remetente apresentado nos e-mails | `no-reply@quick-transfer.local` |
| `MAIL_HOST` / `MAIL_PORT` | Servidor e porta SMTP | `smtp.gmail.com` / `587` |
| `FRONTEND_URL` | URL usada nos links dos e-mails | `http://localhost:3000` |
| `COOKIE_SECURE` | Exige HTTPS para o cookie JWT | `true` |
| `JPA_DDL_AUTO` | Estratégia Hibernate; use `validate` fora do Docker local | `validate` |
| `LOCAL_CACHE_ENABLED` | Cache Caffeine local; habilite somente em instância única | `false` |

As chaves RSA devem ser geradas fora do repositório. A chave privada e credenciais SMTP nunca devem ser adicionadas ao Git. Para desenvolvimento HTTP local, defina `COOKIE_SECURE=false`; em produção, mantenha `true`.

Como o projeto não utiliza ferramenta de migração, o Compose usa `JPA_DDL_AUTO=update` por padrão apenas para desenvolvimento. Em ambientes controlados, mantenha `validate` e aplique o DDL de forma administrada antes da publicação.

---

## 🌐 Endereço e Documentação da API

- **Context Path da Aplicação:** `/api`
- **Porta Padrão Local:** `8080` (Acesso: `http://localhost:8080/api`)
- **Documentação Interativa (Swagger UI):**  
  `http://localhost:8080/api/swagger-ui/index.html` (ou via Docker na porta mapeada `http://localhost:8081/api/swagger-ui/index.html`)
- **OpenAPI Schema (JSON):**  
  `http://localhost:8080/api/v3/api-docs`

### CSRF e paginação

Depois do login, obtenha o token CSRF com `GET /api/auth/csrf` e envie o valor retornado no cabeçalho `X-XSRF-TOKEN` nas operações que modificam dados. O cookie `XSRF-TOKEN` deve acompanhar a requisição.

Endpoints de listagem e pesquisa aceitam `page`, `size` e `sort`. O tamanho padrão é 50 e o limite máximo é 100 itens por página.

---

## ⚙️ Principais Funcionalidades

- **Autenticação e Autorização:**
  - Login e autenticação segura via JWT com os papéis (`ADMIN`, `MANAGER`, `COORDINATOR`). Estudantes são entidades de domínio, não usuários autenticáveis.
- **Gestão de Usuários:**
  - CRUD completo para Administradores, Gestores, Coordenadores e Estudantes.
- **Gestão de Turmas e Cursos (`ClassEntity` & `Course`):**
  - Mapeamento e vinculação de estudantes a turmas e cursos técnicos/formações.
- **Gestão de Vagas (`Vacancy`):**
  - Abertura, atualização, encerramento de vagas e requisito de competências (`Skills`).
- **Fluxo de Entrevistas (`Interview`):**
  - Agendamento, alteração de status (Agendada, Concluída, Cancelada) e avaliação de candidatos.
- **Gestão de Locais (`Place`):**
  - Cadastro de instalações e salas para realização de treinamentos e entrevistas.
- **Notificações:**
  - Envio automatizado de e-mails de confirmação e atualizações via JavaMail Sender.
- **Monitoramento de Saúde (Actuator):**
  - Endpoints de saúde e métricas do sistema (`/api/actuator/health`).

---

## 📁 Estrutura Resumida de Pastas

```text
quick-transfer-backend/
├── .github/                     # Workflows e pipelines CI/CD
├── src/
│   ├── main/
│   │   ├── java/com/weg/quicktransfer/
│   │   │   ├── config/          # Configurações de CORS, Swagger, OpenAPI e Beans
│   │   │   ├── controller/      # Endpoints REST (Auth, Admin, Student, Vacancy, etc.)
│   │   │   ├── dto/             # Objetos de Transferência de Dados (Requests/Responses)
│   │   │   ├── enums/           # Enumeradores do domínio (Roles, Status, etc.)
│   │   │   ├── exception/       # Manipulação global de exceções (GlobalExceptionHandler)
│   │   │   ├── mapper/          # Conversores entre Entidades e DTOs
│   │   │   ├── model/           # Entidades JPA (User, Student, Vacancy, Interview, etc.)
│   │   │   ├── repo/            # Repositórios Spring Data JPA
│   │   │   ├── scheduler/       # Tarefas agendadas em segundo plano
│   │   │   ├── security/        # Filtros JWT, UserDetailsService e configurações de segurança
│   │   │   └── service/         # Camada de Regras de Negócio e Serviços
│   │   └── resources/
│   │       ├── application.properties # Configurações da aplicação Spring
│   │       └── log4j2.xml       # Configurações de logging
│   └── test/java/com/weg/quicktransfer/ # Testes unitários e de integração de regras de negócio
├── Dockerfile                   # Configuração de compilação da imagem Docker
├── docker-compose.yml           # Orquestração do banco PostgreSQL, backend e rede
├── pom.xml                      # Gerenciador de dependências e plugins Maven
└── README.md                    # Documentação do projeto
```

---

## 🛡️ Boas Práticas de Segurança Adotadas

1. **Autenticação via JWT:** Autenticação stateless baseada em tokens JWT com tempo de expiração definido e suporte a par de chaves RSA.
2. **Criptografia de Senhas:** Utilização de algoritmo seguro de hashing para armazenamento de credenciais.
3. **Controle de Acesso Baseado em Perfis (RBAC):** Restrição de endpoints sensíveis através de anotações e filtros do Spring Security de acordo com os papéis do usuário (`ADMIN`, `MANAGER`, `COORDINATOR`).
4. **Validação Rigorosa de Dados:** Uso do `spring-boot-starter-validation` e `commons-validator` em DTOs de entrada para prevenir injeção de dados maliciosos.
5. **Configuração de CORS Controlada:** Permissão explícita apenas para origens e cabeçalhos autorizados via variáveis de ambiente.
6. **Ocultação de Stacktraces em Produção:** Configuração de `server.error.include-stacktrace=never` para evitar a exposição de detalhes internos do servidor em respostas de erro.
7. **Isolamento em Contêineres:** Uso de rede interna de contêineres Docker e usuário sem privilégios elevados.

---

## 🧪 Procedimento Utilizado para Realização dos Testes

O projeto utiliza o **JUnit 5** em conjunto com **Spring Boot Test** para garantir a qualidade do código e a conformidade das regras de negócio.

### Tipos de Testes Implementados
- **Testes de Regras de Negócio (Service Tests):** Cobertura dos fluxos de criação de usuários, agendamento de entrevistas, regras de transferência e gerenciamento de vagas (`AdminServiceTest`, `StudentServiceTest`, `InterviewServiceTest`, `VacancyServiceTest`, etc.).
- **Testes de Segurança e Infraestrutura:** Contratos de autorização, política de senha, Specifications JPA em H2 e concorrência/idempotência do scheduler.

### Como Executar os Testes
Para rodar a suíte completa de testes automatizados, execute o comando:
```bash
./mvnw test
```
*(No Windows: `mvnw.cmd test`)*

---

## ⚠️ Limitações Conhecidas

1. **Dependência de Servidor SMTP Externo:** O envio de e-mails depende de credenciais de serviço SMTP ativo. Caso o serviço SMTP esteja indisponível ou bloqueado por firewall, notificações de e-mail podem falhar silenciosamente ou gerar timeout.
2. **Execução de Schemas DDL Dinâmicos:** A configuração atual utiliza `spring.jpa.hibernate.ddl-auto=update` para ambiente de desenvolvimento; em ambientes de produção de grande porte, recomenda-se a migração para versionamento de schema com **Flyway** ou **Liquibase**.
3. **Integração de Armazenamento de Arquivos Local:** O upload e armazenamento de currículos/documentos anexos utiliza o sistema de arquivos local do container ou servidor, necessitando de volumes persistentes adequados.
