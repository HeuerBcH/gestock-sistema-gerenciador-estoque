# Padrões de Design Implementados

## Visão Geral

O projeto Gestock implementa 5 padrões de design do catálogo GoF (Gang of Four), cada um aplicado em uma funcionalidade específica do sistema:

1. **Decorator** - Gerenciar Estoques (SILVIO)
2. **Template Method** - Gerenciar Produtos (RONALDO)
3. **Proxy** - Gerenciar Pedidos (BERNARDO)
4. **Observer** - Emitir Alertas de Estoque Baixo (RODRIGO)
5. **Strategy** - Selecionar Cotação Mais Vantajosa (EDUARDO)

---

## 1. Decorator - Gerenciar Estoques (SILVIO)

### Localização da Implementação

**Pacote:** `dev.gestock.sge.dominio.estoque`

**Caminho completo:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/estoque/`

### Arquivos Implementados

1. **`IEstoqueServico.java`** - Interface comum que define o contrato do serviço de estoque
   - Define os métodos: `salvar()`, `remover()`, `ativar()`, `inativar()`
   - Essencial para o padrão Decorator, permitindo substituição transparente

2. **`EstoqueServico.java`** - Componente Concreto (Subject Real)
   - Implementação base do serviço de estoque
   - Contém toda a lógica de negócio e validações (regras R1H2, R1H3, R2H1, R2H2, R3H1)
   - Pode ser decorado sem modificação

3. **`EstoqueServicoDecorator.java`** - Decorator Abstrato
   - Classe base abstrata para todos os decorators
   - Implementa `IEstoqueServico` e mantém referência ao serviço decorado
   - Delega todas as chamadas para o serviço decorado

4. **`EstoqueServicoComLogging.java`** - Decorator Concreto
   - Implementação concreta que adiciona funcionalidade de logging
   - Intercepta todas as operações para registrar logs antes e depois da execução
   - Captura e registra erros sem interromper o fluxo

### Justificativa da Escolha

O padrão Decorator foi escolhido para o serviço de estoque porque:

1. **Extensibilidade sem modificação**: Permite adicionar funcionalidades (como logging, cache, validações extras, métricas) ao `EstoqueServico` sem modificar sua implementação original, seguindo o princípio Open/Closed.

2. **Flexibilidade de composição**: Diferentes decorators podem ser combinados dinamicamente em tempo de execução, permitindo criar diferentes configurações conforme a necessidade (ex: logging em desenvolvimento, cache em produção).

3. **Separação de responsabilidades**: A lógica de negócio permanece no `EstoqueServico`, enquanto funcionalidades transversais (cross-cutting concerns) como logging ficam nos decorators.

4. **Substituição transparente**: A interface `IEstoqueServico` permite que o código cliente não precise saber se está usando o serviço base ou um decorator, facilitando testes e manutenção.

### Utilidade no Projeto

- **Logging e Auditoria**: O `EstoqueServicoComLogging` registra todas as operações de estoque para auditoria e debugging, essencial em um sistema de gestão de estoque onde rastreabilidade é crucial.

- **Facilita manutenção**: Novos decorators podem ser adicionados facilmente (ex: `EstoqueServicoComCache`, `EstoqueServicoComValidacaoExtra`) sem alterar código existente.

- **Configuração flexível**: No arquivo `BackendAplicacao.java`, há um exemplo comentado mostrando como alternar entre o serviço base e o decorado, permitindo diferentes configurações por ambiente.

### Como Está Implementado

O padrão segue a estrutura clássica do GoF:

```
IEstoqueServico (Interface)
    ↑
    ├── EstoqueServico (Componente Concreto)
    └── EstoqueServicoDecorator (Decorator Abstrato)
            └── EstoqueServicoComLogging (Decorator Concreto)
```

### Exemplo de Uso

```java
IEstoqueServico servicoBase = new EstoqueServico(repositorio);

IEstoqueServico servicoComLog = new EstoqueServicoComLogging(servicoBase);
servicoComLog.salvar(estoque); 
```

### Onde é Utilizado

- **Configuração Spring**: `BackendAplicacao.java` (linha 116-140) - Bean opcional comentado mostrando uso do decorator
- **Potencial uso**: Qualquer lugar que injete `IEstoqueServico` pode receber tanto o serviço base quanto decorators

---

## 2. Template Method - Gerenciar Produtos (RONALDO)

### Localização da Implementação

**Pacote:** `dev.gestock.sge.dominio.produto`

**Caminho completo:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/produto/`

