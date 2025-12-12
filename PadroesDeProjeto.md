# Padrões de Projeto - Gestock

Este documento descreve os padrões de projeto implementados no sistema Gestock, explicando o contexto, a implementação e os benefícios de cada padrão.

---

## 1. Padrão Decorator

### Contexto no Domínio

O padrão Decorator foi aplicado para adicionar funcionalidade de **auditoria** aos repositórios de domínio sem modificar suas implementações base. No contexto do Gestock, estoques, produtos e fornecedores são entidades críticas que passam por diversas operações (criação, atualização, consulta) e precisam de rastreabilidade para fins de conformidade, debugging e análise de uso.

### Por que este padrão faz sentido no domínio?

- **Requisitos de auditoria**: Em sistemas de gestão de estoque, é fundamental rastrear quem criou, modificou ou consultou estoques, produtos e fornecedores
- **Separação de responsabilidades**: A lógica de persistência não deve misturar-se com lógica de auditoria
- **Extensibilidade futura**: Outros comportamentos transversais podem ser adicionados (cache, validação adicional, notificações) sem modificar o repositório base
- **Open/Closed Principle**: Os repositórios originais permanecem fechados para modificação, mas abertos para extensão
- **Transparência**: Os serviços de domínio recebem um repositório e não precisam saber se é decorado ou não

### Classes Criadas

#### 1.1. EstoqueRepositorioDecorator

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/EstoqueRepositorioDecorator.java`
- **Responsabilidade**: Envolve um `EstoqueRepositorio` real (alvo) e adiciona comportamento de auditoria antes de delegar as operações
- **Implementa**: Interface `EstoqueRepositorio` (mantém o mesmo contrato)
- **Padrão**: Decorator clássico - envolve um objeto e adiciona comportamento sem alterar sua interface
- **Diferença para Proxy**: Decorator adiciona funcionalidade; Proxy controla acesso (lazy loading, segurança)

#### 1.2. EstoqueAuditoria (Interface)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/EstoqueAuditoria.java`
- **Responsabilidade**: Define o contrato para registrar eventos de auditoria
- **Métodos**: 
  - `registrarSalvar(Estoque s)` - Registra quando um estoque é salvo
  - `registrarLeitura(EstoqueId id)` - Registra quando um estoque é buscado por ID
  - `registrarBuscaPorCliente(ClienteId clienteId)` - Registra quando estoques são buscados por cliente

#### 1.3. EstoqueAuditoriaConsole (Implementação)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/EstoqueAuditoriaConsole.java`
- **Responsabilidade**: Implementação concreta que registra eventos no console com timestamp
- **Implementação atual**: Usa `System.out.printf` para logging simples
- **Extensibilidade**: Pode ser substituída por implementação que grava em banco, arquivo ou serviço externo

#### 1.4. ProdutoRepositorioDecorator

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/produto/ProdutoRepositorioDecorator.java`
- **Responsabilidade**: Adiciona auditoria ao `ProdutoRepositorio`
- **Operações auditadas**:
  - `salvar(Produto)` - Criação/atualização de produtos
  - `buscarPorId(ProdutoId)` - Consulta por ID
  - `buscarPorCodigo(CodigoProduto)` - Consulta por código único
  - `inativar(Produto)` - Inativação de produtos

#### 1.5. ProdutoAuditoria e ProdutoAuditoriaConsole

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/produto/`
- **Responsabilidade**: Define e implementa a estratégia de auditoria para produtos

#### 1.6. FornecedorRepositorioDecorator

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/fornecedor/FornecedorRepositorioDecorator.java`
- **Responsabilidade**: Adiciona auditoria ao `FornecedorRepositorio`
- **Operações auditadas**:
  - `salvar(Fornecedor)` - Criação/atualização de fornecedores
  - `buscarPorId(FornecedorId)` - Consulta por ID
  - `buscarPorCnpj(String)` - Consulta por CNPJ único

#### 1.7. FornecedorAuditoria e FornecedorAuditoriaConsole

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/fornecedor/`
- **Responsabilidade**: Define e implementa a estratégia de auditoria para fornecedores

### Como Foi Aplicado

O decorator intercepta as seguintes operações em cada repositório:

**EstoqueRepositorio:**
- `salvar(Estoque estoque)`: Registra auditoria antes de delegar ao repositório alvo
- `buscarPorId(EstoqueId id)`: Registra auditoria antes de delegar ao repositório alvo
- `buscarEstoquesPorClienteId(ClienteId clienteId)`: Registra auditoria antes de delegar ao repositório alvo

**ProdutoRepositorio:**
- `salvar(Produto produto)`: Registra auditoria antes de delegar
- `buscarPorId(ProdutoId id)`: Registra auditoria antes de delegar
- `buscarPorCodigo(CodigoProduto codigo)`: Registra auditoria antes de delegar
- `inativar(Produto produto)`: Registra auditoria antes de delegar

**FornecedorRepositorio:**
- `salvar(Fornecedor fornecedor)`: Registra auditoria antes de delegar
- `buscarPorId(FornecedorId id)`: Registra auditoria antes de delegar
- `buscarPorCnpj(String cnpj)`: Registra auditoria antes de delegar

### Configuração no Spring Boot

Quando o `AplicacaoBackend.java` estiver ativo, o decorator pode ser configurado como bean, envolvendo o repositório JPA:

```java
@Bean
public EstoqueRepositorio estoqueRepositorio(EstoqueRepositorioImpl repositorio) {
    var auditoria = new EstoqueAuditoriaConsole();
    return new EstoqueRepositorioDecorator(repositorio, auditoria);
}

@Bean
public ProdutoRepositorio produtoRepositorio(ProdutoRepositorioImpl repositorio) {
    var auditoria = new ProdutoAuditoriaConsole();
    return new ProdutoRepositorioDecorator(repositorio, auditoria);
}

@Bean
public FornecedorRepositorio fornecedorRepositorio(FornecedorRepositorioImpl repositorio) {
    var auditoria = new FornecedorAuditoriaConsole();
    return new FornecedorRepositorioDecorator(repositorio, auditoria);
}
```

### Estrutura do Padrão

