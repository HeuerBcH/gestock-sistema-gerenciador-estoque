# 📋 Estrutura do Back-End - QNota

Este documento descreve a arquitetura completa do back-end, desde os **Controllers** até a **conexão com o banco de dados**.

---

## 🏗️ Arquitetura em Camadas

O projeto segue uma **arquitetura em camadas** (Layered Architecture) com separação clara de responsabilidades:

```
┌─────────────────────────────────────────────────────────────┐
│  APRESENTAÇÃO (apresentacao-backend)                        │
│  - Controllers (REST)                                       │
│  - DTOs/Formulários                                         │
│  - Mapeadores (DTO ↔ Domínio)                               │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  APLICAÇÃO (aplicacao)                                       │
│  - Serviços de Aplicação (consultas/read)                   │
│  - Repositórios de Aplicação (interfaces)                   │
│  - Resumos/DTOs de leitura                                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  DOMÍNIO (dominio-principal)                                 │
│  - Entidades de Domínio                                      │
│  - Serviços de Domínio (lógica de negócio)                  │
│  - Repositórios de Domínio (interfaces)                     │
│  - Value Objects                                            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  INFRAESTRUTURA (infraestrutura)                             │
│  - Implementação JPA (entidades, repositórios)              │
│  - Configuração de banco de dados                           │
│  - Migrações Flyway                                         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  BANCO DE DADOS (PostgreSQL)                                 │
│  - Tabelas, relacionamentos, índices                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 1️⃣ CAMADA DE APRESENTAÇÃO (Controllers)

**Localização:** `apresentacao-backend/src/main/java/dev/com/qnota/apresentacao/principal/`

### 1.1 Controller (Exemplo: AlunoControlador)

```26:28:apresentacao-backend/src/main/java/dev/com/qnota/apresentacao/principal/aluno/AlunoControlador.java
@RestController
@RequestMapping("backend/aluno")
class AlunoControlador {
```

**Responsabilidades:**
- Recebe requisições HTTP (GET, POST)
- Valida entrada básica
- Converte DTOs da API para objetos de domínio
- Chama serviços de domínio ou aplicação
- Retorna DTOs de resposta

**Exemplo de Fluxo:**

```74:92:apresentacao-backend/src/main/java/dev/com/qnota/apresentacao/principal/aluno/AlunoControlador.java
	@RequestMapping(method = POST, path = "cadastrar")
	Integer cadastrar(@RequestBody AlunoFormulario.AlunoDto dto) {
		var turmaId = mapeador.map(dto.turmaId, TurmaId.class);
		var responsaveis = dto.responsaveis != null 
			? dto.responsaveis.stream().map(id -> mapeador.map(id, ResponsavelId.class)).toList()
			: List.<ResponsavelId>of();
		var principal = dto.responsavelPrincipalId != null 
			? mapeador.map(dto.responsavelPrincipalId, ResponsavelId.class)
			: null;

		var alunoId = alunoServico.cadastrar(
			dto.nome,
			dto.dataNascimento,
			turmaId,
			responsaveis,
			principal
		);
		return alunoId.value();
	}
```

**Componentes:**
- `@RestController`: Marca a classe como controller REST
- `@RequestMapping`: Define o path base do endpoint
- `@Autowired`: Injeta dependências (serviços, mapeadores)
- `BackendMapeador`: Converte entre tipos primitivos e Value Objects

### 1.2 Mapeador (BackendMapeador)

```18:88:apresentacao-backend/src/main/java/dev/com/qnota/apresentacao/BackendMapeador.java
@Component
public class BackendMapeador {