### Arquivos Implementados

1. **`ProdutoOperacaoTemplate.java`** - Classe Abstrata (Template)
   - Define o esqueleto do algoritmo de operações de produto
   - Método template `executar()` que define o fluxo fixo
   - Métodos abstratos e hooks que podem ser sobrescritos

2. **`ProdutoAtivacaoOperacao.java`** - Implementação Concreta
   - Especializa o template para ativação de produtos
   - Implementa validação específica: R3H8 (produto deve estar vinculado a estoque ativo)
   - Executa a operação de ativação

3. **`ProdutoInativacaoOperacao.java`** - Implementação Concreta
   - Especializa o template para inativação de produtos
   - Implementa validações específicas: R1H10 (não pode inativar com saldo ou pedidos em andamento)
   - Executa a operação de inativação

### Justificativa da Escolha

O padrão Template Method foi escolhido para operações de produto porque:

1. **Eliminação de duplicação**: As operações de ativação e inativação compartilham um fluxo comum (validação, obtenção, persistência, pós-processamento), mas têm diferenças nas validações e na operação específica. O template elimina código duplicado.

2. **Controle do fluxo**: Garante que todas as operações sigam o mesmo fluxo, evitando erros por esquecimento de etapas importantes (ex: validar antes de executar, persistir após executar).

3. **Extensibilidade**: Novas operações de produto (ex: reativação, suspensão temporária) podem ser adicionadas criando novas subclasses, reutilizando o fluxo comum.

4. **Manutenibilidade**: Mudanças no fluxo comum (ex: adicionar logging em todas as operações) são feitas em um único lugar (classe template).

### Utilidade no Projeto

- **Consistência**: Garante que ativação e inativação sigam o mesmo padrão de validação e execução, reduzindo bugs.

- **Facilita adição de novas operações**: Se futuramente for necessário adicionar operações como "suspender temporariamente" ou "reativar", basta criar uma nova subclasse seguindo o template.

- **Separação clara**: O fluxo comum fica no template, enquanto as diferenças específicas ficam nas subclasses, facilitando entendimento e manutenção.

### Como Está Implementado

O template define 6 passos no método `executar()`:

1. **Validar entrada** - Validação comum (ID não nulo)
2. **Obter produto** - Busca no repositório
3. **Validar regras de negócio** - Hook method (pode ser sobrescrito)
4. **Executar operação** - Método abstrato (deve ser implementado)
5. **Persistir alterações** - Persistência comum
6. **Pós-processar** - Hook method (pode ser sobrescrito)

### Exemplo de Uso

```java
ProdutoOperacaoTemplate operacao = new ProdutoAtivacaoOperacao(repositorio);
operacao.executar(produtoId);

ProdutoOperacaoTemplate operacao = new ProdutoInativacaoOperacao(repositorio);
operacao.executar(produtoId); 
```

### Onde é Utilizado

- **Potencial uso**: Pode ser usado no `ProdutoServico` para substituir os métodos `ativar()` e `inativar()` atuais, garantindo consistência no fluxo de operações.

---

## 3. Proxy - Gerenciar Pedidos (BERNARDO)

### Localização da Implementação

**Pacote:** `dev.gestock.sge.dominio.pedido`