```
┌─────────────────────────────────────────────────────────────┐
│                    EstoqueServico                           │
│  (usa EstoqueRepositorio - não sabe que é decorado)         │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│          EstoqueRepositorioDecorator                        │
│  - Implementa EstoqueRepositorio                            │
│  - Mantém referência ao repositório alvo                    │
│  - Mantém referência à auditoria                             │
│  - Intercepta chamadas e adiciona auditoria                  │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
        ▼                             ▼
┌──────────────────┐        ┌──────────────────────┐
│ EstoqueAuditoria │        │ EstoqueRepositorio    │
│   (interface)    │        │   (interface)         │
└────────┬─────────┘        └──────────┬───────────┘
         │                               │
         ▼                               ▼
┌──────────────────┐        ┌──────────────────────┐
│EstoqueAuditoria  │        │EstoqueRepositorioImpl│
│    Console       │        │   (implementação)    │
└──────────────────┘        └──────────────────────┘
```

### Benefícios Específicos no Domínio Gestock

1. **Separação de responsabilidades**: A lógica de auditoria não polui os repositórios de infraestrutura (`EstoqueRepositorioImpl`, `ProdutoRepositorioImpl`, etc.)

2. **Testabilidade**: Os serviços de domínio (`EstoqueServico`, `ProdutoServico`, etc.) podem ser testados com ou sem auditoria, usando diferentes implementações

3. **Extensibilidade**: Outros decorators podem ser empilhados (ex.: cache → auditoria → repositório)

4. **Transparência**: Os serviços recebem um repositório e não precisam saber se é decorado ou não

5. **Conformidade**: Facilita auditoria de conformidade (LGPD, requisitos de rastreabilidade) sem alterar código de negócio

6. **Flexibilidade**: A estratégia de auditoria pode ser trocada facilmente (console → banco de dados → serviço externo) sem modificar o decorator

7. **Open/Closed Principle**: Os repositórios originais permanecem fechados para modificação, mas abertos para extensão via decorators

### Exemplo de Uso

```java
// Criação do repositório base (JPA)
EstoqueRepositorioImpl repositorioJpa = new EstoqueRepositorioImpl(...);

// Criação da estratégia de auditoria
EstoqueAuditoria auditoria = new EstoqueAuditoriaConsole();

// Criação do decorator
EstoqueRepositorio repositorioDecorado = 
    new EstoqueRepositorioDecorator(repositorioJpa, auditoria);

// Uso no serviço (transparente)
EstoqueServico servico = new EstoqueServico(repositorioDecorado);

// Quando salvar é chamado, a auditoria é registrada automaticamente
servico.cadastrar(estoque);
// Output: [AUDITORIA ESTOQUE] 2024-01-15 10:30:45 | SALVAR | EstoqueId=1 | Nome=Estoque Principal | ClienteId=1
```

### Extensibilidade Futura

O padrão permite adicionar facilmente:

1. **Auditoria em Banco de Dados**: Criar `EstoqueAuditoriaDatabase` que grava em tabela de auditoria
2. **Auditoria em Arquivo**: Criar `EstoqueAuditoriaFile` que grava em arquivo de log
3. **Auditoria em Serviço Externo**: Criar `EstoqueAuditoriaService` que envia eventos para sistema externo
4. **Múltiplos Decorators**: Empilhar cache + auditoria + validação
5. **Auditoria Condicional**: Adicionar lógica para auditar apenas operações específicas

### Diferenças entre Decorator e Outros Padrões

**Decorator vs Proxy:**
- **Decorator**: Adiciona funcionalidade (comportamento adicional)
- **Proxy**: Controla acesso (lazy loading, segurança, cache)

**Decorator vs Adapter:**
- **Decorator**: Mantém a mesma interface, adiciona comportamento
- **Adapter**: Muda a interface para compatibilidade

**Decorator vs Strategy:**
- **Decorator**: Envolve objeto e adiciona comportamento
- **Strategy**: Troca algoritmo/comportamento interno

### Conclusão

O padrão Decorator foi implementado com sucesso no Gestock para adicionar auditoria aos repositórios de forma não invasiva. A implementação segue os princípios SOLID, especialmente o Open/Closed Principle, permitindo extensão sem modificação do código existente. O padrão é facilmente extensível e pode ser aplicado a outros repositórios conforme necessário.

---

## 2. Padrão Observer

### Contexto no Domínio

O padrão Observer foi aplicado para permitir que componentes reajam à **atualização de estoques** de forma desacoplada. No Gestock, quando um estoque é atualizado, o sistema precisa **remover alertas automaticamente** se o estoque físico ficou acima do ROP (R1H17: Alerta removido automaticamente após recebimento). Sem o Observer, haveria acoplamento direto entre `EstoqueServico` e `AlertaServico`.

### Por que este padrão faz sentido no domínio?

- **Desacoplamento**: `EstoqueServico` não deve conhecer `AlertaServico` ou outras reações à atualização
- **Extensibilidade**: Novos comportamentos podem reagir à atualização (envio de email, geração de relatório, notificação de fornecedores) sem modificar o código de atualização
- **Single Responsibility**: Cada observer tem UMA responsabilidade específica
- **Event-driven**: A atualização de estoque é um **evento de domínio** que dispara reações em cascata
- **Conformidade com DDD**: Agregados não devem referenciar diretamente outros agregados; comunicação via eventos é preferível

### Classes Criadas/Modificadas

#### `EstoqueObserver` (CRIADA - Interface)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/EstoqueObserver.java`
- **Responsabilidade**: Define o contrato para observadores de eventos relacionados a Estoque
- **Método principal**:
  ```java
  void aoAtualizarEstoque(EstoqueId id);
  ```
- **Extensível**: Pode adicionar outros métodos para outros eventos:
  - `aoInativarEstoque(EstoqueId id)`
  - `aoTransferirProduto(EstoqueId origem, EstoqueId destino, ProdutoId produtoId)`

