# Explicação Detalhada: Por Que os Erros Aconteceram

## 📋 Resumo Executivo

Os erros ocorreram porque implementamos o **Template Method Pattern** no `EstoqueServico`, adicionando uma nova dependência (`AtualizacaoEstoqueTemplate`), mas os **testes não foram atualizados** para refletir essa mudança arquitetural.

---

## 🔄 1. Estado ANTES da Implementação

### Código Original do `EstoqueServico`

```java
public class EstoqueServico {
    private final EstoqueRepositorio estoqueRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    
    // Construtores disponíveis
    public EstoqueServico(EstoqueRepositorio estoqueRepositorio) {
        this(estoqueRepositorio, null);
    }
    
    public EstoqueServico(EstoqueRepositorio estoqueRepositorio, PedidoRepositorio pedidoRepositorio) {
        this.estoqueRepositorio = estoqueRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
    }
    
    // Método atualizar simples
    public void atualizar(Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
        estoqueRepositorio.salvar(estoque);
    }
}
```

### Como os Testes Funcionavam

```java
// ✅ Funcionava perfeitamente
EstoqueServico estoqueServico = new EstoqueServico(repo);
EstoqueServico estoqueServico = new EstoqueServico(repo, repo);
```

**Nenhum teste passava um terceiro parâmetro** porque não existia essa necessidade.

---

## 🎯 2. O Que Mudou com o Template Method

### Nova Estrutura do `EstoqueServico`

```java
public class EstoqueServico {
    private final EstoqueRepositorio estoqueRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final AtualizacaoEstoqueTemplate atualizacaoTemplate; // ← NOVA DEPENDÊNCIA
    
    // Construtores mantidos para compatibilidade
    public EstoqueServico(EstoqueRepositorio estoqueRepositorio) {
        this(estoqueRepositorio, null); // Chama o construtor de 2 parâmetros
    }
    
    public EstoqueServico(EstoqueRepositorio estoqueRepositorio, PedidoRepositorio pedidoRepositorio) {
        this(estoqueRepositorio, pedidoRepositorio, null); // ← Chama o novo construtor de 3 parâmetros
    }
    
    // NOVO CONSTRUTOR com 3 parâmetros
    public EstoqueServico(EstoqueRepositorio estoqueRepositorio, 
                         PedidoRepositorio pedidoRepositorio, 
                         AtualizacaoEstoqueTemplate atualizacaoTemplate) { // ← NOVO PARÂMETRO
        this.estoqueRepositorio = estoqueRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.atualizacaoTemplate = atualizacaoTemplate;
    }
    
    // Método atualizar agora delega para o template
    public void atualizar(Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
        
        if (atualizacaoTemplate != null) {
            atualizacaoTemplate.atualizar(estoque.getId()); // ← Delega para template
        } else {
            estoqueRepositorio.salvar(estoque); // Fallback para compatibilidade
        }
    }
}
```

### Por Que Adicionamos o Template?

O Template Method Pattern foi implementado para:

1. **Definir um fluxo fixo** de atualização de estoque (8 passos)
2. **Integrar com Observer Pattern** para notificar observers após atualização
3. **Permitir extensibilidade** sem modificar o `EstoqueServico`
4. **Garantir consistência** na ordem das operações

---

## ❌ 3. Por Que os Testes Quebraram

### O Erro Específico

```java
// ❌ ERRO: Tentando passar Repositorio onde se espera AtualizacaoEstoqueTemplate
estoqueServico = new EstoqueServico(repo, null, repo);
//                                      ↑    ↑     ↑
//                                      |    |     └─ Esperava: AtualizacaoEstoqueTemplate
//                                      |    |        Recebeu: Repositorio (tipo incompatível)
//                                      |    └─ PedidoRepositorio (OK, pode ser null)
//                                      └─ EstoqueRepositorio (OK)
```

### Análise do Erro

**Mensagem de erro:**
```
incompatible types: dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio 
cannot be converted to dev.gestock.sge.dominio.principal.estoque.AtualizacaoEstoqueTemplate
```

**Causa raiz:**
- O teste estava tentando passar um `Repositorio` (que implementa `EstoqueRepositorio`) como terceiro parâmetro
- O construtor agora espera um `AtualizacaoEstoqueTemplate` como terceiro parâmetro
- **Incompatibilidade de tipos**: `Repositorio` ≠ `AtualizacaoEstoqueTemplate`

### Por Que Alguns Testes Não Quebraram?

Alguns testes continuaram funcionando porque usavam os construtores antigos:

```java
// ✅ Continua funcionando (usa construtor de 1 parâmetro)
EstoqueServico estoqueServico = new EstoqueServico(repo);

// ✅ Continua funcionando (usa construtor de 2 parâmetros)
EstoqueServico estoqueServico = new EstoqueServico(repo, repo);
```

Esses construtores foram mantidos para **compatibilidade retroativa**, mas agora delegam para o construtor de 3 parâmetros passando `null` como template.

---

## 🔍 4. Por Que Isso Não Foi Detectado Antes?

