# 🚀 Como Rodar o Projeto Gestock

## 📋 Sobre o Projeto

O **Gestock** é um sistema de gerenciamento inteligente de estoques desenvolvido para otimizar o controle de produtos, pedidos, fornecedores e estoques, garantindo visibilidade, rastreabilidade e automação de processos logísticos e operacionais.

---

## ✅ Pré-requisitos

Certifique-se de ter instalado:

- **Java 17+** (JDK)
- **Maven 3.8+**
- **Docker** e **Docker Compose**

### Verificar as versões

```bash
java -version
mvn -version
docker --version
docker compose version
```

**Versões recomendadas:**
- Java: OpenJDK 17 ou superior
- Maven: 3.8.0 ou superior
- Docker: 20.10 ou superior

---

## 🚀 Início Rápido (3 Passos)

### 1️⃣ Clonar o Repositório

```bash
git clone <url-do-repositorio>
cd gestock-sistema-gerenciador-estoque
```

---

### 2️⃣ Compilar o Projeto

Compile todos os módulos Maven:

```bash
mvn clean install -DskipTests
```

**Tempo estimado:** ~30-40 segundos

**O que acontece:**
- Compila os módulos: `dominio-principal`, `aplicacao`, `infraestrutura`, `apresentacao-backend`, `apresentacao-frontend`
- Gera os JARs em `target/` de cada módulo
- Baixa todas as dependências necessárias

**Saída esperada:**
```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for sge-modulos 0.0.1-SNAPSHOT:
[INFO] 
[INFO] sge-pai ............................................ SUCCESS
[INFO] sge-dominio-principal .............................. SUCCESS
[INFO] sge-aplicacao ...................................... SUCCESS
[INFO] sge-infraestrutura ................................. SUCCESS
[INFO] sge-apresentacao-backend ........................... SUCCESS
[INFO] sge-apresentacao-frontend .......................... SUCCESS
[INFO] sge-modulos ........................................ SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

### 3️⃣ Subir o Banco de Dados (PostgreSQL)

O projeto usa PostgreSQL em container Docker:

```bash
docker compose up -d
```

**Verificar se subiu:**

```bash
docker ps
```

Você deve ver algo como:

```
CONTAINER ID   IMAGE         COMMAND                  STATUS         PORTS                    NAMES
abc123...      postgres:17   "docker-entrypoint..."   Up 5 seconds   0.0.0.0:5433->5432/tcp   gestock-database
```

**Verificar logs do container:**

```bash
docker logs gestock-database
```

Você deve ver: `database system is ready to accept connections`

---

## 🗄️ Configuração do Banco de Dados

| Propriedade | Valor |
|-------------|-------|
| **Host** | `localhost` |
| **Porta** | `5433` |
| **Database** | `gestock` |
| **Schema** | `gestock` |
| **Usuário** | `gestock` |
| **Senha** | `gestock` |
| **URL JDBC** | `jdbc:postgresql://localhost:5433/gestock` |

**Migrações Flyway:**  
As migrações (`V1__Gestock_criacao.sql`, `V2__Gestock_povoamento.sql`) rodam automaticamente quando o backend iniciar.

**Scripts SQL localizados em:**
```
infraestrutura/src/main/resources/db/migration/
├── V1__Gestock_criacao.sql    # Criação das tabelas
└── V2__Gestock_povoamento.sql # Dados iniciais
```

---

## 🖥️ Rodar o Backend (Spring Boot)

> ⚠️ **IMPORTANTE**: Antes de rodar o backend, é necessário descomentar os arquivos de configuração que estão comentados.

### Passo 1: Ativar o Backend

Você precisa descomentar (remover os `//` ou `/**  **/`) dos seguintes arquivos:

1. **BackendAplicacao.java**
   - Localização: `apresentacao-backend/src/main/java/dev/gestock/sge/BackendAplicacao.java`
   - Este arquivo contém a configuração principal do Spring Boot

2. **BackendDesenvolvimentoAplicacao.java**
   - Localização: `apresentacao-backend/src/test/java/dev/gestock/sge/BackendDesenvolvimentoAplicacao.java`
   - Este arquivo ativa o perfil de desenvolvimento