	/**
	 * Método genérico para mapear Integer -> Value Object ou Value Object -> Integer.
	 */
	@SuppressWarnings("unchecked")
	public <D> D map(Object source, Class<D> destinationType) {
		if (source == null) {
			return null;
		}

		// Integer -> Value Object
		if (source instanceof Integer integer) {
			if (destinationType == AlunoId.class) {
				return (D) new AlunoId(integer);
			}
			if (destinationType == ProfessorId.class) {
				return (D) new ProfessorId(integer);
			}
			if (destinationType == TurmaId.class) {
				return (D) new TurmaId(integer);
			}
			if (destinationType == SimuladoId.class) {
				return (D) new SimuladoId(integer);
			}
			if (destinationType == DisciplinaId.class) {
				return (D) new DisciplinaId(integer);
			}
			if (destinationType == ResponsavelId.class) {
				return (D) new ResponsavelId(integer);
			}
			if (destinationType == CoordenadorId.class) {
				return (D) new CoordenadorId(integer);
			}
			if (destinationType == RankingId.class) {
				return (D) new RankingId(integer);
			}
		}

		// Value Object -> Integer
		if (destinationType == Integer.class) {
			if (source instanceof AlunoId alunoId) {
				return (D) Integer.valueOf(alunoId.value());
			}
			if (source instanceof ProfessorId professorId) {
				return (D) Integer.valueOf(professorId.value());
			}
			if (source instanceof TurmaId turmaId) {
				return (D) Integer.valueOf(turmaId.value());
			}
			if (source instanceof SimuladoId simuladoId) {
				return (D) Integer.valueOf(simuladoId.value());
			}
			if (source instanceof DisciplinaId disciplinaId) {
				return (D) Integer.valueOf(disciplinaId.value());
			}
			if (source instanceof ResponsavelId responsavelId) {
				return (D) Integer.valueOf(responsavelId.value());
			}
			if (source instanceof CoordenadorId coordenadorId) {
				return (D) Integer.valueOf(coordenadorId.value());
			}
			if (source instanceof RankingId rankingId) {
				return (D) Integer.valueOf(rankingId.value());
			}
		}

		throw new IllegalArgumentException("Tipo de mapeamento não suportado: " + source.getClass() + " -> " + destinationType);
	}
}
```

**Função:** Converte entre tipos primitivos (Integer) e Value Objects do domínio.

---

## 2️⃣ CAMADA DE APLICAÇÃO (Serviços de Consulta)

**Localização:** `aplicacao/src/main/java/dev/com/qnota/aplicacao/principal/`

### 2.1 Serviço de Aplicação (Exemplo: AlunoServicoAplicacao)

```7:19:aplicacao/src/main/java/dev/com/qnota/aplicacao/principal/aluno/AlunoServicoAplicacao.java
public class AlunoServicoAplicacao {
	private AlunoRepositorioAplicacao repositorio;

	public AlunoServicoAplicacao(AlunoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");

		this.repositorio = repositorio;
	}

	public List<AlunoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
}
```

**Responsabilidades:**
- Operações de **leitura/consulta** (queries)
- Orquestração de consultas complexas
- Retorna DTOs de leitura (Resumos)

**Diferença entre Serviço de Aplicação e Serviço de Domínio:**
- **Serviço de Aplicação**: Focado em consultas e leitura
- **Serviço de Domínio**: Focado em comandos e lógica de negócio

### 2.2 Repositório de Aplicação (Interface)

```5:12:aplicacao/src/main/java/dev/com/qnota/aplicacao/principal/aluno/AlunoRepositorioAplicacao.java
public interface AlunoRepositorioAplicacao {
	List<AlunoResumo> pesquisarResumos();
	
	// TODO: Criar AlunoResumoExpandido quando necessário
	default List<AlunoResumo> pesquisarResumosExpandidos() {
		return pesquisarResumos();
	}
}
```

**Função:** Define contratos para consultas otimizadas (queries específicas).

---

## 3️⃣ CAMADA DE DOMÍNIO (Lógica de Negócio)

**Localização:** `dominio-principal/src/main/java/dev/com/qnota/dominio/principal/`

### 3.1 Serviço de Domínio (Exemplo: AlunoServico)

```17:32:dominio-principal/src/main/java/dev/com/qnota/dominio/principal/aluno/AlunoServico.java
public class AlunoServico implements ResponsavelVinculoService {

    private final AlunoRepositorio repo;
    private final ResponsavelRepositorio responsavelRepo;
    private final TurmaRepositorio turmaRepo;
    private final SimuladoRepositorio simuladoRepo;