#### `AtualizacaoEstoqueTemplate` (CRIADA - Subject)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/AtualizacaoEstoqueTemplate.java`
- **Papel duplo**: Template Method (define fluxo) + Subject do Observer (notifica observers)
- **Modificação**: Adicionada lista de observers e métodos de gerenciamento:
  ```java
  private final List<EstoqueObserver> observers = new ArrayList<>();
  
  public void registrarObserver(EstoqueObserver observer) {
      observers.add(observer);
  }
  
  protected void notificarObservers(EstoqueId id) {
      for (EstoqueObserver observer : observers) {
          observer.aoAtualizarEstoque(id);
      }
  }
  ```
- **Notificação**: Ocorre no passo 5 do template method, após salvar o estoque atualizado

#### `AtualizacaoEstoquePadrao` (CRIADA - Implementação Concreta)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/AtualizacaoEstoquePadrao.java`
- **Responsabilidade**: Implementa os métodos abstratos do template usando o repositório
- **Implementação**: Usa `EstoqueRepositorio` para carregar e salvar estoques

#### `AlertaServico` (MODIFICADO - Observer Concreto)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/alerta/AlertaServico.java`
- **Modificação**: Agora implementa `EstoqueObserver`
- **Implementação**:
  ```java
  public class AlertaServico implements EstoqueObserver {
      @Override
      public void aoAtualizarEstoque(EstoqueId id) {
          removerAlertasSeNecessario(id); // R1H17
      }
  }
  ```
- **Responsabilidade**: Reage à atualização de estoque removendo alertas automaticamente
- **Outros observers possíveis** (não implementados):
  - `NotificacaoEmailObserver`: Envia emails quando estoque é atualizado
  - `RelatorioObserver`: Gera relatório de atualização de estoque
  - `LogObserver`: Registra evento de atualização em log estruturado

### Como Foi Aplicado

**Antes** (acoplamento direto):
```java
public class EstoqueServico {
    private final AlertaRepositorio alertaRepositorio; // acoplamento!
    
    public void atualizar(Estoque estoque) {
        // ... validações e atualização
        removerAlertasSeNecessario(estoque); // chamada direta!
    }
    
    private void removerAlertasSeNecessario(Estoque estoque) {
        // lógica de remoção de alertas acoplada
    }
}
```
❌ **Problema**: `EstoqueServico` precisa conhecer `AlertaRepositorio` e futuros serviços que reajam à atualização

**Depois** (usando Observer):
```java
// 1. AlertaServico implementa EstoqueObserver
public class AlertaServico implements EstoqueObserver {
    @Override
    public void aoAtualizarEstoque(EstoqueId id) {
        removerAlertasSeNecessario(id); // R1H17
    }
}

// 2. Template notifica observers após atualização
public abstract class AtualizacaoEstoqueTemplate {
    private final List<EstoqueObserver> observers = new ArrayList<>();
    
    public final void atualizar(EstoqueId id) {
        // ... passos 1-4
        notificarObservers(id); // passo 5
    }
}

// 3. Registro do observer na configuração
public class EstoqueServico {
    public EstoqueServico(..., AlertaServico alertaServico) {
        var template = new AtualizacaoEstoquePadrao(repo);
        template.registrarObserver(alertaServico); // registro
        this.atualizacaoTemplate = template;
    }
}
```
✅ **Benefício**: `EstoqueServico` não conhece `AlertaServico` diretamente; apenas registra observers

### Registro no Spring Boot

No `AplicacaoBackend.java` (quando estiver ativo):
```java
@Bean
public EstoqueServico estoqueServico(EstoqueRepositorio estoqueRepo, 
                                     PedidoRepositorio pedidoRepo,
                                     AlertaRepositorio alertaRepo,
                                     EstoqueRepositorio estoqueRepoParaAlerta) {
    var template = new AtualizacaoEstoquePadrao(estoqueRepo);
    var alertaServico = new AlertaServico(alertaRepo, estoqueRepoParaAlerta);
    template.registrarObserver(alertaServico); // R1H17
    // template.registrarObserver(emailObserver); // futuro
    // template.registrarObserver(relatorioObserver); // futuro
    return new EstoqueServico(estoqueRepo, pedidoRepo, template);
}
```

### Estrutura do Padrão

```
┌─────────────────────────────────────────────────────────────┐
│                    EstoqueServico                           │
│  (usa AtualizacaoEstoqueTemplate)                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│        AtualizacaoEstoqueTemplate                          │
│  (Template Method + Subject)                                │
│  - List<EstoqueObserver> observers                         │
│  - registrarObserver(observer)                              │
│  - notificarObservers(estoqueId)                           │
│  - final void atualizar(estoqueId)                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
        │ notifica                    │ implementa
        ▼                             ▼
┌──────────────────┐        ┌──────────────────────┐
│EstoqueObserver   │        │AtualizacaoEstoque    │
│  (interface)     │        │    Padrao             │
└────────┬─────────┘        └──────────────────────┘
         │
         │ implementa
         ▼
┌─────────────────────────────────────────────────────────────┐
│              AlertaServico (Observer Concreto)              │
│  - Reage a atualização removendo alertas (R1H17)            │
└─────────────────────────────────────────────────────────────┘
```

### Benefícios Específicos no Domínio Gestock

- **Desacoplamento total**: `EstoqueServico` não conhece `AlertaServico`; comunicação via evento
- **Extensibilidade sem modificação**: Novos observers (email, relatório, log) podem ser adicionados sem alterar código de atualização
- **Testabilidade**: `EstoqueServico` pode ser testado sem `AlertaServico` real (sem observers ou com mocks)
- **Responsabilidade única**: Cada observer cuida de UMA reação ao evento de atualização
- **Event-driven design**: Alinhado com arquiteturas orientadas a eventos (base para futura migração para eventos assíncronos)
- **Template Method**: Define fluxo fixo de atualização, garantindo que observers sejam sempre notificados

### Exemplo de Uso