3. **BackendMapeador.java** (se necessário)
   - Localização: `apresentacao-backend/src/main/java/dev/gestock/sge/apresentacao/BackendMapeador.java`
   - Este arquivo configura o ModelMapper

### Passo 2: Recompilar

Após descomentar os arquivos:

```bash
mvn clean install -DskipTests
```

### Passo 3: Rodar o Backend

#### Opção 1: Via Maven (Recomendado para Desenvolvimento)

```bash
cd apresentacao-backend
mvn test-compile exec:java "-Dexec.mainClass=dev.gestock.sge.BackendDesenvolvimentoAplicacao" "-Dexec.classpathScope=test"
```

#### Opção 2: Via IDE (IntelliJ IDEA / Eclipse / VSCode)

1. Abra a classe: `apresentacao-backend/src/test/java/dev/gestock/sge/BackendDesenvolvimentoAplicacao.java`
2. Clique com botão direito → **Run** (ou **Debug**)
3. Aguarde a mensagem: `Started BackendAplicacao in X seconds`

#### Opção 3: Via Spring Boot Maven Plugin

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=desenvolvimento -pl apresentacao-backend
```

**Tempo de inicialização:** ~7-10 segundos

---

### ✅ Verificar se o Backend Subiu

#### PowerShell (Windows):

```powershell
Invoke-RestMethod -Uri http://localhost:8080/actuator/health
```

#### Linux/Mac:

```bash
curl http://localhost:8080/actuator/health
```

**Resposta esperada:**
```json
{
  "status": "UP"
}
```

---

## 📡 Endpoints Principais da API

O backend roda em: **http://localhost:8080**

### Endpoints de Produtos

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/backend/produto/pesquisa` | GET | Lista todos os produtos |
| `/backend/produto/cadastrar` | POST | Cadastra um novo produto |
| `/backend/produto/{id}` | GET | Busca produto por ID |
| `/backend/produto/{id}/atualizar` | PUT | Atualiza produto |
| `/backend/produto/{id}/inativar` | DELETE | Inativa produto |

### Endpoints de Estoques

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/backend/estoque/pesquisa` | GET | Lista todos os estoques |
| `/backend/estoque/cadastrar` | POST | Cadastra novo estoque |
| `/backend/estoque/{id}` | GET | Busca estoque por ID |
| `/backend/estoque/{id}/movimentacao` | POST | Registra movimentação |

### Endpoints de Fornecedores

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/backend/fornecedor/pesquisa` | GET | Lista todos os fornecedores |
| `/backend/fornecedor/cadastrar` | POST | Cadastra fornecedor |
| `/backend/fornecedor/{id}` | GET | Busca fornecedor por ID |

### Endpoints de Clientes

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/backend/cliente/pesquisa` | GET | Lista todos os clientes |
| `/backend/cliente/cadastrar` | POST | Cadastra cliente |
| `/backend/cliente/{id}` | GET | Busca cliente por ID |

### Endpoints de Pedidos

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/backend/pedido/pesquisa` | GET | Lista todos os pedidos |
| `/backend/pedido/cadastrar` | POST | Cria novo pedido |
| `/backend/pedido/{id}/aprovar` | POST | Aprova pedido |

