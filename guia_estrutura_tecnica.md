# 🏗️ Guia Técnico: Estrutura Arquitetural do Back-End

**Objetivo:** Entender a estrutura técnica do back-end para replicar em outros projetos, **sem focar em regras de negócio**.

---

## 📦 Estrutura de Módulos Maven

O projeto é um **multi-módulo Maven** com separação clara de responsabilidades:

```
qnota/
├── pom.xml                          # Módulo raiz (agrega todos)
├── pai/pom.xml                      # POM pai (gerencia dependências)
│
├── dominio-principal/                # Módulo 1: Domínio
│   └── pom.xml
│
├── aplicacao/                        # Módulo 2: Aplicação
│   └── pom.xml
│
├── infraestrutura/                   # Módulo 3: Infraestrutura
│   └── pom.xml
│
└── apresentacao-backend/             # Módulo 4: Apresentação (Spring Boot)
    └── pom.xml
```

### Dependências entre Módulos

```
apresentacao-backend
    ↓ depende de
aplicacao + dominio-principal
    ↓ depende de
infraestrutura
    ↓ depende de
aplicacao + dominio-principal
```

**Regra:** As dependências sempre apontam **para baixo** (camadas superiores dependem das inferiores).

---

## 📁 Estrutura de Pacotes

### Padrão de Organização

Cada módulo segue o padrão: `dev.com.qnota.{modulo}/{subpacote}`

```
dev.com.qnota
├── apresentacao/              # apresentacao-backend
│   ├── principal/
│   │   ├── aluno/
│   │   │   ├── AlunoControlador.java
│   │   │   └── AlunoFormulario.java
│   │   ├── professor/
│   │   └── ...
│   └── BackendMapeador.java
│
├── aplicacao/                 # aplicacao
│   └── principal/
│       ├── aluno/
│       │   ├── AlunoServicoAplicacao.java
│       │   ├── AlunoRepositorioAplicacao.java
│       │   └── AlunoResumo.java
│       └── ...
│
├── dominio/                   # dominio-principal
│   └── principal/
│       ├── aluno/
│       │   ├── Aluno.java              # Entidade
│       │   ├── AlunoId.java            # Value Object
│       │   ├── AlunoServico.java       # Serviço de domínio
│       │   └── AlunoRepositorio.java   # Interface
│       └── ...
│
└── infraestrutura/            # infraestrutura
    └── persistencia/
        └── jpa/
            ├── AlunoJpa.java           # Entidade JPA
            ├── AlunoJpaRepository.java  # Spring Data
            └── AlunoRepositorioImpl.java # Implementação
```

---

## 🔄 Fluxo Técnico: Controller → Banco → Controller

### 1. CAMADA DE APRESENTAÇÃO (Controller)

**Localização:** `apresentacao-backend/src/main/java/dev/com/qnota/apresentacao/`

#### 1.1 Estrutura do Controller

```java
@RestController
@RequestMapping("backend/aluno")
class AlunoControlador {
    
    // Injeção de dependências
    private @Autowired AlunoServico alunoServico;              // Domínio
    private @Autowired AlunoServicoAplicacao alunoServicoConsulta; // Aplicação
    private @Autowired BackendMapeador mapeador;
    
    // Endpoint GET (consulta)
    @RequestMapping(method = GET, path = "pesquisa")
    List<AlunoResumoDto> pesquisa() {
        return alunoServicoConsulta.pesquisarResumos().stream()
            .map(r -> new AlunoResumoDto(...))
            .toList();
    }
    
    // Endpoint POST (comando)
    @RequestMapping(method = POST, path = "cadastrar")
    Integer cadastrar(@RequestBody AlunoFormulario.AlunoDto dto) {
        // 1. Converte DTO para Value Objects
        var turmaId = mapeador.map(dto.turmaId, TurmaId.class);
        
        // 2. Chama serviço de domínio
        var alunoId = alunoServico.cadastrar(...);
        
        // 3. Retorna ID primitivo
        return alunoId.value();
    }
}
```

**Características:**
- `@RestController`: Marca como controller REST
- `@RequestMapping`: Define path base
- `@Autowired`: Injeção automática do Spring
- **Separação:** Consultas usam `ServicoAplicacao`, comandos usam `Servico` (domínio)

#### 1.2 Mapeador (DTO ↔ Domínio)

