# Padrões de Design Implementados

Este documento descreve os padrões de design implementados no sistema Gestock, conforme solicitado.

## Distribuição dos Padrões

### 1. Decorator - Gerenciar Estoques (SILVIO)

**Localização:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/estoque/`

**Arquivos:**
- `EstoqueServicoDecorator.java` - Classe abstrata base do decorator
- `EstoqueServicoComLogging.java` - Decorator concreto que adiciona funcionalidade de logging

**Descrição:**
O padrão Decorator foi aplicado ao `EstoqueServico` para adicionar funcionalidades extras (como logging) sem modificar a implementação original. O decorator permite envolver o serviço original e adicionar comportamentos antes e depois das operações.

**Uso:**
```java
EstoqueServico servicoBase = new EstoqueServico(repositorio);
EstoqueServico servicoComLog = new EstoqueServicoComLogging(servicoBase);
servicoComLog.salvar(estoque); // Operação com logging automático
```

---

### 2. Template Method - Gerenciar Produtos (RONALDO)

**Localização:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/produto/`

**Arquivos:**
- `ProdutoOperacaoTemplate.java` - Classe abstrata que define o template do algoritmo
- `ProdutoAtivacaoOperacao.java` - Implementação concreta para ativação
- `ProdutoInativacaoOperacao.java` - Implementação concreta para inativação

**Descrição:**
O padrão Template Method foi aplicado para definir o esqueleto de operações de produto (ativação/inativação), deixando alguns passos para as subclasses implementarem. O template define o fluxo comum: validação, obtenção do produto, validação de regras, execução da operação, persistência e pós-processamento.

**Uso:**
```java
ProdutoOperacaoTemplate operacao = new ProdutoAtivacaoOperacao(repositorio);
operacao.executar(produtoId); // Executa o template com a implementação específica
```

---

### 3. Proxy - Gerenciar Pedidos (BERNARDO)

**Localização:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/pedido/`

**Arquivos:**
- `PedidoServicoProxy.java` - Proxy que controla o acesso ao PedidoServico

**Descrição:**
O padrão Proxy foi aplicado ao `PedidoServico` para adicionar controle de acesso e funcionalidades de auditoria. O proxy intercepta as chamadas ao serviço real, verificando permissões e registrando operações antes de delegar ao serviço original.

**Uso:**
```java
PedidoServicoProxy proxy = new PedidoServicoProxy(repositorio, ...);
proxy.permitirAcesso(true);
proxy.criar(pedido); // Operação com controle de acesso e auditoria
```

---

### 4. Observer - Emitir Alertas de Estoque Baixo (RODRIGO)

**Localização:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/alerta/`

**Arquivos:**
- `AlertaObserver.java` - Interface do observador
- `AlertaInfo.java` - Classe com informações do alerta
- `AlertaServicoObservable.java` - Serviço observável que notifica observadores
- `AlertaObserverEmail.java` - Observador concreto que envia emails

**Descrição:**
O padrão Observer foi aplicado ao sistema de alertas para notificar múltiplos observadores quando alertas são gerados. Isso permite adicionar diferentes tipos de notificações (email, SMS, logs, etc.) sem modificar o código principal.

**Uso:**
```java
AlertaServicoObservable servico = new AlertaServicoObservable(new AlertaServico());
servico.adicionarObservador(new AlertaObserverEmail());
servico.determinarNivelENotificar(percentual, produtoId, ...); // Notifica todos os observadores
```

---

### 5. Strategy - Selecionar Cotação Mais Vantajosa (EDUARDO)

**Localização:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/cotacao/`

**Arquivos:**
- `CotacaoSelecaoStrategy.java` - Interface da estratégia
- `CotacaoSelecaoPorPrecoStrategy.java` - Estratégia que seleciona por menor preço
- `CotacaoSelecaoPorLeadTimeStrategy.java` - Estratégia que seleciona por menor lead time
- `CotacaoSelecaoCompletaStrategy.java` - Estratégia completa (padrão)
- `CotacaoServico.java` - Atualizado para usar estratégias

**Descrição:**
O padrão Strategy foi aplicado ao `CotacaoServico` para permitir diferentes algoritmos de seleção de cotação. Isso permite escolher entre diferentes critérios (preço, lead time, ou combinação) sem modificar o código do serviço.

**Uso:**
```java
CotacaoServico servico = new CotacaoServico(repositorio, ...);
// Usar estratégia padrão (completa)
Cotacao melhor = servico.obterMaisVantajosa(produtoId);

// Ou usar estratégia específica
Cotacao maisBarata = servico.obterMaisVantajosa(produtoId, new CotacaoSelecaoPorPrecoStrategy());
```

---

## Resumo

Todos os 5 padrões de design foram implementados conforme solicitado:

1. ✅ **Decorator** - SILVIO (Gerenciar Estoques)
2. ✅ **Template Method** - RONALDO (Gerenciar Produtos)
3. ✅ **Proxy** - BERNARDO (Gerenciar Pedidos)
4. ✅ **Observer** - RODRIGO (Emitir Alertas de Estoque Baixo)
5. ✅ **Strategy** - EDUARDO (Selecionar Cotação Mais Vantajosa)

Cada padrão foi aplicado em uma funcionalidade diferente, sem repetição, e todos os padrões solicitados foram implementados.