    public AlunoServico(AlunoRepositorio repo,
                        ResponsavelRepositorio responsavelRepo,
                        TurmaRepositorio turmaRepo,
                        SimuladoRepositorio simuladoRepo) {
        this.repo = repo;
        this.responsavelRepo = responsavelRepo;
        this.turmaRepo = turmaRepo;
        this.simuladoRepo = simuladoRepo;
    }
```

**Responsabilidades:**
- **Orquestração** de operações de negócio
- **Validações** cross-aggregate
- **Aplicação de regras de negócio** (RN-*)
- Coordenação entre múltiplos agregados

**Exemplo de Operação:**

```34:46:dominio-principal/src/main/java/dev/com/qnota/dominio/principal/aluno/AlunoServico.java
    public AlunoId cadastrar(String nome,
                             LocalDate nascimento,
                             TurmaId turma,
                             List<ResponsavelId> responsaveis,
                             ResponsavelId principal) {

        validarCadastro(nome, nascimento, turma, responsaveis, principal);
        validarCadastroResponsaveis(responsaveis, principal);

        var aluno = new Aluno(nome, nascimento, true, turma, responsaveis, principal);
        return repo.salvar(aluno);
    }
```

### 3.2 Repositório de Domínio (Interface)

**Localização:** `dominio-principal/src/main/java/dev/com/qnota/dominio/principal/aluno/AlunoRepositorio.java`

**Função:** Define o contrato de persistência do agregado, independente da tecnologia.

---

## 4️⃣ CAMADA DE INFRAESTRUTURA (Persistência JPA)

**Localização:** `infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/`

### 4.1 Entidades JPA

```53:77:infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/AlunoJpa.java
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

    @Column(nullable = false)
    Boolean ativo;

    @Column(name = "turma_id", nullable = false)
    Integer turmaId;

