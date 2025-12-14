# Como Rodar o Projeto Gestock

## Pré-requisitos

- **Java 17+** (JDK)
- **Maven 3.8+**
- **Docker** e **Docker Compose**

Verificar versões:
```bash
java -version
mvn -version
docker --version
```

---

## Compilar o Projeto

Na pasta backend:

```bash
mvn clean install -DskipTests
```

Isso compila todos os módulos Maven e gera os JARs.

---

## Subir o Banco de Dados

```bash
docker compose up -d
```

Verificar se está rodando:
```bash
docker ps
```

**Configuração do banco:**
- Host: `localhost`
- Porta: `5433`
- Database: `gestock`
- Usuário: `gestock`
- Senha: `gestock`

---

## Rodar o Backend

### Opção 1: Via Maven (Recomendado)

```bash
cd apresentacao-backend
mvn test-compile exec:java "-Dexec.mainClass=dev.gestock.sge.BackendDesenvolvimentoAplicacao" "-Dexec.classpathScope=test"
```

### Opção 2: Via IDE

Abra e execute a classe:
`apresentacao-backend/src/test/java/dev/gestock/sge/BackendDesenvolvimentoAplicacao.java`

**Backend disponível em:** http://localhost:8080

**Swagger UI:** http://localhost:8080/swagger-ui.html

---

## Rodar o Frontend

Em outra instância do terminal

```bash
cd frontend
npm install
npm run dev
```

---
## Rodar os Testes

### Todos os testes:
```bash
mvn test
```

### Testes do domínio (Cucumber BDD):
```bash
mvn test -pl dominio-principal
```

### Testes de um módulo específico:
```bash
mvn test -pl infraestrutura
mvn test -pl aplicacao
mvn test -pl apresentacao-backend
```

**Relatórios de teste:** `dominio-principal/target/surefire-reports/`

---

## Parar os Serviços

**Backend:** Pressione `Ctrl+C` no terminal

**Banco de dados:**
```bash
docker compose down
```

**Resetar banco (remove todos os dados):**
```bash
docker compose down -v
```

---

## 🔧 Troubleshooting Rápido

**Erro de compilação:**
```bash
mvn clean install -DskipTests
```

**Erro de conexão com banco:**
```bash
docker compose up -d
docker logs gestock-database
```

**Porta 8080 em uso (Windows PowerShell):**
```powershell
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess
Stop-Process -Id <PID> -Force
```

**Porta 8080 em uso (Linux/Mac):**
```bash
lsof -ti:8080 | xargs kill -9
```