### Razões Técnicas

1. **Refatoração com Breaking Change**
   - Mudamos a assinatura do construtor (adicionamos um novo parâmetro)
   - Isso é um **breaking change** para código que já usava 3 parâmetros
   - Mas como nenhum teste usava 3 parâmetros antes, não quebrou imediatamente

2. **Compatibilidade Retroativa Parcial**
   - Mantivemos os construtores antigos (1 e 2 parâmetros)
   - Mas alguns testes foram escritos assumindo que podiam passar qualquer coisa como terceiro parâmetro
   - Esses testes quebraram

3. **Falta de Compilação Imediata**
   - Os testes só foram compilados depois da implementação
   - Se tivéssemos compilado imediatamente, teríamos detectado o erro antes

4. **Assunção Incorreta nos Testes**
   - Alguns testes assumiram que o terceiro parâmetro poderia ser um `Repositorio`
   - Mas na verdade, o terceiro parâmetro sempre deveria ser um `AtualizacaoEstoqueTemplate`

---

## 🎓 5. Relação com Princípios de Design

### Dependency Inversion Principle (DIP)

**Antes:**
```java
// EstoqueServico dependia diretamente de EstoqueRepositorio
public void atualizar(Estoque estoque) {
    estoqueRepositorio.salvar(estoque); // Dependência direta
}
```

**Depois:**
```java
// EstoqueServico agora depende de uma abstração (AtualizacaoEstoqueTemplate)
public void atualizar(Estoque estoque) {
    if (atualizacaoTemplate != null) {
        atualizacaoTemplate.atualizar(estoque.getId()); // Dependência de abstração
    }
}
```

### Open/Closed Principle (OCP)

- **Aberto para extensão**: Podemos criar novas implementações de `AtualizacaoEstoqueTemplate`
- **Fechado para modificação**: Não precisamos modificar `EstoqueServico` para adicionar novos comportamentos

### Single Responsibility Principle (SRP)

- **Antes**: `EstoqueServico` tinha a responsabilidade de atualizar estoque diretamente
- **Depois**: `EstoqueServico` orquestra, `AtualizacaoEstoqueTemplate` implementa o fluxo

---

## 📊 6. Diagrama da Mudança

### ANTES

```
[Teste]
   ↓
[EstoqueServico]
   ↓
[EstoqueRepositorio]
   ↓
[Repositorio (em memória)]
```

### DEPOIS

```
[Teste]
   ↓
[EstoqueServico]
   ↓
[AtualizacaoEstoqueTemplate] ← NOVO
   ↓
[AtualizacaoEstoquePadrao] ← Implementação concreta
   ↓
[EstoqueRepositorio]
   ↓
[Repositorio (em memória)]
```

---

## ✅ 7. Solução Aplicada

### Correção nos Testes

```java
// ❌ ANTES (causava erro)
estoqueServico = new EstoqueServico(repo, null, repo);

// ✅ DEPOIS (corrigido)
AtualizacaoEstoqueTemplate template = new AtualizacaoEstoquePadrao(repo, null);
template.registrarObserver(alertaServico); // Onde necessário
estoqueServico = new EstoqueServico(repo, null, template);
```

### Arquivos Corrigidos

1. `EmitirAlertasEstoqueBaixoFuncionalidade.java` - Também registra observer
2. `CalcularROPFuncionalidade.java`
3. `GerenciarEstoqueFuncionalidade.java`
4. `ReservarEstoqueFuncionalidade.java` (2 ocorrências)
5. `GerenciarProdutosFuncionalidade.java`
6. `RegistrarMovimentacaoFuncionalidade.java`
7. `TransferirProdutosFuncionalidade.java`

---

## 🎯 8. Lições Aprendidas

### Para Evitar Erros Similares no Futuro

1. **Compilar imediatamente após refatorações**
   - Sempre executar `mvn test-compile` após mudanças arquiteturais

2. **Atualizar testes junto com código de produção**
   - Quando mudamos a assinatura de um construtor, devemos atualizar todos os testes

3. **Usar tipos explícitos**
   - Evitar passar tipos genéricos onde se espera tipos específicos

4. **Manter compatibilidade retroativa conscientemente**
   - Se mantivermos construtores antigos, garantir que funcionem corretamente

5. **Testes como documentação**
   - Os testes devem refletir o uso correto da API

---

## 📝 Conclusão

Os erros aconteceram porque:

1. ✅ Implementamos o Template Method Pattern corretamente
2. ✅ Mantivemos compatibilidade retroativa parcial
3. ❌ Mas não atualizamos todos os testes que usavam a nova assinatura
4. ❌ Alguns testes assumiram tipos incorretos para o terceiro parâmetro

A solução foi **criar instâncias corretas de `AtualizacaoEstoqueTemplate`** e passá-las aos construtores, garantindo que os testes refletem a nova arquitetura.

---

**Data da correção**: 2025-12-11  
**Padrão implementado**: Template Method + Observer  
**Status**: ✅ Resolvido - Todos os testes compilam corretamente