### Endpoints de Alertas

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/backend/alerta/pesquisa` | GET | Lista alertas de estoque baixo |
| `/backend/alerta/{id}` | GET | Busca alerta por ID |

---

## 📚 Documentação da API (Swagger)

Após subir o backend, acesse a documentação interativa:

**Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🎨 Frontend (Status)

> ⚠️ **Nota**: O módulo de frontend ainda não está implementado. A pasta `apresentacao-frontend` existe na estrutura, mas não contém código React/Angular/Vue.

Para acessar o sistema, use:
- **Swagger UI** (interface interativa para testar a API)
- **Postman/Insomnia** (clientes REST)
- **cURL/PowerShell** (linha de comando)

---

## 🧪 Como Rodar os Testes

### Testes de Domínio (Cucumber BDD)

Os testes estão escritos em Gherkin e automatizados com Cucumber:

```bash
mvn test -pl dominio-principal
```

**O que testa:**
- Cenários Gherkin (`.feature`)
- Regras de negócio (RN-*)
- Entidades de domínio
- Serviços de domínio

**Arquivos de teste (.feature):**
- `gerenciarProdutos.feature` - Cadastro e gerenciamento de produtos
- `gerenciarEstoque.feature` - Operações de estoque
- `gerenciarFornecedores.feature` - Gestão de fornecedores
- `gerenciarPedidos.feature` - Fluxo de pedidos
- `registrarMovimentacao.feature` - Movimentações de estoque
- `reservarEstoque.feature` - Reservas de produtos
- `transferirProdutos.feature` - Transferências entre estoques
- `calcularROP.feature` - Cálculo de Ponto de Reposição
- `alerta.feature` - Alertas de estoque baixo
- `selecionarCotacao.feature` - Seleção de cotações

### Rodar Todos os Testes

```bash
mvn test
```

### Rodar Testes de um Módulo Específico

```bash
mvn test -pl infraestrutura
mvn test -pl aplicacao
mvn test -pl apresentacao-backend
```

### Ver Relatório de Testes

Após executar os testes, os relatórios são gerados em:

```
dominio-principal/target/surefire-reports/
```

---

## ✅ Resumo dos Serviços Rodando

| Serviço | URL | Porta | Status |
|---------|-----|-------|--------|
| **Backend (API REST)** | http://localhost:8080 | 8080 | 🟢 Spring Boot |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | 8080 | 🟢 Documentação |
| **PostgreSQL** | localhost:5433 | 5433 | 🟢 Docker Container |

---

## 🛑 Como Parar os Serviços

### Parar o Backend

Pressione `Ctrl+C` no terminal onde o backend está rodando.

### Parar o Banco de Dados

```bash
docker compose down
```

### Parar e Remover Volumes (Resetar Banco)

```bash
docker compose down -v
```

> ⚠️ **Atenção**: Isso irá **deletar todos os dados** do banco!

---

## 🔧 Troubleshooting (Erros Comuns)

### ❌ Erro: `Cannot load driver class: org.postgresql.Driver`

**Causa:** Dependência do PostgreSQL não encontrada.

**Solução:**

```bash
mvn clean install -DskipTests
```

---

### ❌ Erro: `Failed to configure a DataSource: 'url' attribute is not specified`

**Causa:** Backend rodando sem o perfil `desenvolvimento` ou sem o `application-desenvolvimento.properties`.

**Solução 1:** Use a classe `BackendDesenvolvimentoAplicacao`:

```bash
mvn test-compile exec:java "-Dexec.mainClass=dev.gestock.sge.BackendDesenvolvimentoAplicacao" "-Dexec.classpathScope=test"
```

**Solução 2:** Ative o perfil manualmente:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=desenvolvimento -pl apresentacao-backend
```

---

### ❌ Erro: `Connection to localhost:5433 refused`

**Causa:** Container PostgreSQL não está rodando.

**Solução:**

```bash
docker compose up -d
docker ps  # verificar se está UP
docker logs gestock-database  # verificar logs
```

---

### ❌ Erro: `Port 8080 is already in use`

**Causa:** Outro processo está usando a porta 8080.

**Solução (PowerShell - Windows):**

```powershell
# Encontrar processo usando a porta 8080
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object OwningProcess

# Matar o processo (substitua <PID> pelo número do processo)
Stop-Process -Id <PID> -Force
```

**Solução (Linux/Mac):**

```bash
# Encontrar e matar processo
lsof -ti:8080 | xargs kill -9
```

**Solução Alternativa:** Mudar a porta do backend

Edite `apresentacao-backend/src/test/resources/application-desenvolvimento.properties`:

```properties
server.port=8081
```

---

### ❌ Erro: `Flyway migration failed`

**Causa:** Esquema do banco está inconsistente ou corrompido.

**Solução:** Recriar o banco do zero:

```bash
docker compose down -v  # remove volumes
docker compose up -d    # recria o container
```

Aguarde o backend reiniciar (ele vai aplicar as migrações automaticamente).