```java
@Component
public class BackendMapeador {
    
    public <D> D map(Object source, Class<D> destinationType) {
        // Integer → Value Object
        if (source instanceof Integer integer) {
            if (destinationType == AlunoId.class) {
                return (D) new AlunoId(integer);
            }
            // ... outros Value Objects
        }
        
        // Value Object → Integer
        if (destinationType == Integer.class) {
            if (source instanceof AlunoId alunoId) {
                return (D) Integer.valueOf(alunoId.value());
            }
        }
        
        throw new IllegalArgumentException("Tipo não suportado");
    }
}
```

**Função:** Converte entre tipos primitivos (Integer) e Value Objects do domínio.

---

### 2. CAMADA DE APLICAÇÃO (Consultas)

**Localização:** `aplicacao/src/main/java/dev/com/qnota/aplicacao/`

#### 2.1 Serviço de Aplicação

```java
public class AlunoServicoAplicacao {
    private AlunoRepositorioAplicacao repositorio;
    
    public AlunoServicoAplicacao(AlunoRepositorioAplicacao repositorio) {
        this.repositorio = repositorio;
    }
    
    public List<AlunoResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }
}
```

**Características:**
- Focado em **consultas/leitura**
- Retorna DTOs de leitura (Resumos)
- Não contém lógica de negócio

#### 2.2 Interface de Repositório de Aplicação

```java
public interface AlunoRepositorioAplicacao {
    List<AlunoResumo> pesquisarResumos();
}
```

**Função:** Define contratos para queries otimizadas.

---

### 3. CAMADA DE DOMÍNIO (Lógica)

**Localização:** `dominio-principal/src/main/java/dev/com/qnota/dominio/`

#### 3.1 Serviço de Domínio

```java
public class AlunoServico {
    private final AlunoRepositorio repo;
    private final ResponsavelRepositorio responsavelRepo;
    private final TurmaRepositorio turmaRepo;
    
    public AlunoServico(AlunoRepositorio repo, ...) {
        this.repo = repo;
        // ...
    }
    
    public AlunoId cadastrar(String nome, LocalDate nascimento, ...) {
        // Validações
        validarCadastro(...);
        
        // Cria agregado
        var aluno = new Aluno(nome, nascimento, ...);
        
        // Persiste
        return repo.salvar(aluno);
    }
}
```

**Características:**
- Focado em **comandos/escrita**
- Contém **lógica de negócio**
- Orquestra múltiplos agregados

#### 3.2 Interface de Repositório de Domínio

```java
public interface AlunoRepositorio {
    AlunoId salvar(Aluno aluno);
    Aluno porId(AlunoId id);
    void remover(AlunoId id);
    // ... outros métodos
}
```

**Função:** Define contrato de persistência, independente de tecnologia.

---

### 4. CAMADA DE INFRAESTRUTURA (Persistência)

**Localização:** `infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/`

#### 4.1 Entidade JPA

```java
@Entity
@Table(name = "alunos")
class AlunoJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @Column(nullable = false)
    String nome;
    
    @Column(name = "datanascimento", nullable = false)
    LocalDate dataNascimento;
    
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
    Set<AlunoResponsavelJpa> responsaveis = new LinkedHashSet<>();
}
```

**Características:**
- Classe **package-private** (não exposta)
- Mapeamento direto para tabela
- Anotações JPA padrão

#### 4.2 Repositório Spring Data JPA

```java
interface AlunoJpaRepository extends JpaRepository<AlunoJpa, Integer> {
    
    // Query automática (Spring Data)
    List<AlunoJpa> findByTurmaId(Integer turmaId);
    
    // Query nativa
    @Query(value = "SELECT ... FROM alunos ...", nativeQuery = true)
    List<AlunoResumo> findAlunoResumoByOrderByNome();
    
    // Query de modificação
    @Modifying
    @Query(value = "UPDATE alunos SET ...", nativeQuery = true)
    int alterarTurma(@Param("alunoId") int alunoId, ...);
}
```

**Características:**
- Estende `JpaRepository<Entidade, ID>`
- Métodos automáticos (ex: `findBy...`)
- Queries nativas com `@Query`
- Queries de modificação com `@Modifying`

#### 4.3 Implementação do Repositório de Domínio