**Caminho completo:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/pedido/`

### Arquivos Implementados

1. **`IPedidoServico.java`** - Interface comum (Subject Interface)
   - Define o contrato do serviço de pedidos
   - Métodos: `criar()`, `confirmarRecebimento()`, `cancelar()`, `alterarStatus()`
   - Essencial para substituição transparente entre Subject Real e Proxy

2. **`PedidoServico.java`** - Subject Real (Implementação Real)
   - Implementação completa do serviço de pedidos
   - Contém toda a lógica de negócio complexa:
     - Validação de fornecedor e estoque
     - Cálculo de capacidade
     - Busca e aplicação de cotações
     - Criação de movimentações
     - Publicação de eventos
   - Implementa regras de negócio como R1H12 (não pode cancelar pedido em transporte)

3. **`PedidoServicoProxy.java`** - Proxy (Controle de Acesso)
   - Implementa `IPedidoServico` e envolve o `PedidoServico`
   - Adiciona funcionalidades extras:
     - **Controle de acesso**: Permite habilitar/desabilitar acesso ao serviço
     - **Auditoria/Logging**: Registra todas as operações para rastreabilidade
   - Intercepta chamadas antes de delegar ao serviço real

### Justificativa da Escolha

O padrão Proxy foi escolhido para o serviço de pedidos porque:

1. **Controle de acesso**: Em um sistema de gestão, pode ser necessário temporariamente bloquear operações de pedidos (ex: durante manutenção, migração de dados, ou por políticas de negócio).

2. **Auditoria e rastreabilidade**: Pedidos são operações críticas que precisam ser auditadas. O proxy permite adicionar logging/auditoria sem modificar a lógica de negócio complexa do `PedidoServico`.

3. **Separação de responsabilidades**: A lógica de negócio complexa permanece no `PedidoServico`, enquanto funcionalidades transversais (controle de acesso, auditoria) ficam no proxy.

4. **Substituição transparente**: O código cliente (como `PedidoControlador`) usa `IPedidoServico`, não sabendo se está usando o serviço real ou o proxy, facilitando alternância entre ambos.

5. **Extensibilidade futura**: O proxy pode ser estendido para adicionar cache, lazy loading, ou outras funcionalidades sem modificar o serviço real.

### Utilidade no Projeto

- **Segurança e controle**: Permite controlar quando operações de pedidos podem ser executadas, útil em cenários de manutenção ou políticas de negócio.

- **Auditoria completa**: Todas as operações de pedidos são registradas automaticamente, essencial para compliance e rastreabilidade em sistemas de gestão.

- **Flexibilidade de configuração**: No `BackendAplicacao.java` (linha 218-232), há comentários mostrando como alternar entre usar o serviço real diretamente ou através do proxy, permitindo diferentes configurações por ambiente.

- **Uso em produção**: O `PedidoControlador` injeta `IPedidoServico`, permitindo que o Spring injete tanto o serviço real quanto o proxy conforme a configuração.

### Como Está Implementado

O padrão segue a estrutura clássica do GoF:

```
IPedidoServico (Interface)
    ↑
    ├── PedidoServico (Subject Real)
    └── PedidoServicoProxy (Proxy)
            └── usa → PedidoServico (internamente)
```

O proxy intercepta todas as chamadas, verifica acesso, registra logs, e então delega para o serviço real.

### Exemplo de Uso

```java
IPedidoServico proxy = new PedidoServicoProxy(repositorio, fornecedorRepositorio, 
    produtoRepositorio, estoqueRepositorio, cotacaoRepositorio, cotacaoServico, 
    movimentacaoServico, barramento);

proxy.permitirAcesso(true);
Pedido pedido = proxy.criar(novoPedido); 

proxy.permitirAcesso(false);
proxy.criar(pedido); 
```

### Onde é Utilizado

- **Configuração Spring**: `BackendAplicacao.java` (linha 218-232) - Bean configurado para retornar `IPedidoServico`, com opção comentada para usar o proxy
- **Controller**: `PedidoControlador.java` (linha 27) - Injeta `IPedidoServico`, podendo receber tanto o serviço real quanto o proxy
- **Uso atual**: O sistema está configurado para usar o serviço real diretamente, mas pode ser facilmente alterado para usar o proxy

---

## 4. Observer - Emitir Alertas de Estoque Baixo (RODRIGO)

### Localização da Implementação

**Pacote:** `dev.gestock.sge.dominio.alerta`

**Caminho completo:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/alerta/`

### Arquivos Implementados

1. **`AlertaObserver.java`** - Interface do Observador
   - Define o contrato que todos os observadores devem implementar
   - Método: `notificarAlertaGerado(AlertaInfo alertaInfo)`

2. **`AlertaInfo.java`** - Classe de Dados
   - Contém todas as informações sobre o alerta gerado
   - Campos: produtoId, produtoNome, estoqueId, estoqueNome, quantidadeAtual, rop, percentualAbaixoRop, nivel

3. **`AlertaServicoObservable.java`** - Subject Observável
   - Envolve o `AlertaServico` e adiciona capacidade de notificar observadores
   - Mantém lista de observadores
   - Métodos: `adicionarObservador()`, `removerObservador()`, `determinarNivelENotificar()`
   - Notifica todos os observadores quando um alerta é gerado

4. **`AlertaObserverEmail.java`** - Observador Concreto
   - Implementação concreta que envia notificações por email
   - Filtra apenas alertas críticos para envio de email
   - Demonstra como diferentes observadores podem ter comportamentos diferentes

### Justificativa da Escolha