---

### ❌ Erro: `Tests compilation failed`

**Causa:** Arquivos comentados ou faltando dependências.

**Solução:**

1. Verifique se descomentou os arquivos principais
2. Execute:

```bash
mvn clean install -DskipTests
mvn test-compile
```

---

### ❌ Erro: `ClassNotFoundException` no backend

**Causa:** Dependências não estão no classpath.

**Solução:**

```bash
mvn clean install -U
```

O parâmetro `-U` força o Maven a atualizar as dependências.

---

### ❌ Erro: `Port 5433 is already in use`

**Causa:** Outra instância do PostgreSQL está rodando nessa porta.

**Solução 1:** Parar o outro container:

```bash
docker ps  # ver todos os containers
docker stop <container-id>
```

**Solução 2:** Mudar a porta no `docker-compose.yml`:

```yaml
ports:
  - 5434:5432  # mude de 5433 para 5434
```

Depois atualize o `application-desenvolvimento.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5434/gestock
```

---

## 📁 Estrutura do Projeto

```
gestock-sistema-gerenciador-estoque/
│
├── dominio-principal/              # 🏛️ Camada de Domínio
│   ├── src/main/java/             # Entidades, agregados, serviços de domínio
│   │   └── dev/gestock/sge/dominio/principal/
│   │       ├── produto/           # Agregado Produto
│   │       ├── estoque/           # Agregado Estoque
│   │       ├── fornecedor/        # Agregado Fornecedor
│   │       ├── cliente/           # Agregado Cliente
│   │       ├── pedido/            # Agregado Pedido
│   │       └── alerta/            # Agregado Alerta
│   └── src/test/resources/        # *.feature (Gherkin/Cucumber)
│
├── aplicacao/                      # 📋 Camada de Aplicação
│   └── src/main/java/             # Interfaces de consulta, resumos, DTOs
│       └── dev/gestock/sge/aplicacao/dominio/
│           ├── produto/
│           ├── estoque/
│           ├── fornecedor/
│           ├── cliente/
│           ├── pedido/
│           └── alerta/
│
├── infraestrutura/                 # 🔧 Camada de Infraestrutura
│   ├── src/main/java/             # Implementações JPA, repositórios concretos
│   │   └── dev/gestock/sge/infraestrutura/
│   │       ├── persistencia/      # Repositórios JPA
│   │       └── evento/            # Barramento de eventos
│   └── src/main/resources/
│       └── db/migration/          # Scripts Flyway
│           ├── V1__Gestock_criacao.sql      # Schema
│           └── V2__Gestock_povoamento.sql   # Dados iniciais
│
├── apresentacao-backend/           # 🌐 Camada de Apresentação (API REST)
│   ├── src/main/java/
│   │   └── dev/gestock/sge/
│   │       ├── BackendAplicacao.java           # Classe principal Spring Boot
│   │       └── apresentacao/
│   │           ├── BackendMapeador.java        # ModelMapper
│   │           └── principal/
│   │               ├── produto/                # Controllers de Produto
│   │               ├── estoque/                # Controllers de Estoque
│   │               ├── fornecedor/             # Controllers de Fornecedor
│   │               ├── cliente/                # Controllers de Cliente
│   │               ├── pedido/                 # Controllers de Pedido
│   │               └── alerta/                 # Controllers de Alerta
│   ├── src/main/resources/
│   │   └── application.properties              # Config produção (vazio)
│   └── src/test/
│       ├── java/
│       │   └── dev/gestock/sge/
│       │       └── BackendDesenvolvimentoAplicacao.java  # Dev server
│       └── resources/
│           └── application-desenvolvimento.properties    # Config dev
│
├── apresentacao-frontend/          # 🎨 Camada de Apresentação (Frontend)
│   └── (ainda não implementado)
│
├── pai/                            # 🏗️ POM Pai
│   └── pom.xml                    # Dependências compartilhadas
│
├── docker-compose.yml              # 🐳 PostgreSQL container
├── pom.xml                         # 📦 POM raiz (multi-módulo)
│
├── Documentacao/                   # 📚 Documentação do projeto
│   ├── Apresentação Gestock.pdf
│   ├── Definição de Funcionalidades, User Stories e Regras de Negócio - Gestock.pdf
│   ├── Descrição do Domínio - Sistema de Gestão de Estoques.pdf
│   ├── Mapa de Histórias de Usuário - Gestock.pdf
│   └── prototipo-gestock.png
│
├── COMO_RODAR.md                   # 🚀 Este arquivo (guia de instalação)
├── README.md                       # 📖 Visão geral do projeto
├── PadroesDeProjeto.md             # 🎨 Padrões de projeto utilizados
├── ANALISE_COMPARATIVA_PADROES.md
├── VERIFICACAO_FINAL_PADROES.md
└── Gestock-Domain-Model.cml        # 🗺️ Modelo de domínio (Context Mapper)
```