```java
// Criação do template
EstoqueRepositorio estoqueRepo = new EstoqueRepositorioImpl(...);
AtualizacaoEstoqueTemplate template = new AtualizacaoEstoquePadrao(estoqueRepo);

// Criação e registro do observer
AlertaRepositorio alertaRepo = new AlertaRepositorioImpl(...);
AlertaServico alertaServico = new AlertaServico(alertaRepo, estoqueRepo);
template.registrarObserver(alertaServico);

// Criação do serviço com template
EstoqueServico servico = new EstoqueServico(estoqueRepo, null, template);

// Quando atualizar é chamado, observers são notificados automaticamente
Estoque estoque = ...;
servico.atualizar(estoque);
// Template executa passos 1-4 e notifica observers no passo 5
// AlertaServico remove alertas se necessário (R1H17)
```

### Fluxo do Template Method

O `AtualizacaoEstoqueTemplate` define o seguinte fluxo fixo:

1. **Passo 1**: Carregar estoque do repositório
2. **Passo 2**: Validar estoque (deve estar ativo)
3. **Passo 3**: Processar atualização (hook para subclasses)
4. **Passo 4**: Salvar estoque atualizado
5. **Passo 5**: Notificar observers (R1H17 será tratado pelos observers)

### Extensibilidade Futura

O padrão permite adicionar facilmente:

1. **NotificacaoEmailObserver**: Envia emails quando estoque é atualizado
2. **RelatorioObserver**: Gera relatório de atualização de estoque
3. **LogObserver**: Registra eventos de atualização em log estruturado
4. **FornecedorNotificacaoObserver**: Notifica fornecedores sobre mudanças de estoque
5. **MetricasObserver**: Coleta métricas de estoque para análise
6. **CacheObserver**: Atualiza cache quando estoque é modificado

### Integração com Outros Padrões

O Observer trabalha em conjunto com o **Decorator** e **Template Method**:

- **Decorator**: Adiciona auditoria às operações de repositório
- **Template Method**: Define fluxo fixo de atualização
- **Observer**: Reage a eventos de domínio (atualização de estoque)

**Fluxo completo: Atualizar Estoque**
```
[Cliente] 
   ↓ chama atualizar(estoque)
[EstoqueServico] 
   ↓ delega para
[AtualizacaoEstoqueTemplate] ← Template Method (define fluxo fixo)
   ↓ passo 1: carregarEstoque()
[EstoqueRepositorioDecorator] ← Decorator (adiciona auditoria)
   ↓ registra leitura e delega
[EstoqueRepositorioImpl] (JPA)
   ↓ retorna Estoque
[AtualizacaoEstoqueTemplate]
   ↓ passo 2-3: validar e processar
   ↓ passo 4: salvar()
[EstoqueRepositorioDecorator] ← Decorator (registra salvamento)
   ↓ passo 5: notificarObservers()
[AlertaServico] ← Observer (reage ao evento)
   ↓ aoAtualizarEstoque(id)
   ↓ remove alertas se necessário (R1H17)
✓ Atualização completa
```

### Diferenças entre Observer e Outros Padrões

**Observer vs Mediator:**
- **Observer**: Um subject notifica múltiplos observers (one-to-many)
- **Mediator**: Um mediador coordena comunicação entre múltiplos objetos (many-to-many)

**Observer vs Strategy:**
- **Observer**: Reage a eventos (comportamento reativo)
- **Strategy**: Troca algoritmo/comportamento (comportamento ativo)

**Observer vs Command:**
- **Observer**: Notifica sobre mudanças de estado
- **Command**: Encapsula requisições como objetos

### Conclusão

O padrão Observer foi implementado com sucesso no Gestock para desacoplar a lógica de alertas do `EstoqueServico`. A implementação combina Template Method (para definir o fluxo fixo) com Observer (para notificar reações), seguindo os princípios SOLID, especialmente o Open/Closed Principle e o Single Responsibility Principle. O padrão é facilmente extensível e pode ser aplicado a outros eventos de domínio conforme necessário.

---

## 3. Padrão Template Method

### Contexto no Domínio

O padrão Template Method foi aplicado para definir o **fluxo de atualização de estoques**, uma operação crítica no Gestock que envolve múltiplas etapas sequenciais e validações de regras de negócio (R2H2: estoque com pedido em andamento não pode ser atualizado, R1H17: alertas devem ser removidos automaticamente).

### Por que este padrão faz sentido no domínio?

- **Algoritmo fixo com variações**: O fluxo de atualização tem etapas obrigatórias (carregar, validar, salvar, notificar) que devem sempre ocorrer na mesma ordem
- **Extensibilidade controlada**: Permite criar variações de atualização (ex.: com validações extras, com aprovação manual) sem duplicar o fluxo base
- **Garantia de consistência**: Evita que implementações esqueçam etapas críticas como notificação de observers ou validação de pré-condições
- **Pontos de extensão**: Ganchos (hooks) permitem customização antes/depois da atualização sem quebrar o fluxo principal

### Classes Criadas/Modificadas

#### `AtualizacaoEstoqueTemplate` (CRIADA - Classe Abstrata)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/AtualizacaoEstoqueTemplate.java`
- **Responsabilidade**: Define o **template method** `final atualizar(EstoqueId)` com o algoritmo completo:
  
  **Fluxo Fixo (não pode ser alterado)**:
  1. `carregarEstoque(id)` - busca o estoque (abstrato)
  2. `validarEstoqueInativo(estoque)` - verifica se está inativo (concreto)
  3. `validarPreCondicoes(estoqueId, estoque)` - valida R2H2 e outras regras (abstrato)
  4. `antesDeAtualizar(estoque, estoqueId)` - hook opcional (concreto, vazio)
  5. `aplicarAtualizacao(estoque)` - processa mudanças no estoque (concreto)
  6. `salvarEstoque(estoque)` - persiste (abstrato)
  7. `notificarObservers(estoqueId)` - notifica observers registrados (concreto)
  8. `aposAtualizar(estoque, estoqueId)` - hook opcional (concreto, vazio)

- **Métodos abstratos**: Devem ser implementados pelas subclasses
  - `carregarEstoque(EstoqueId)` - busca o estoque do repositório
  - `validarPreCondicoes(EstoqueId, Estoque)` - valida regras de negócio
  - `salvarEstoque(Estoque)` - persiste o estoque