O padrão Observer foi escolhido para o sistema de alertas porque:

1. **Desacoplamento**: O `AlertaServico` não precisa saber quantos ou quais tipos de notificações serão enviadas. Novos tipos de notificação (SMS, push, webhook) podem ser adicionados sem modificar o código do serviço.

2. **Extensibilidade**: É fácil adicionar novos observadores (ex: `AlertaObserverSMS`, `AlertaObserverSlack`, `AlertaObserverLog`) sem modificar código existente.

3. **Flexibilidade**: Diferentes observadores podem ter comportamentos diferentes (ex: email apenas para alertas críticos, SMS para todos, log para debug).

4. **Princípio Open/Closed**: O sistema está aberto para extensão (novos observadores) mas fechado para modificação (não precisa alterar o serviço para adicionar notificações).

5. **Notificação múltipla**: Um alerta pode disparar múltiplas notificações simultaneamente (email + SMS + log) sem acoplamento entre elas.

### Utilidade no Projeto

- **Sistema de alertas robusto**: Permite que o sistema notifique múltiplos canais quando estoque está baixo, essencial para gestão proativa de estoque.

- **Facilita integração**: Novos canais de notificação podem ser adicionados facilmente (ex: integração com sistemas externos, dashboards, aplicativos móveis).

- **Configuração flexível**: Diferentes ambientes podem ter diferentes observadores (ex: desenvolvimento apenas com log, produção com email + SMS).

- **Manutenibilidade**: Cada tipo de notificação é isolado em sua própria classe, facilitando manutenção e testes.

### Como Está Implementado

O padrão segue a estrutura clássica do GoF:

```
AlertaServicoObservable (Subject)
    ├── mantém lista de → AlertaObserver (Interface)
    │                       ↑
    │                       └── AlertaObserverEmail (ConcreteObserver)
    │
    └── usa → AlertaServico (para determinar nível do alerta)
```

Quando `determinarNivelENotificar()` é chamado:
1. O `AlertaServico` determina o nível do alerta
2. Se um alerta foi gerado, cria um `AlertaInfo`
3. Notifica todos os observadores registrados
4. Cada observador processa a notificação de forma independente

### Exemplo de Uso

```java
AlertaServicoObservable servico = new AlertaServicoObservable(new AlertaServico());

servico.adicionarObservador(new AlertaObserverEmail());

servico.determinarNivelENotificar(percentual, produtoId, produtoNome, 
    estoqueId, estoqueNome, quantidadeAtual, rop);
```

### Onde é Utilizado

- **Potencial uso**: Pode ser usado em serviços que verificam níveis de estoque, como em processos de verificação de ponto de ressuprimento (ROP) ou em relatórios de estoque baixo.

---

## 5. Strategy - Selecionar Cotação Mais Vantajosa (EDUARDO)

### Localização da Implementação

**Pacote:** `dev.gestock.sge.dominio.cotacao`

**Caminho completo:** `backend/dominio-principal/src/main/java/dev/gestock/sge/dominio/cotacao/`

### Arquivos Implementados

1. **`CotacaoSelecaoStrategy.java`** - Interface da Estratégia
   - Define o contrato para algoritmos de seleção de cotação
   - Método: `selecionar(List<Cotacao> cotacoes)`

2. **`CotacaoSelecaoCompletaStrategy.java`** - Estratégia Concreta (Padrão)
   - Estratégia completa que considera múltiplos fatores:
     - Preço (menor)
     - Lead time (menor)
     - Validade (ATIVA primeiro)
     - ID (ordem original como desempate)
   - É a estratégia padrão usada no sistema

3. **`CotacaoSelecaoPorPrecoStrategy.java`** - Estratégia Concreta
   - Seleciona a cotação com menor preço
   - Útil quando o custo é a prioridade principal

4. **`CotacaoSelecaoPorLeadTimeStrategy.java`** - Estratégia Concreta
   - Seleciona a cotação com menor lead time
   - Útil quando a velocidade de entrega é a prioridade

5. **`CotacaoServico.java`** - Context
   - Mantém referência à estratégia atual
   - Método `definirEstrategiaSelecao()` para alterar estratégia
   - Método `obterMaisVantajosa()` que delega para a estratégia
   - Sobrecarga que permite usar estratégia específica sem alterar a configurada

### Justificativa da Escolha

O padrão Strategy foi escolhido para seleção de cotações porque:

1. **Múltiplos critérios de seleção**: Diferentes situações podem exigir diferentes critérios (ex: emergência precisa de menor lead time, economia precisa de menor preço, otimização precisa de combinação).

2. **Alteração dinâmica**: A estratégia pode ser alterada em tempo de execução conforme a necessidade do negócio, sem modificar o código do `CotacaoServico`.

3. **Extensibilidade**: Novas estratégias podem ser adicionadas facilmente (ex: `CotacaoSelecaoPorQualidadeStrategy`, `CotacaoSelecaoPorFornecedorPreferidoStrategy`) sem modificar código existente.

4. **Testabilidade**: Cada estratégia pode ser testada independentemente, e o `CotacaoServico` pode ser testado com diferentes estratégias.

5. **Separação de algoritmos**: Os algoritmos de seleção ficam isolados em suas próprias classes, facilitando manutenção e compreensão.

### Utilidade no Projeto

- **Flexibilidade de negócio**: Permite que o sistema se adapte a diferentes necessidades (ex: em períodos de alta demanda, priorizar lead time; em períodos de economia, priorizar preço).

- **Otimização**: A estratégia completa permite encontrar o melhor equilíbrio entre preço e tempo de entrega, otimizando a gestão de compras.

- **Configuração por contexto**: Diferentes partes do sistema podem usar estratégias diferentes (ex: pedidos automáticos usam estratégia completa, pedidos manuais podem escolher estratégia específica).

- **Facilita testes**: Estratégias simples podem ser usadas em testes para garantir comportamento previsível.

### Como Está Implementado

O padrão segue a estrutura clássica do GoF:

```
CotacaoServico (Context)
    ├── mantém referência para → CotacaoSelecaoStrategy (Interface)
    │                               ↑
    │                               ├── CotacaoSelecaoCompletaStrategy
    │                               ├── CotacaoSelecaoPorPrecoStrategy
    │                               └── CotacaoSelecaoPorLeadTimeStrategy
    │
    └── delega seleção para → estratégia atual
```

O `CotacaoServico` usa a estratégia configurada (padrão: `CotacaoSelecaoCompletaStrategy`) ou permite usar uma estratégia específica por chamada.

### Exemplo de Uso

```java
CotacaoServico servico = new CotacaoServico(repositorio, produtoRepositorio, fornecedorRepositorio);

Cotacao melhor = servico.obterMaisVantajosa(produtoId);

servico.definirEstrategiaSelecao(new CotacaoSelecaoPorPrecoStrategy());
Cotacao maisBarata = servico.obterMaisVantajosa(produtoId);

Cotacao maisRapida = servico.obterMaisVantajosa(produtoId, new CotacaoSelecaoPorLeadTimeStrategy());
```

### Onde é Utilizado

- **CotacaoServico**: Usa a estratégia para selecionar a melhor cotação quando `obterMaisVantajosa()` é chamado
- **PedidoServico**: Chama `cotacaoServico.obterMaisVantajosa()` quando não há cotação específica do fornecedor (linha 134), usando a estratégia configurada
- **Potencial uso**: Pode ser usado em processos de criação automática de pedidos, onde diferentes estratégias podem ser aplicadas conforme políticas de negócio

---

## Resumo dos Padrões

| Padrão | Responsável | Funcionalidade | Status |
|--------|-------------|----------------|--------|
| **Decorator** | SILVIO | Gerenciar Estoques | ✅ Implementado |
| **Template Method** | RONALDO | Gerenciar Produtos | ✅ Implementado |
| **Proxy** | BERNARDO | Gerenciar Pedidos | ✅ Implementado |
| **Observer** | RODRIGO | Emitir Alertas de Estoque Baixo | ✅ Implementado |
| **Strategy** | EDUARDO | Selecionar Cotação Mais Vantajosa | ✅ Implementado |

## Conclusão

Todos os 5 padrões de design foram implementados conforme solicitado, cada um aplicado em uma funcionalidade diferente do sistema, sem repetição. Cada padrão foi escolhido estrategicamente para resolver problemas específicos do domínio, proporcionando:

- **Extensibilidade**: Facilita adicionar novas funcionalidades sem modificar código existente
- **Manutenibilidade**: Separa responsabilidades e facilita manutenção
- **Flexibilidade**: Permite diferentes configurações e comportamentos conforme necessidade
- **Testabilidade**: Facilita criação de testes unitários e de integração
- **Qualidade de código**: Segue princípios SOLID e boas práticas de design