---

## 🏗️ Arquitetura em Camadas (Clean Architecture)

```
┌─────────────────────────────────────────────────────────┐
│      Apresentação Backend (REST Controllers)            │
│     Spring Boot + Swagger + ModelMapper                 │
│               localhost:8080                            │
└─────────────────────────────────────────────────────────┘
                        ↓ HTTP REST
┌─────────────────────────────────────────────────────────┐
│       Aplicação (Interfaces de Consulta)                │
│     Resumos, DTOs, Serviços de Aplicação                │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│    Domínio Principal (Entidades, Agregados, Serviços)  │
│   • Produto  • Estoque  • Fornecedor  • Cliente        │
│   • Pedido   • Alerta   • Movimentação                  │
│   Padrões: Strategy, Template, Observer, Decorator     │
└─────────────────────────────────────────────────────────┐
                        ↓
┌─────────────────────────────────────────────────────────┐
│    Infraestrutura (JPA, Repositórios, Flyway)          │
│     PostgreSQL (Docker) - localhost:5433                │
└─────────────────────────────────────────────────────────┘
```

### Fluxo de Dados

1. **Cliente** → Faz requisição HTTP para o backend
2. **Controller** → Recebe requisição, valida dados
3. **Serviço de Aplicação** → Orquestra casos de uso
4. **Domínio** → Executa lógica de negócio
5. **Repositório** → Persiste/recupera dados do banco
6. **Infraestrutura** → Implementa persistência com JPA/Hibernate

---

## 🎯 Conceitos-Chave do Domínio

### Agregados Principais

| Agregado | Descrição | Entidade Raiz |
|----------|-----------|---------------|
| **Produto** | Item gerenciado no estoque | `Produto` |
| **Estoque** | Depósito físico onde produtos são armazenados | `Estoque` |
| **Fornecedor** | Empresa que fornece produtos | `Fornecedor` |
| **Cliente** | Dono dos estoques (empresa/organização) | `Cliente` |
| **Pedido** | Solicitação de compra de produtos | `Pedido` |
| **Alerta** | Notificação de estoque baixo | `Alerta` |

### Entidades e Value Objects

- **Produto**: Código único, nome, unidade de medida, peso, perecível
- **ItemEstoque**: Relaciona produto com estoque (quantidade, ROP)
- **Movimentacao**: Entrada/saída de produtos (tipo, quantidade, data)
- **Cotacao**: Preço e prazo oferecido por fornecedor
- **Pedido**: Conjunto de itens solicitados de um fornecedor

---

## 📝 Regras de Negócio Principais

### Produtos (H8)

- **RN-R1H8**: Código do produto deve ser único por cliente
- **RN-R2H8**: Produto pode ser fornecido por múltiplos fornecedores
- **RN-R3H8**: Produto deve estar vinculado a pelo menos um estoque

### Estoques (H1, H2, H3)

- **RN-R1H1**: Cada estoque pertence a um único cliente
- **RN-R2H1**: Estoque deve ter nome único por cliente
- **RN-R3H1**: Estoque deve ter capacidade máxima definida
- **RN-R1H2**: Sistema deve alertar quando estoque atingir nível mínimo (ROP)
- **RN-R1H3**: Movimentações devem ser rastreáveis (data, tipo, quantidade)