```java
@Repository
class AlunoRepositorioImpl implements AlunoRepositorio, AlunoRepositorioAplicacao {
    
    private final AlunoJpaRepository alunoRepo;
    
    @Autowired
    AlunoRepositorioImpl(AlunoJpaRepository alunoRepo) {
        this.alunoRepo = alunoRepo;
    }
    
    // ===== Implementação do Repositório de Domínio =====
    
    @Override
    @Transactional
    public AlunoId salvar(Aluno aluno) {
        // 1. Converte Agregado → JPA
        var jpa = toJpa(aluno);
        
        // 2. Persiste via Spring Data
        jpa = alunoRepo.save(jpa);
        
        // 3. Atualiza ID no agregado
        if (aluno.getId() == null) {
            aluno.atribuirIdSeAusente(new AlunoId(jpa.id));
        }
        
        return new AlunoId(jpa.id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Aluno porId(AlunoId id) {
        // 1. Busca JPA
        var jpa = alunoRepo.findById(id.value())
            .orElseThrow(() -> new EntityNotFoundException(...));
        
        // 2. Converte JPA → Agregado
        return toDomain(jpa);
    }
    
    // ===== Implementação do Repositório de Aplicação =====
    
    @Override
    @Transactional(readOnly = true)
    public List<AlunoResumo> pesquisarResumos() {
        return alunoRepo.findAlunoResumoByOrderByNome();
    }
    
    // ===== Métodos de Conversão =====
    
    private static Aluno toDomain(AlunoJpa j) {
        // Converte JPA → Agregado de Domínio
        var aluno = new Aluno(j.nome, j.dataNascimento, ...);
        if (j.id != null) {
            aluno.atribuirIdSeAusente(new AlunoId(j.id));
        }
        return aluno;
    }
    
    private AlunoJpa toJpa(Aluno d) {
        // Converte Agregado → JPA
        AlunoJpa j;
        if (d.getId() == null) {
            j = new AlunoJpa();
        } else {
            j = alunoRepo.findById(d.getId().value())
                .orElseThrow(...);
        }
        j.nome = d.getNome();
        j.dataNascimento = d.getDataNascimento();
        // ...
        return j;
    }
}
```

**Características:**
- **Implementa** ambas interfaces (Domínio + Aplicação)
- **Converte** entre Agregado ↔ JPA
- **Delega** para Spring Data JPA
- **Gerencia** transações com `@Transactional`

---

## 🔌 Configuração do Banco de Dados

### 1. Docker Compose

```yaml
services:
  postgres:
    container_name: qnota-postgres
    image: postgres:17
    environment:
      POSTGRES_DB: qnota
      POSTGRES_USER: qnota
      POSTGRES_PASSWORD: qnota
    ports:
      - 5433:5432
    volumes:
      - postgres:/var/lib/postgresql/data
```

### 2. Application Properties

```properties
# DataSource
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5433/qnota
spring.datasource.username=qnota
spring.datasource.password=qnota

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Flyway (migrações)
spring.flyway.schemas=qnota
spring.flyway.baseline-on-migrate=true
```

### 3. Dependências Maven

```xml
<!-- infraestrutura/pom.xml -->
<dependencies>
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>
</dependencies>
```

### 4. Migrações Flyway

**Localização:** `infraestrutura/src/main/resources/db/migration/`

```
V1__QNota_criacao.sql
V2__QNota_Povoamento.sql
```

**Convenção:** `V{versao}__{nome}.sql`

---

## ⚙️ Configuração Spring Boot (Injeção de Dependências)

**Localização:** `apresentacao-backend/src/main/java/dev/com/qnota/AplicacaoBackend.java`

```java
@SpringBootApplication
public class AplicacaoBackend {
    
    // ===== Serviços de Domínio =====
    
    @Bean
    public AlunoServico alunoServico(
            AlunoRepositorio repositorio,
            ResponsavelRepositorio responsavelRepositorio,
            TurmaRepositorio turmaRepositorio,
            SimuladoRepositorio simuladoRepositorio) {
        return new AlunoServico(repositorio, responsavelRepositorio, 
                                turmaRepositorio, simuladoRepositorio);
    }
    
    // ===== Serviços de Aplicação =====
    
    @Bean
    public AlunoServicoAplicacao alunoServicoAplicacao(
            AlunoRepositorioAplicacao repositorio) {
        return new AlunoServicoAplicacao(repositorio);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(AplicacaoBackend.class, args);
    }
}
```