- **Métodos concretos**: Implementação padrão que pode ser sobrescrita se necessário
  - `validarEstoqueInativo(Estoque)` - valida se estoque está ativo
  - `antesDeAtualizar(Estoque, EstoqueId)` - hook antes da atualização
  - `aplicarAtualizacao(Estoque)` - processa atualização
  - `notificarObservers(EstoqueId)` - notifica observers
  - `aposAtualizar(Estoque, EstoqueId)` - hook após atualização

- **Papel duplo**: Também atua como **Subject** no padrão Observer (mantém lista de observers)

#### `AtualizacaoEstoquePadrao` (CRIADA - Implementação Concreta)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/AtualizacaoEstoquePadrao.java`
- **Responsabilidade**: Implementação concreta padrão do template
- **Implementações fornecidas**:
  - `carregarEstoque(id)`: Delega para `repositorio.buscarPorId(id)`
  - `validarPreCondicoes(id, estoque)`: Aplica **R2H2** (estoque com pedido em andamento não pode ser atualizado) via `pedidoRepositorio.existePedidoPendentePorEstoqueId(id)`
  - `salvar(estoque)`: Delega para `repositorio.salvar(estoque)`
- **Não sobrescreve**: Hooks opcionais (usa implementação vazia do template)

#### `EstoqueServico` (MODIFICADO)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/estoque/EstoqueServico.java`
- **Modificação**: O método `atualizar(Estoque)` agora delega para o template:
  ```java
  public void atualizar(Estoque estoque) {
      notNull(estoque, "Estoque é obrigatório");
      // SEMPRE usa o template - sem fallback (padrão Template Method)
      atualizacaoTemplate.atualizar(estoque.getId());
  }
  ```
- **Template obrigatório**: O template é sempre obrigatório (sem fallback), seguindo o padrão Template Method corretamente
- **Construtores**: 
  - Construtores sem template criam automaticamente `AtualizacaoEstoquePadrao` como padrão
  - Construtor principal exige template explicitamente

### Como Foi Aplicado

O método `atualizar(EstoqueId id)` em `AtualizacaoEstoqueTemplate` é declarado como `final`, garantindo que o fluxo não pode ser alterado. Subclasses implementam apenas os métodos abstratos:

```java
public abstract class AtualizacaoEstoqueTemplate {
    public final void atualizar(EstoqueId id) {
        // Fluxo fixo em 8 passos
        Optional<Estoque> estoqueOpt = carregarEstoque(id);
        Estoque estoque = estoqueOpt.get();
        validarEstoqueInativo(estoque);
        validarPreCondicoes(id, estoque);
        antesDeAtualizar(estoque, id);
        aplicarAtualizacao(estoque);
        salvarEstoque(estoque);
        notificarObservers(id);
        aposAtualizar(estoque, id);
    }
    
    protected abstract Optional<Estoque> carregarEstoque(EstoqueId id);
    protected abstract void validarPreCondicoes(EstoqueId id, Estoque estoque);
    protected abstract void salvarEstoque(Estoque estoque);
}
```

### Uso no EstoqueServico (MODIFICADO)

O `EstoqueServico` foi modificado para delegar a atualização ao template:

**Antes** (lógica inline):
```java
public void atualizar(Estoque estoque) {
    if (pedidoRepositorio != null && pedidoRepositorio.existePedidoPendentePorEstoqueId(estoque.getId())) {
        throw new IllegalStateException("R2H2");
    }
    estoqueRepositorio.salvar(estoque);
    // Observers não eram notificados automaticamente
}
```

**Depois** (usando Template Method):
```java
public void atualizar(Estoque estoque) {
    notNull(estoque, "Estoque é obrigatório");
    // SEMPRE usa o template - sem fallback (padrão Template Method)
    atualizacaoTemplate.atualizar(estoque.getId());
}
```

### Configuração no Spring Boot

No `AplicacaoBackend.java` (quando estiver ativo):
```java
@Bean
public EstoqueServico estoqueServico(EstoqueRepositorio estoqueRepo, 
                                     PedidoRepositorio pedidoRepo,
                                     AlertaRepositorio alertaRepo,
                                     EstoqueRepositorio estoqueRepoParaAlerta) {
    var template = new AtualizacaoEstoquePadrao(estoqueRepo, pedidoRepo);
    var alertaServico = new AlertaServico(alertaRepo, estoqueRepoParaAlerta);
    template.registrarObserver(alertaServico); // R1H17
    return new EstoqueServico(estoqueRepo, pedidoRepo, template);
}
```

### Estrutura do Padrão

```
┌─────────────────────────────────────────────────────────────┐
│                    EstoqueServico                           │
│  (delega para template)                                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│        AtualizacaoEstoqueTemplate                          │
│  (Template Method - classe abstrata)                        │
│  - final void atualizar(EstoqueId)                         │
│  - abstract carregarEstoque()                              │
│  - abstract validarPreCondicoes()                          │
│  - abstract salvarEstoque()                                │
│  - concrete validarEstoqueInativo()                         │
│  - concrete antesDeAtualizar() (hook)                      │
│  - concrete aplicarAtualizacao()                           │
│  - concrete notificarObservers()                           │
│  - concrete aposAtualizar() (hook)                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ extends
                       ▼
┌─────────────────────────────────────────────────────────────┐
│        AtualizacaoEstoquePadrao                            │
│  (Implementação concreta)                                   │
│  - carregarEstoque() → repositorio.buscarPorId()          │
│  - validarPreCondicoes() → valida R2H2                    │
│  - salvarEstoque() → repositorio.salvar()                 │
└─────────────────────────────────────────────────────────────┘
```

### Benefícios Específicos no Domínio Gestock

- **Consistência**: Impossível esquecer etapas (validação, notificação de observers) pois o fluxo é fixo
- **Extensibilidade**: Fácil criar variações:
  - `AtualizacaoEstoqueComAprovacao` (requer aprovação do gerente)
  - `AtualizacaoEstoqueComNotificacao` (envia emails após atualizar)
  - `AtualizacaoEstoqueComAuditoria` (registra auditoria detalhada)
- **Manutenibilidade**: Se a R2H2 mudar ou novas validações forem adicionadas, apenas a classe concreta muda
- **Testabilidade**: Pode-se criar implementações mock do template para testes
- **Garantia de notificação**: Observers são sempre notificados, não há como esquecer