    @OneToMany(mappedBy = "aluno", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    Set<AlunoResponsavelJpa> responsaveis = new LinkedHashSet<>();

    @OneToMany(mappedBy = "aluno", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    Set<NotaAlunoJpa> notas = new LinkedHashSet<>();
}
```

**Características:**
- Classes **package-private** (não expostas fora do pacote)
- Mapeamento direto para tabelas do banco
- Anotações JPA (`@Entity`, `@Table`, `@Column`, etc.)
- Relacionamentos JPA (`@OneToMany`, `@ManyToOne`, etc.)

### 4.2 Repositórios Spring Data JPA

```201:236:infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/AlunoJpa.java
interface AlunoJpaRepository extends JpaRepository<AlunoJpa, Integer> {

    @Query(value = """
        select exists(
          select 1 from alunos a
          where lower(a.nome) = lower(:nome)
            and a.datanascimento = :data
            and a.turma_id = :turmaId
        )
        """, nativeQuery = true)
    boolean existsHomonimoMesmoNascimentoNaTurma(@Param("nome") String nome,
                                                 @Param("data") LocalDate data,
                                                 @Param("turmaId") int turmaId);

    List<AlunoJpa> findByTurmaId(Integer turmaId);

    @Modifying
    @Query(value = "update alunos set turma_id = :nova where id = :alunoId", nativeQuery = true)
    int alterarTurma(@Param("alunoId") int alunoId, @Param("nova") int novaTurmaId);

    // Query para resumos com informações da turma e quantidade de responsáveis
    @Query(value = """
        SELECT a.id AS id,
               a.nome AS nome,
               a.datanascimento AS dataNascimento,
               a.ativo AS ativo,
               a.turma_id AS turmaId,
               t.nome AS turmaNome,
               COUNT(ar.responsavel_id) AS quantidadeResponsaveis
          FROM alunos a
     LEFT JOIN turmas t ON t.id = a.turma_id
     LEFT JOIN aluno_responsaveis ar ON ar.aluno_id = a.id
      GROUP BY a.id, a.nome, a.datanascimento, a.ativo, a.turma_id, t.nome
      ORDER BY a.nome
        """, nativeQuery = true)
    List<AlunoResumo> findAlunoResumoByOrderByNome();
}
```

**Características:**
- Estendem `JpaRepository<Entidade, ID>`
- Métodos de query automáticos (ex: `findByTurmaId`)
- Queries nativas com `@Query`
- Queries de modificação com `@Modifying`

### 4.3 Implementação do Repositório de Domínio

```255:269:infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/AlunoJpa.java
@Repository
class AlunoRepositorioImpl implements AlunoRepositorio, AlunoRepositorioAplicacao {

    private final AlunoJpaRepository alunoRepo;
    private final AlunoResponsavelJpaRepository respRepo;
    private final NotaAlunoJpaRepository notaRepo;

    @Autowired
    AlunoRepositorioImpl(AlunoJpaRepository alunoRepo,
                          AlunoResponsavelJpaRepository respRepo,
                          NotaAlunoJpaRepository notaRepo) {
        this.alunoRepo = alunoRepo;
        this.respRepo = respRepo;
        this.notaRepo = notaRepo;
    }
```

**Responsabilidades:**
- **Implementa** `AlunoRepositorio` (domínio) e `AlunoRepositorioAplicacao` (aplicação)
- **Converte** entre entidades JPA e agregados de domínio
- **Delega** operações para Spring Data JPA
- **Gerencia** transações com `@Transactional`

**Métodos de Conversão:**

```317:335:infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/AlunoJpa.java
    private static Aluno toDomain(AlunoJpa j) {
        var listaResp = responsaveisDe(j.responsaveis);
        var principal = principalDe(j.responsaveis);

        var aluno = new Aluno(
                j.nome,
                j.dataNascimento,
                Boolean.TRUE.equals(j.ativo),
                new TurmaId(j.turmaId),
                listaResp,
                principal
        );
        if (j.id != null) aluno.atribuirIdSeAusente(new AlunoId(j.id));

        if (j.notas != null && !j.notas.isEmpty()) {
            hidratarNotas(aluno, j.notas);
        }
        return aluno;
    }
```

```401:418:infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/AlunoJpa.java
    private AlunoJpa toJpa(Aluno d) {
        final AlunoJpa j;
        if (d.getId() == null) {
            j = new AlunoJpa();
        } else {
            j = alunoRepo.findById(d.getId().value())
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + d.getId().value()));
        }
        j.nome = d.getNome();
        j.dataNascimento = d.getDataNascimento();
        j.ativo = d.isAtivo();
        j.turmaId = d.getTurma().value();

        preencherVinculosJpa(j, d);
        preencherNotasJpa(j, d.getNotas());

        return j;
    }
```

**Operações de Persistência:**

```422:431:infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/AlunoJpa.java
    @Override
    @Transactional
    public AlunoId salvar(Aluno aluno) {
        var j = toJpa(aluno);
        j = alunoRepo.save(j);
        if (aluno.getId() == null) {
            aluno.atribuirIdSeAusente(new AlunoId(j.id));
        }
        return new AlunoId(j.id);
    }
```

```434:439:infraestrutura/src/main/java/dev/com/qnota/infraestrutura/persistencia/jpa/AlunoJpa.java
    @Override
    @Transactional(readOnly = true)
    public Aluno porId(AlunoId id) {
        var j = alunoRepo.findById(id.value())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + id.value()));
        return toDomain(j);
    }
```

---

## 5️⃣ CONFIGURAÇÃO DO BANCO DE DADOS

### 5.1 Docker Compose (PostgreSQL)

```1:22:docker-compose.yml
services:
  postgres:
    container_name: qnota-postgres
    image: postgres:17
    environment:
      POSTGRES_DB: qnota
      POSTGRES_USER: qnota
      POSTGRES_PASSWORD: qnota
    volumes:
      - postgres:/var/lib/postgresql/data
    ports:
      - 5433:5432
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U qnota"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres:
```

**Configuração:**
- **Imagem:** PostgreSQL 17
- **Porta:** 5433 (host) → 5432 (container)
- **Database:** `qnota`
- **Usuário/Senha:** `qnota/qnota`

### 5.2 Application Properties (Desenvolvimento)

**Localização:** `apresentacao-backend/src/test/resources/application-desenvolvimento.properties`

```1:15:apresentacao-backend/src/test/resources/application-desenvolvimento.properties
# Configurações de desenvolvimento - PostgreSQL com Docker
# Container Docker: docker compose up -d

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.physical_naming_strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5433/qnota
spring.datasource.username=qnota
spring.datasource.password=qnota