**Função:** Conecta todas as camadas através de injeção de dependências.

**Observação:** Os repositórios são descobertos automaticamente pelo Spring (via `@Repository`).

---

## 🔄 Fluxo Completo: Exemplo Prático

### Cenário: Cadastrar Aluno (POST)

```
┌─────────────────────────────────────────────────────────────┐
│ 1. HTTP POST /backend/aluno/cadastrar                       │
│    Body: { "nome": "João", "turmaId": 1, ... }              │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. AlunoControlador.cadastrar()                             │
│    - Recebe AlunoFormulario.AlunoDto                        │
│    - Converte: mapeador.map(dto.turmaId, TurmaId.class)    │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. AlunoServico.cadastrar()                                 │
│    - Valida regras                                           │
│    - Cria: new Aluno(nome, nascimento, ...)                 │
│    - Chama: repo.salvar(aluno)                               │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. AlunoRepositorioImpl.salvar()                            │
│    - Converte: toJpa(aluno) → AlunoJpa                       │
│    - Persiste: alunoRepo.save(jpa)                           │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. AlunoJpaRepository.save()                                │
│    - Spring Data JPA                                         │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. Hibernate/JPA                                             │
│    - Gera SQL: INSERT INTO alunos (...) VALUES (...)         │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. PostgreSQL (JDBC)                                         │
│    - Executa SQL                                             │
│    - Retorna ID gerado                                       │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 8. Resposta HTTP 200                                         │
│    Body: 42 (ID do aluno cadastrado)                        │
└─────────────────────────────────────────────────────────────┘
```

### Cenário: Pesquisar Alunos (GET)