### Exemplo de Uso

```java
// Criação do template
EstoqueRepositorio estoqueRepo = new EstoqueRepositorioImpl(...);
PedidoRepositorio pedidoRepo = new PedidoRepositorioImpl(...);
AtualizacaoEstoqueTemplate template = new AtualizacaoEstoquePadrao(estoqueRepo, pedidoRepo);

// Criação e registro do observer
AlertaRepositorio alertaRepo = new AlertaRepositorioImpl(...);
AlertaServico alertaServico = new AlertaServico(alertaRepo, estoqueRepo);
template.registrarObserver(alertaServico);

// Criação do serviço com template
EstoqueServico servico = new EstoqueServico(estoqueRepo, pedidoRepo, template);

// Quando atualizar é chamado, o template executa todos os passos
Estoque estoque = ...;
servico.atualizar(estoque);
// Template executa: carregar → validar → antesDeAtualizar → aplicar → salvar → notificar → aposAtualizar
```

### Fluxo Detalhado do Template Method

O `AtualizacaoEstoqueTemplate` define o seguinte fluxo fixo:

1. **Passo 1 - carregarEstoque(id)**: Busca o estoque do repositório (abstrato)
2. **Passo 2 - validarEstoqueInativo(estoque)**: Verifica se estoque está ativo (concreto)
3. **Passo 3 - validarPreCondicoes(id, estoque)**: Valida regras de negócio como R2H2 (abstrato)
4. **Passo 4 - antesDeAtualizar(estoque, id)**: Hook opcional antes da atualização (concreto, vazio)
5. **Passo 5 - aplicarAtualizacao(estoque)**: Processa mudanças no estoque (concreto)
6. **Passo 6 - salvarEstoque(estoque)**: Persiste o estoque atualizado (abstrato)
7. **Passo 7 - notificarObservers(id)**: Notifica observers registrados (concreto)
8. **Passo 8 - aposAtualizar(estoque, id)**: Hook opcional após atualização (concreto, vazio)

### Extensibilidade Futura

O padrão permite criar facilmente variações:

1. **AtualizacaoEstoqueComAprovacao**: Requer aprovação do gerente antes de atualizar
   ```java
   public class AtualizacaoEstoqueComAprovacao extends AtualizacaoEstoqueTemplate {
       @Override
       protected void validarPreCondicoes(EstoqueId id, Estoque estoque) {
           super.validarPreCondicoes(id, estoque);
           if (!temAprovacaoGerente(id)) {
               throw new IllegalStateException("Requer aprovação do gerente");
           }
       }
   }
   ```

2. **AtualizacaoEstoqueComNotificacao**: Envia emails após atualização
   ```java
   public class AtualizacaoEstoqueComNotificacao extends AtualizacaoEstoquePadrao {
       @Override
       protected void aposAtualizar(Estoque estoque, EstoqueId id) {
           emailService.enviarNotificacaoAtualizacao(estoque);
       }
   }
   ```

3. **AtualizacaoEstoqueComAuditoria**: Registra auditoria detalhada
   ```java
   public class AtualizacaoEstoqueComAuditoria extends AtualizacaoEstoquePadrao {
       @Override
       protected void antesDeAtualizar(Estoque estoque, EstoqueId id) {
           auditoriaService.registrarInicioAtualizacao(id);
       }
       
       @Override
       protected void aposAtualizar(Estoque estoque, EstoqueId id) {
           auditoriaService.registrarFimAtualizacao(id);
       }
   }
   ```

### Integração com Outros Padrões

O Template Method trabalha em conjunto com **Observer** e **Decorator**:

- **Template Method**: Define fluxo fixo de atualização
- **Observer**: Reage a eventos de domínio (atualização de estoque)
- **Decorator**: Adiciona auditoria às operações de repositório

**Fluxo completo: Atualizar Estoque**
```
[Cliente] 
   ↓ chama atualizar(estoque)
[EstoqueServico] 
   ↓ delega para
[AtualizacaoEstoqueTemplate] ← Template Method (define fluxo fixo)
   ↓ passo 1: carregarEstoque()
[EstoqueRepositorioDecorator] ← Decorator (adiciona auditoria)
   ↓ registra leitura e delega
[EstoqueRepositorioImpl] (JPA)
   ↓ retorna Estoque
[AtualizacaoEstoqueTemplate]
   ↓ passo 2: validarEstoqueInativo()
   ↓ passo 3: validarPreCondicoes() (R2H2)
   ↓ passo 4: antesDeAtualizar() (hook)
   ↓ passo 5: aplicarAtualizacao()
   ↓ passo 6: salvar()
[EstoqueRepositorioDecorator] ← Decorator (registra salvamento)
   ↓ passo 7: notificarObservers()
[AlertaServico] ← Observer (reage ao evento)
   ↓ aoAtualizarEstoque(id)
   ↓ remove alertas se necessário (R1H17)
   ↓ passo 8: aposAtualizar() (hook)
✓ Atualização completa
```

### Diferenças entre Template Method e Outros Padrões

**Template Method vs Strategy:**
- **Template Method**: Define estrutura fixa de algoritmo com pontos de extensão
- **Strategy**: Troca algoritmo completo (estrutura pode mudar)

**Template Method vs Factory Method:**
- **Template Method**: Define fluxo de execução
- **Factory Method**: Define criação de objetos

**Template Method vs Hook Method:**
- **Template Method**: Padrão completo (template + hooks)
- **Hook Method**: Técnica usada dentro do Template Method

### Conclusão

O padrão Template Method foi implementado com sucesso no Gestock para definir o fluxo fixo de atualização de estoques. A implementação garante que todas as etapas críticas (validação, persistência, notificação de observers) sejam sempre executadas na ordem correta, evitando erros e garantindo consistência. O padrão é facilmente extensível através de subclasses que podem personalizar pontos específicos do fluxo sem alterar a estrutura principal.

---

## 4. Padrão Strategy

### Contexto no Domínio