### Pedidos (H6, H7)

- **RN-R1H6**: Pedido deve ter pelo menos um item
- **RN-R2H6**: Pedido aprovado não pode ser alterado
- **RN-R1H7**: Sistema deve selecionar cotação com melhor custo-benefício

### Fornecedores (H5)

- **RN-R1H5**: CNPJ do fornecedor deve ser único
- **RN-R2H5**: Fornecedor deve fornecer pelo menos um produto

---

## 🧪 Testes BDD (Behavior-Driven Development)

Os testes estão escritos em **Gherkin** (linguagem natural) e automatizados com **Cucumber**.

### Exemplo de Cenário de Teste

**Arquivo:** `gerenciarProdutos.feature`

```gherkin
Funcionalidade: Gerenciar Produtos

  Cenario: Cadastrar produto com sucesso
    Dado que o cliente informa codigo "PROD-001", nome "Produto A", unidade "UN" e indica que nao e perecivel
    Quando o cliente confirma o cadastro do produto
    Entao o sistema deve cadastrar o produto com sucesso
    E o produto deve estar ativo
    E o ROP deve estar nulo inicialmente
```

### Rodar um Teste Específico

```bash
mvn test -pl dominio-principal -Dtest=GerenciarProdutosFuncionalidade
```

---

## 📊 Tecnologias Utilizadas

### Backend

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Java** | 17 | Linguagem principal |
| **Spring Boot** | 3.4.4 | Framework web |
| **Spring Data JPA** | 3.4.4 | Persistência ORM |
| **Hibernate** | 6.x | Implementação JPA |
| **PostgreSQL** | 17 | Banco de dados |
| **Flyway** | Latest | Migrações de schema |
| **ModelMapper** | 3.2.2 | Mapeamento objeto-objeto |
| **Springdoc OpenAPI** | 2.8.6 | Documentação Swagger |
| **Cucumber** | 7.21.1 | Testes BDD |
| **JUnit 5** | Latest | Framework de testes |
| **Maven** | 3.8+ | Gerenciamento de dependências |

### Infraestrutura

- **Docker** - Containerização
- **Docker Compose** - Orquestração de containers
- **PostgreSQL 17** - Banco de dados relacional

---

## 🎓 Padrões de Projeto Utilizados

O Gestock implementa diversos padrões de projeto conforme documentado em `PadroesDeProjeto.md`:

- **Repository Pattern** - Abstração de persistência
- **Strategy Pattern** - Seleção de cotações
- **Template Method** - Fluxos de pedidos
- **Observer Pattern** - Alertas de estoque
- **Decorator Pattern** - Enriquecimento de produtos
- **Factory Pattern** - Criação de entidades
- **Domain-Driven Design (DDD)** - Arquitetura de domínio

---

## 📚 Documentação Adicional

- **User Story Map**: [Avion.io](https://gestock.avion.io/share/bp92kjfD92MCfSDRT)
- **Protótipo**: [YouTube](https://www.youtube.com/watch?v=PDMaYk-0dAI)
- **Modelo de Domínio**: `Gestock-Domain-Model.cml`
- **Funcionalidades e Regras**: `Documentacao/Definição de Funcionalidades, User Stories e Regras de Negócio - Gestock.pdf`

---

## 👥 Equipe de Desenvolvimento

Projeto desenvolvido no contexto da disciplina **Requisitos, Projeto de Software e Validação** – CESAR School.

**Equipe Gestock:**
- Bernardo Heuer
- Eduardo Roma
- Rodrigo Nunes
- Ronaldo Souto Maior
- Sílvio Fitipaldi

---

## 📞 Suporte

Em caso de dúvidas ou problemas:

1. Consulte a seção **Troubleshooting** acima
2. Verifique os logs do backend e do container Docker
3. Revise a documentação em `Documentacao/`
4. Entre em contato com a equipe de desenvolvimento

---

## 📄 Licença

Este projeto está sob a licença especificada no arquivo `LICENSE`.

---

## 🎉 Pronto para Começar!

Siga os passos acima e você terá o Gestock rodando em poucos minutos.

**Happy Coding! 🚀**