spring.flyway.schemas=qnota
spring.flyway.baseline-on-migrate=true
```

**Configurações Importantes:**
- **JPA:** Hibernate com dialeto PostgreSQL
- **DataSource:** URL, usuário e senha
- **Flyway:** Migrações automáticas de schema

### 5.3 Dependências (pom.xml)

**Localização:** `infraestrutura/pom.xml`

```16:48:infraestrutura/pom.xml
	<dependencies>
		<dependency>
		    <groupId>org.flywaydb</groupId>
		    <artifactId>flyway-core</artifactId>
		</dependency>

		<dependency>
		    <groupId>org.modelmapper</groupId>
		    <artifactId>modelmapper</artifactId>
		</dependency>		

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		
		<dependency>
			<groupId>org.flywaydb</groupId>
			<artifactId>flyway-database-postgresql</artifactId>
		</dependency>

		<dependency>
			<groupId>${project.groupId}</groupId>
			<artifactId>qnota-aplicacao</artifactId>
			<version>${project.version}</version>
		</dependency>
		
		<!-- Dependência de banco de dados PostgreSQL -->
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
	</dependencies>
```

**Dependências Principais:**
- `spring-boot-starter-data-jpa`: Spring Data JPA
- `postgresql`: Driver JDBC do PostgreSQL
- `flyway-core` e `flyway-database-postgresql`: Migrações de banco

### 5.4 Migrações Flyway

**Localização:** `infraestrutura/src/main/resources/db/migration/`

- `V1__QNota_criacao.sql`: Criação inicial do schema
- `V2__QNota_Povoamento.sql`: Dados iniciais (se houver)

**Flyway** executa automaticamente as migrações na inicialização da aplicação.

---

## 6️⃣ INJEÇÃO DE DEPENDÊNCIAS (Spring Boot)

**Localização:** `apresentacao-backend/src/main/java/dev/com/qnota/AplicacaoBackend.java`

### 6.1 Configuração de Beans

```51:224:apresentacao-backend/src/main/java/dev/com/qnota/AplicacaoBackend.java
@SpringBootApplication
public class AplicacaoBackend {