O padrão Strategy foi aplicado para encapsular diferentes **algoritmos de seleção de cotação**. No Gestock, a seleção da melhor cotação é feita com base em critérios que podem variar (menor preço, menor prazo, melhor relação preço/prazo, etc.) dependendo de decisões estratégicas ou configurações futuras.

### Por que este padrão faz sentido no domínio?

- **Requisitos mutáveis**: Empresas frequentemente mudam critérios de seleção de fornecedores e cotações
- **Múltiplos algoritmos**: Diferentes tipos de produtos ou situações podem usar diferentes estratégias de seleção
- **Testabilidade**: Facilita testar `FornecedorServico` com algoritmos mock sem depender de lógica de seleção complexa
- **Single Responsibility**: `FornecedorServico` orquestra o fluxo de seleção; a Strategy implementa o algoritmo de comparação
- **Open/Closed**: Novos algoritmos podem ser adicionados sem modificar `FornecedorServico`

### Classes Criadas/Modificadas

#### `SelecaoCotacaoStrategy` (CRIADA - Interface)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/fornecedor/SelecaoCotacaoStrategy.java`
- **Responsabilidade**: Define o contrato para algoritmos de seleção de cotação
- **Método principal**: 
  ```java
  Optional<Cotacao> selecionar(List<Fornecedor> fornecedores, ProdutoId produtoId);
  ```
- **Entrada**: 
  - `fornecedores`: Lista de fornecedores a considerar
  - `produtoId`: O produto para o qual selecionar a cotação
- **Saída**: `Optional<Cotacao>` com a melhor cotação encontrada

#### `SelecaoCotacaoMenorPreco` (CRIADA - Estratégia Concreta)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/fornecedor/SelecaoCotacaoMenorPreco.java`
- **Responsabilidade**: Implementa o algoritmo de **menor preço** usado atualmente no Gestock
- **Algoritmo**:
  1. Filtra fornecedores ativos (R1H18)
  2. Filtra cotações com validade ativa (R1H18)
  3. Seleciona a cotação com menor preço
  4. Em caso de empate, seleciona a com menor prazo (R2H18)
- **Outras estratégias possíveis** (não implementadas ainda):
  - `SelecaoCotacaoMenorPrazo`: Prioriza menor prazo de entrega
  - `SelecaoCotacaoMelhorRelacao`: Usa relação preço/prazo (menor custo-benefício)
  - `SelecaoCotacaoPorConfiabilidade`: Prioriza fornecedores com melhor histórico

#### `FornecedorServico` (MODIFICADO)

- **Localização**: `dominio-principal/src/main/java/dev/gestock/sge/dominio/principal/fornecedor/FornecedorServico.java`
- **Modificação**: Agora recebe `SelecaoCotacaoStrategy` via construtor (inversão de dependência)
- **Responsabilidade**: Orquestra a seleção de cotação, **mas não implementa o algoritmo**
- **Strategy obrigatória**: A strategy é sempre obrigatória (sem fallback inline), seguindo o padrão Strategy corretamente
- **Construtores**: 
  - Construtores sem strategy criam automaticamente `SelecaoCotacaoMenorPreco` como padrão
  - Construtor principal exige strategy explicitamente
- **Uso da strategy**:
  ```java
  // Sempre delega para a strategy - sem fallback
  return selecaoCotacaoStrategy.selecionar(fornecedores, produtoId);
  ```

### Como Foi Aplicado

**Antes** (lógica de seleção dentro do serviço):
```java
public class FornecedorServico {
    public Optional<Cotacao> selecionarMelhorCotacao(List<Fornecedor> fornecedores, ProdutoId produtoId) {
        // ... lógica de seleção inline
        return fornecedores.stream()
            .filter(Fornecedor::isAtivo)
            .map(f -> f.obterCotacaoPorProduto(produtoId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(Cotacao::isValidadeAtiva)
            .min((c1, c2) -> {
                int cmp = Double.compare(c1.getPreco(), c2.getPreco());
                return (cmp != 0) ? cmp : Integer.compare(c1.getPrazoDias(), c2.getPrazoDias());
            }); // algoritmo inline
    }
}
```

**Depois** (usando Strategy):
```java
public class FornecedorServico {
    private final SelecaoCotacaoStrategy selecaoCotacaoStrategy;
    
    // Construtores que criam strategy padrão automaticamente
    public FornecedorServico(FornecedorRepositorio fornecedorRepositorio) {
        this(fornecedorRepositorio, null, new SelecaoCotacaoMenorPreco());
    }
    
    public FornecedorServico(FornecedorRepositorio fornecedorRepositorio, PedidoRepositorio pedidoRepositorio) {
        this(fornecedorRepositorio, pedidoRepositorio, new SelecaoCotacaoMenorPreco());
    }
    
    // Construtor principal - strategy obrigatória
    public FornecedorServico(FornecedorRepositorio fornecedorRepositorio, 
                             PedidoRepositorio pedidoRepositorio, 
                             SelecaoCotacaoStrategy selecaoCotacaoStrategy) {
        notNull(selecaoCotacaoStrategy, "Strategy é obrigatória");
        this.selecaoCotacaoStrategy = selecaoCotacaoStrategy;
    }
    
    public Optional<Cotacao> selecionarMelhorCotacao(List<Fornecedor> fornecedores, ProdutoId produtoId) {
        // Sempre delega para a strategy - sem fallback inline
        return selecaoCotacaoStrategy.selecionar(fornecedores, produtoId);
    }
}
```

### Configuração no Spring Boot

No `AplicacaoBackend.java` (quando estiver ativo), a strategy concreta é injetada como bean:

```java
@Bean
public SelecaoCotacaoStrategy selecaoCotacaoStrategy() {
    return new SelecaoCotacaoMenorPreco();
}

@Bean
public FornecedorServico fornecedorServico(FornecedorRepositorio fornecedorRepo, 
                                           PedidoRepositorio pedidoRepo,
                                           SelecaoCotacaoStrategy strategy) {
    return new FornecedorServico(fornecedorRepo, pedidoRepo, strategy);
}
```

### Estrutura do Padrão