```
┌─────────────────────────────────────────────────────────────┐
│ 1. HTTP GET /backend/aluno/pesquisa                         │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. AlunoControlador.pesquisa()                              │
│    - Chama: alunoServicoConsulta.pesquisarResumos()         │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. AlunoServicoAplicacao.pesquisarResumos()                 │
│    - Delega: repositorio.pesquisarResumos()                 │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. AlunoRepositorioImpl.pesquisarResumos()                  │
│    - Query: alunoRepo.findAlunoResumoByOrderByNome()        │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. AlunoJpaRepository.findAlunoResumoByOrderByNome()         │
│    - Query nativa SQL com JOINs                             │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. PostgreSQL                                                │
│    - Executa: SELECT a.id, a.nome, t.nome, ...              │
│    - Retorna: List<AlunoResumo> (projeção)                  │
└─────────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. Resposta HTTP 200                                         │
│    Body: [{ "id": 1, "nome": "João", ... }, ...]           │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 Checklist para Replicar em Outro Projeto

### 1. Estrutura de Módulos Maven

- [ ] Criar módulo raiz (`pom.xml` com `<packaging>pom</packaging>`)
- [ ] Criar módulo `pai/` (gerencia dependências)
- [ ] Criar módulo `dominio-principal/`
- [ ] Criar módulo `aplicacao/`
- [ ] Criar módulo `infraestrutura/`
- [ ] Criar módulo `apresentacao-backend/` (Spring Boot)

### 2. Dependências entre Módulos

- [ ] `apresentacao-backend` depende de `aplicacao` + `dominio-principal`
- [ ] `aplicacao` depende de `dominio-principal`
- [ ] `infraestrutura` depende de `aplicacao` + `dominio-principal`
- [ ] `apresentacao-backend` depende de `infraestrutura`

### 3. Estrutura de Pacotes

- [ ] Criar pacote base: `dev.com.{projeto}`
- [ ] Em cada módulo: `dev.com.{projeto}.{modulo}/principal/{entidade}/`
- [ ] Seguir padrão: Controller → ServicoAplicacao → Servico → Repositorio

### 4. Camada de Apresentação

- [ ] Criar `@RestController` com `@RequestMapping`
- [ ] Criar `BackendMapeador` para conversões
- [ ] Injetar `Servico` (domínio) e `ServicoAplicacao` (aplicação)
- [ ] Separar: GET → `ServicoAplicacao`, POST → `Servico`

### 5. Camada de Aplicação

- [ ] Criar `{Entidade}ServicoAplicacao` (consultas)
- [ ] Criar interface `{Entidade}RepositorioAplicacao`
- [ ] Criar DTOs de leitura (`{Entidade}Resumo`)

### 6. Camada de Domínio

- [ ] Criar entidade de domínio (`{Entidade}.java`)
- [ ] Criar Value Objects (`{Entidade}Id.java`, etc.)
- [ ] Criar `{Entidade}Servico` (lógica de negócio)
- [ ] Criar interface `{Entidade}Repositorio`

### 7. Camada de Infraestrutura

- [ ] Criar entidade JPA (`{Entidade}Jpa.java`) - package-private
- [ ] Criar `{Entidade}JpaRepository` (Spring Data)
- [ ] Criar `{Entidade}RepositorioImpl` implementando ambas interfaces
- [ ] Implementar métodos `toDomain()` e `toJpa()`

### 8. Configuração do Banco

- [ ] Criar `docker-compose.yml` (PostgreSQL)
- [ ] Criar `application.properties` (DataSource, JPA, Flyway)
- [ ] Adicionar dependências Maven (JPA, PostgreSQL, Flyway)
- [ ] Criar migrações Flyway em `infraestrutura/src/main/resources/db/migration/`

### 9. Configuração Spring Boot

- [ ] Criar classe `@SpringBootApplication`
- [ ] Configurar beans de serviços (`@Bean`)
- [ ] Spring descobre automaticamente `@Repository`

---

## 🎯 Padrões Arquiteturais Aplicados

### 1. **Layered Architecture (Arquitetura em Camadas)**
- Separação clara de responsabilidades
- Dependências unidirecionais (superior → inferior)

### 2. **Repository Pattern**
- Abstração da persistência
- Interfaces no domínio, implementação na infraestrutura

### 3. **Dependency Injection (Spring)**
- Inversão de controle
- Desacoplamento entre camadas

### 4. **CQRS (Command Query Responsibility Segregation)**
- Separação entre comandos (Servico) e consultas (ServicoAplicacao)
- Repositórios diferentes para leitura e escrita

### 5. **Value Objects**
- Tipos primitivos encapsulados (ex: `AlunoId`)
- Type safety e validação

---

## 🔑 Pontos-Chave para Replicação

1. **Separação de Módulos:** Cada camada é um módulo Maven independente
2. **Interfaces no Domínio:** Repositórios são interfaces, implementação na infraestrutura
3. **Conversão de Camadas:** Métodos `toDomain()` e `toJpa()` na infraestrutura
4. **Duas Interfaces de Repositório:** Uma para domínio (comandos), outra para aplicação (consultas)
5. **Spring Data JPA:** Usa interfaces que estendem `JpaRepository`
6. **Transações:** `@Transactional` nos métodos de repositório
7. **Flyway:** Migrações automáticas na inicialização

---

## 📚 Estrutura de Arquivos por Entidade

Para cada entidade (ex: `Aluno`), você terá:

```
apresentacao-backend/
└── apresentacao/principal/aluno/
    ├── AlunoControlador.java
    └── AlunoFormulario.java

aplicacao/
└── aplicacao/principal/aluno/
    ├── AlunoServicoAplicacao.java
    ├── AlunoRepositorioAplicacao.java
    └── AlunoResumo.java

dominio-principal/
└── dominio/principal/aluno/
    ├── Aluno.java
    ├── AlunoId.java
    ├── AlunoServico.java
    └── AlunoRepositorio.java

infraestrutura/
└── infraestrutura/persistencia/jpa/
    ├── AlunoJpa.java
    ├── AlunoJpaRepository.java
    └── AlunoRepositorioImpl.java
```

**Total:** ~10 arquivos por entidade (dependendo da complexidade)

---

## ✅ Resumo Executivo

| Camada | Responsabilidade | Tecnologia | Exemplo |
|--------|------------------|------------|---------|
| **Apresentação** | Controllers REST, DTOs | Spring MVC | `AlunoControlador` |
| **Aplicação** | Consultas, Resumos | Java POJO | `AlunoServicoAplicacao` |
| **Domínio** | Entidades, Lógica | Java POJO | `AlunoServico` |
| **Infraestrutura** | JPA, Persistência | Spring Data JPA | `AlunoRepositorioImpl` |
| **Banco** | Dados | PostgreSQL | Tabelas SQL |

**Fluxo:** Controller → ServicoAplicacao/Servico → Repositorio → JPA → PostgreSQL

---

**Documento criado para:** Aplicação em outros projetos  
**Foco:** Estrutura técnica, sem regras de negócio  
**Versão:** 1.0