	// ===== HashService (para Coordenador) =====
	@Bean
	public HashService hashService(PasswordEncoder passwordEncoder) {
		return new HashService() {
			@Override
			public String hash(String rawPassword) {
				return passwordEncoder.encode(rawPassword);
			}

			@Override
			public boolean matches(String rawPassword, String hashedPassword) {
				return passwordEncoder.matches(rawPassword, hashedPassword);
			}
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// ===== Serviços de Domínio =====

	@Bean
	public CoordenadorServico coordenadorServico(CoordenadorRepositorio repositorio, HashService hashService) {
		return new CoordenadorServico(repositorio, hashService);
	}

	@Bean
	public ResponsavelServico responsavelServico(ResponsavelRepositorio repositorio, @Qualifier("responsavelVinculoService") ResponsavelVinculoService vinculoService) {
		return new ResponsavelServico(repositorio, vinculoService);
	}

	@Bean
	public ProfessorServico professorServico(ProfessorRepositorio repositorio,
	                                         AlunoRepositorio alunoRepositorio,
	                                         SimuladoRepositorio simuladoRepositorio) {
		return new ProfessorServico(repositorio, alunoRepositorio, simuladoRepositorio);
	}

	@Bean
	public DisciplinaServico disciplinaServico(DisciplinaRepositorio repositorio) {
		return new DisciplinaServico(repositorio);
	}

	@Bean
	public TurmaServico turmaServico(TurmaRepositorio repositorio, ProfessorRepositorio professorRepositorio) {
		return new TurmaServico(repositorio, professorRepositorio);
	}

	@Bean
	public AlunoServico alunoServico(AlunoRepositorio repositorio,
	                                 ResponsavelRepositorio responsavelRepositorio,
	                                 TurmaRepositorio turmaRepositorio,
	                                 SimuladoRepositorio simuladoRepositorio) {
		return new AlunoServico(repositorio, responsavelRepositorio, turmaRepositorio, simuladoRepositorio);
	}

	@Bean
	public NotaServico notaServico(AlunoRepositorio alunoRepositorio,
	                               SimuladoRepositorio simuladoRepositorio,
	                               TurmaRepositorio turmaRepositorio,
	                               DisciplinaRepositorio disciplinaRepositorio) {
		return new NotaServico(alunoRepositorio, simuladoRepositorio, turmaRepositorio, disciplinaRepositorio);
	}

	@Bean
	public CalculoRankingStrategy calculoRankingStrategy(NotaServico notaServico) {
		return new CalculoRankingMediaPonderada(notaServico);
	}

	@Bean
	public RankingServico rankingServico(AlunoRepositorio alunoRepositorio,
	                                     SimuladoRepositorio simuladoRepositorio,
	                                     RankingRepositorio rankingRepositorio,
	                                     CalculoRankingStrategy calculoRankingStrategy) {
		return new RankingServico(alunoRepositorio, simuladoRepositorio, rankingRepositorio, calculoRankingStrategy);
	}

	// ===== Auditoria + Decorator para SimuladoRepositorio (Padrao Decorator) =====
	// 
	// O padrao Decorator e aplicado aqui para adicionar comportamento de auditoria
	// ao SimuladoRepositorio sem modificar sua implementacao base.
	//
	// Fluxo:
	// 1. SimuladoServico chama metodos do SimuladoRepositorio (interface)
	// 2. O Decorator intercepta essas chamadas e registra eventos de auditoria
	// 3. O Decorator delega para o repositorio real (JPA)
	// 4. Os eventos ficam disponiveis via /backend/auditoria/eventos
	//
	// Beneficios:
	// - Separacao de responsabilidades (auditoria desacoplada da persistencia)
	// - Open/Closed: adiciona auditoria sem modificar codigo existente
	// - Testabilidade: pode testar com ou sem auditoria

	@Bean
	public SimuladoAuditoriaArmazenada simuladoAuditoria() {
		// Usa a implementacao que armazena eventos em memoria
		// Os eventos sao expostos via AuditoriaControlador
		return new SimuladoAuditoriaArmazenada();
	}

	@Bean
	public SimuladoServico simuladoServico(SimuladoRepositorio repositorio,
	                                       RankingServico rankingServico,
	                                       TurmaRepositorio turmaRepositorio,
	                                       ProfessorRepositorio professorRepositorio,
	                                       DisciplinaRepositorio disciplinaRepositorio,
	                                       AlunoRepositorio alunoRepositorio,
	                                       SimuladoAuditoriaArmazenada simuladoAuditoria) {

		// Envolve o repositorio real com o Decorator para adicionar auditoria
		SimuladoRepositorio decorator = new SimuladoRepositorioDecorator(repositorio, simuladoAuditoria);

		return new SimuladoServico(decorator, rankingServico,
		                           turmaRepositorio, professorRepositorio,
		                           disciplinaRepositorio, alunoRepositorio);
	}

	// ===== ResponsavelVinculoService (implementado por AlunoServico) =====
	@Bean
	public ResponsavelVinculoService responsavelVinculoService(AlunoServico alunoServico) {
		return alunoServico; // AlunoServico implementa ResponsavelVinculoService
	}

	// ===== Serviços de Aplicação =====

	@Bean
	public CoordenadorServicoAplicacao coordenadorServicoAplicacao(CoordenadorRepositorioAplicacao repositorio) {
		return new CoordenadorServicoAplicacao(repositorio);
	}

	@Bean
	public ResponsavelServicoAplicacao responsavelServicoAplicacao(ResponsavelRepositorioAplicacao repositorio) {
		return new ResponsavelServicoAplicacao(repositorio);
	}

	@Bean
	public ProfessorServicoAplicacao professorServicoAplicacao(ProfessorRepositorioAplicacao repositorio) {
		return new ProfessorServicoAplicacao(repositorio);
	}

	@Bean
	public DisciplinaServicoAplicacao disciplinaServicoAplicacao(DisciplinaRepositorioAplicacao repositorio) {
		return new DisciplinaServicoAplicacao(repositorio);
	}

	@Bean
	public TurmaServicoAplicacao turmaServicoAplicacao(TurmaRepositorioAplicacao repositorio) {
		return new TurmaServicoAplicacao(repositorio);
	}

	@Bean
	public AlunoServicoAplicacao alunoServicoAplicacao(AlunoRepositorioAplicacao repositorio) {
		return new AlunoServicoAplicacao(repositorio);
	}

	@Bean
	public SimuladoServicoAplicacao simuladoServicoAplicacao(SimuladoRepositorioAplicacao repositorio) {
		return new SimuladoServicoAplicacao(repositorio);
	}

	@Bean
	public RankingServicoAplicacao rankingServicoAplicacao(RankingRepositorioAplicacao repositorio) {
		return new RankingServicoAplicacao(repositorio);
	}

	public static void main(String[] args) {
		run(AplicacaoBackend.class, args);
	}
}
```

**Função:** Configura todos os beans do Spring, conectando as camadas através de injeção de dependências.

---

## 7️⃣ FLUXO COMPLETO: Requisição → Banco de Dados

### Exemplo: Cadastrar Aluno

```
1. HTTP POST /backend/aluno/cadastrar
   ↓
2. AlunoControlador.cadastrar()
   - Recebe AlunoFormulario.AlunoDto
   - Converte DTOs para Value Objects (via BackendMapeador)
   ↓
3. AlunoServico.cadastrar()
   - Valida regras de negócio
   - Cria agregado Aluno
   ↓
4. AlunoRepositorio.salvar()
   - Interface do domínio
   ↓
5. AlunoRepositorioImpl.salvar()
   - Converte Aluno → AlunoJpa (toJpa)
   ↓
6. AlunoJpaRepository.save()
   - Spring Data JPA
   ↓
7. Hibernate/JPA
   - Gera SQL INSERT
   ↓
8. PostgreSQL (JDBC)
   - Executa SQL
   - Retorna ID gerado
   ↓
9. Resposta HTTP
   - ID do aluno cadastrado
```

### Exemplo: Pesquisar Alunos

```
1. HTTP GET /backend/aluno/pesquisa
   ↓
2. AlunoControlador.pesquisa()
   ↓
3. AlunoServicoAplicacao.pesquisarResumos()
   ↓
4. AlunoRepositorioAplicacao.pesquisarResumos()
   - Interface de aplicação
   ↓
5. AlunoRepositorioImpl.pesquisarResumos()
   - Implementação JPA
   ↓
6. AlunoJpaRepository.findAlunoResumoByOrderByNome()
   - Query nativa SQL otimizada
   ↓
7. PostgreSQL
   - Executa SELECT com JOINs
   - Retorna AlunoResumo (projeção)
   ↓
8. Resposta HTTP
   - Lista de AlunoResumoDto
```

---

## 8️⃣ PADRÕES E PRÁTICAS

### 8.1 Separação de Responsabilidades
- **Controller**: Apenas HTTP
- **Serviço de Aplicação**: Consultas
- **Serviço de Domínio**: Lógica de negócio
- **Repositório**: Persistência

### 8.2 Conversão de Camadas
- **DTO ↔ Value Object**: `BackendMapeador`
- **Agregado ↔ JPA**: Métodos `toDomain()` e `toJpa()`

### 8.3 Transações
- `@Transactional`: Em métodos de repositório
- `readOnly = true`: Para consultas

### 8.4 Queries Otimizadas
- **Domínio**: Carrega agregados completos
- **Aplicação**: Queries específicas (projeções, JOINs)

---

## 9️⃣ RESUMO DA ARQUITETURA

| Camada | Responsabilidade | Tecnologia |
|--------|------------------|------------|
| **Apresentação** | Controllers REST, DTOs | Spring MVC |
| **Aplicação** | Serviços de consulta, Resumos | Java POJO |
| **Domínio** | Entidades, Serviços de negócio, Value Objects | Java POJO |
| **Infraestrutura** | JPA, Spring Data, Conversões | Spring Data JPA, Hibernate |
| **Banco de Dados** | Persistência | PostgreSQL |

---

## 🔟 PRÓXIMOS PASSOS

Para entender melhor:
1. Explore os outros controllers em `apresentacao-backend/`
2. Veja as entidades JPA em `infraestrutura/`
3. Analise as migrações Flyway em `infraestrutura/src/main/resources/db/migration/`
4. Estude os serviços de domínio em `dominio-principal/`

---

**Documento criado em:** 2025-01-27  
**Versão:** 1.0