```
┌─────────────────────────────────────────────────────────────┐
│                    FornecedorServico                         │
│  (orquestra seleção, mas não implementa algoritmo)            │
│  - SelecaoCotacaoStrategy selecaoCotacaoStrategy            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ usa
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              SelecaoCotacaoStrategy (Interface)              │
│  - selecionar(List<Fornecedor>, ProdutoId)                  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ implementa
                       ▼
┌─────────────────────────────────────────────────────────────┐
│        SelecaoCotacaoMenorPreco (Estratégia Concreta)       │
│  - Seleciona por menor preço, desempata por prazo (R2H18)   │
└─────────────────────────────────────────────────────────────┘
```

### Benefícios Específicos no Domínio Gestock

- **Flexibilidade estratégica**: Gerentes podem escolher diferentes critérios de seleção sem alterar código
- **Testabilidade**: Testes do `FornecedorServico` podem usar strategy mock que retorna cotações fixas
- **Extensibilidade**: Novos algoritmos (menor prazo, melhor relação, por confiabilidade) são fáceis de adicionar
- **Separação de responsabilidades**: `FornecedorServico` cuida da orquestração; Strategy cuida do algoritmo de seleção
- **Configuração dinâmica**: No futuro, a strategy pode ser escolhida por configuração ou por tipo de produto
- **Padrão Strategy puro**: Sem fallback inline, eliminando duplicação de código e lógica condicional
- **Strategy obrigatória**: Garante que o padrão seja seguido corretamente, sem código duplicado

### Exemplo de Uso

```java
// Criação da strategy
SelecaoCotacaoStrategy strategy = new SelecaoCotacaoMenorPreco();

// Criação do serviço com strategy
FornecedorRepositorio fornecedorRepo = new FornecedorRepositorioImpl(...);
PedidoRepositorio pedidoRepo = new PedidoRepositorioImpl(...);
FornecedorServico servico = new FornecedorServico(fornecedorRepo, pedidoRepo, strategy);

// Quando selecionarMelhorCotacao é chamado, a strategy executa o algoritmo
List<Fornecedor> fornecedores = ...;
ProdutoId produtoId = ...;
Optional<Cotacao> melhorCotacao = servico.selecionarMelhorCotacao(fornecedores, produtoId);
// SelecaoCotacaoMenorPreco seleciona a cotação com menor preço (R2H18)
```

### Extensibilidade Futura

O padrão permite adicionar facilmente novas estratégias:

1. **SelecaoCotacaoMenorPrazo**: Prioriza menor prazo de entrega
   ```java
   public class SelecaoCotacaoMenorPrazo implements SelecaoCotacaoStrategy {
       @Override
       public Optional<Cotacao> selecionar(List<Fornecedor> fornecedores, ProdutoId produtoId) {
           return fornecedores.stream()
               .filter(Fornecedor::isAtivo)
               .map(f -> f.obterCotacaoPorProduto(produtoId))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .filter(Cotacao::isValidadeAtiva)
               .min(Comparator.comparingInt(Cotacao::getPrazoDias)
                   .thenComparingDouble(Cotacao::getPreco)); // desempata por preço
       }
   }
   ```

2. **SelecaoCotacaoMelhorRelacao**: Usa relação preço/prazo
   ```java
   public class SelecaoCotacaoMelhorRelacao implements SelecaoCotacaoStrategy {
       @Override
       public Optional<Cotacao> selecionar(List<Fornecedor> fornecedores, ProdutoId produtoId) {
           return fornecedores.stream()
               .filter(Fornecedor::isAtivo)
               .map(f -> f.obterCotacaoPorProduto(produtoId))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .filter(Cotacao::isValidadeAtiva)
               .min(Comparator.comparingDouble(c -> c.getPreco() / c.getPrazoDias())); // melhor relação
       }
   }
   ```

3. **SelecaoCotacaoPorConfiabilidade**: Prioriza fornecedores com melhor histórico
   ```java
   public class SelecaoCotacaoPorConfiabilidade implements SelecaoCotacaoStrategy {
       private final HistoricoFornecedorRepositorio historicoRepo;
       
       @Override
       public Optional<Cotacao> selecionar(List<Fornecedor> fornecedores, ProdutoId produtoId) {
           // Seleciona baseado em histórico de entregas e confiabilidade
           // ...
       }
   }
   ```

### Integração com Outros Padrões

O Strategy trabalha em conjunto com outros padrões:

- **Strategy + Template Method**: O Template Method pode usar diferentes strategies em diferentes passos
- **Strategy + Observer**: Observers podem reagir a seleções de cotação usando diferentes strategies
- **Strategy + Decorator**: Decorators podem adicionar funcionalidades (cache, logging) às strategies

**Fluxo completo: Selecionar Melhor Cotação**
```
[Cliente] 
   ↓ chama selecionarMelhorCotacao(fornecedores, produtoId)
[FornecedorServico] 
   ↓ delega para
[SelecaoCotacaoStrategy] ← Strategy (algoritmo de seleção)
   ↓ selecionar()
[SelecaoCotacaoMenorPreco] ← Estratégia concreta
   ↓ filtra e compara cotações
   ↓ retorna melhor cotação
✓ Cotação selecionada
```

### Diferenças entre Strategy e Outros Padrões

**Strategy vs Template Method:**
- **Strategy**: Troca algoritmo completo (estrutura pode mudar)
- **Template Method**: Define estrutura fixa de algoritmo com pontos de extensão

**Strategy vs State:**
- **Strategy**: Troca comportamento baseado em escolha/configuração
- **State**: Troca comportamento baseado em estado interno do objeto

**Strategy vs Command:**
- **Strategy**: Encapsula algoritmo/comportamento
- **Command**: Encapsula requisição/ação

### Conclusão

O padrão Strategy foi implementado com sucesso no Gestock para encapsular algoritmos de seleção de cotação. A implementação segue os princípios SOLID, especialmente o Open/Closed Principle e o Single Responsibility Principle, permitindo adicionar novos algoritmos de seleção sem modificar o código existente. O padrão é facilmente extensível e pode ser aplicado a outros algoritmos variáveis no domínio conforme necessário.

---

## Próximos Padrões

Este documento será atualizado conforme novos padrões de projeto forem implementados no sistema Gestock.
