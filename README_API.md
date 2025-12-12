# API REST - Sistema Gestock

## 📋 Índice
- [Base URL](#base-url)
- [Autenticação](#autenticação)
- [1. Gerenciar Estoques](#1-gerenciar-estoques)
- [2. Gerenciar Fornecedores](#2-gerenciar-fornecedores)
- [3. Gerenciar Produtos](#3-gerenciar-produtos)
- [4. Gerenciar Pedidos](#4-gerenciar-pedidos)
- [5. Calcular ROP](#5-calcular-rop)
- [6. Emitir Alertas](#6-emitir-alertas)
- [7. Selecionar Cotação](#7-selecionar-cotação)
- [8. Registrar Movimentações](#8-registrar-movimentações)
- [9. Transferir Produtos](#9-transferir-produtos)
- [10. Reservar Estoque](#10-reservar-estoque)

---

## Base URL
```
http://localhost:8080/api
```

## Autenticação
*Nota: Autenticação será implementada futuramente*

---

## 1. Gerenciar Estoques

### H1: Cadastrar Estoques

#### `POST /estoques`
Cadastra um novo estoque.

**Request Body:**
```json
{
  "clienteId": 1,
  "nome": "Estoque Central",
  "endereco": "Rua das Flores, 123 - Centro - São Paulo/SP",
  "capacidade": 10000
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "clienteId": 1,
  "nome": "Estoque Central",
  "endereco": "Rua das Flores, 123 - Centro - São Paulo/SP",
  "capacidade": 10000,
  "ativo": true
}
```

**Regras:**
- R1H1: Cada estoque deve pertencer a um único cliente
- R2H1: Não pode haver mais de um estoque cadastrado em um mesmo endereço
- R3H1: Dois ou mais estoques não podem ter o mesmo nome

---

### H2: Inativar Estoques

#### `PUT /estoques/{id}/inativar`
Inativa um estoque.

**Response:** `200 OK`
```json
{
  "id": 1,
  "ativo": false
}
```

**Regras:**
- R1H2: Um estoque que ainda possui produtos não pode ser removido
- R2H2: Um estoque que possui um pedido alocado em andamento não pode ser removido

**Erro:** `400 Bad Request` - "Estoque com produtos não pode ser inativado"

---

### H3: Editar Parâmetros de Estoque

#### `PUT /estoques/{id}`
Atualiza os parâmetros de um estoque.

**Request Body:**
```json
{
  "nome": "Estoque Central Atualizado",
  "endereco": "Rua das Flores, 123 - Centro - São Paulo/SP",
  "capacidade": 12000
}
```

**Response:** `200 OK`

**Regras:**
- R1H3: O tamanho de um estoque não pode ser diminuído caso o mesmo esteja com produtos ocupando a capacidade máxima

---

### H4: Pesquisar e Visualizar Estoques

#### `GET /estoques`
Lista todos os estoques.

**Query Parameters:**
- `clienteId` (opcional): Filtrar por cliente
- `nome` (opcional): Filtrar por nome
- `endereco` (opcional): Filtrar por endereço
- `ativo` (opcional): Filtrar por status (true/false)

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "clienteId": 1,
    "nome": "Estoque Central",
    "endereco": "Rua das Flores, 123",
    "capacidade": 10000,
    "ativo": true
  }
]
```

#### `GET /estoques/{id}`
Obtém detalhes de um estoque específico.

**Response:** `200 OK`
```json
{
  "id": 1,
  "clienteId": 1,
  "nome": "Estoque Central",
  "endereco": "Rua das Flores, 123",
  "capacidade": 10000,
  "ativo": true,
  "saldos": [...],
  "movimentacoes": [...]
}
```

**Regras:**
- R1H4: Não deve ser possível pesquisar estoques se não houver estoques cadastrados
- R2H4: Deve ser possível pesquisar estoques por mais parâmetros além do nome

---

## 2. Gerenciar Fornecedores

### H5: Cadastrar Fornecedores

#### `POST /fornecedores`
Cadastra um novo fornecedor.

**Request Body:**
```json
{
  "nome": "TechSupply Brasil",
  "cnpj": "12.345.678/0001-11",
  "contato": "vendas@techsupply.com.br",
  "leadTimeMedio": 7
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "nome": "TechSupply Brasil",
  "cnpj": "12.345.678/0001-11",
  "contato": "vendas@techsupply.com.br",
  "leadTimeMedio": 7,
  "ativo": true
}
```

#### `POST /fornecedores/{id}/cotacoes`
Registra uma cotação para um produto.

**Request Body:**
```json
{
  "produtoId": 1,
  "preco": 3500.00,
  "prazoDias": 7
}
```

**Response:** `201 Created`

**Regras:**
- R1H5: Cada fornecedor deve possuir uma cotação vinculada a um produto
- R2H5: O Lead Time informado deve ser um número positivo (dias)

---

### H6: Atualizar Informações de Fornecedores

#### `PUT /fornecedores/{id}`
Atualiza dados cadastrais do fornecedor.

**Request Body:**
```json
{
  "nome": "TechSupply Brasil Atualizado",
  "contato": "novo@techsupply.com.br"
}
```

#### `PUT /fornecedores/{id}/lead-time`
Recalibra o lead time com base no histórico de entregas.

**Request Body:**
```json
{
  "historicoEntregasDias": [8, 9, 7, 10, 8]
}
```

**Response:** `200 OK`
```json
{
  "leadTimeMedio": 8
}
```

**Regras:**
- R1H6: Alterar o Lead Time de um fornecedor recalcula o ponto de ressuprimento dos produtos associados

---

### H7: Inativar Fornecedores

#### `PUT /fornecedores/{id}/inativar`
Inativa um fornecedor.

**Response:** `200 OK`

**Regras:**
- R1H7: Um fornecedor não pode ser inativado se houver pedidos pendentes com ele

**Erro:** `400 Bad Request` - "Fornecedor com pedidos pendentes não pode ser inativado"

---

#### `GET /fornecedores`
Lista todos os fornecedores.

**Query Parameters:**
- `ativo` (opcional): Filtrar por status

#### `GET /fornecedores/{id}`
Obtém detalhes de um fornecedor.

#### `GET /fornecedores/{id}/cotacoes`
Lista todas as cotações do fornecedor.

---

## 3. Gerenciar Produtos

### H8: Cadastrar Produtos

#### `POST /produtos`
Cadastra um novo produto.

**Request Body:**
```json
{
  "codigo": "PROD-001",
  "nome": "Notebook Dell Inspiron 15",
  "unidadePeso": "kg",
  "peso": 2.5,
  "perecivel": false
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "codigo": "PROD-001",
  "nome": "Notebook Dell Inspiron 15",
  "unidadePeso": "kg",
  "peso": 2.5,
  "perecivel": false,
  "ativo": true
}
```

**Regras:**
- R1H8: Cada produto deve possuir um código único dentro do catálogo do cliente
- R2H8: Um produto pode ser fornecido por diferentes fornecedores
- R3H8: Todo produto deve estar vinculado a pelo menos um estoque ativo

---

### H9: Editar Produtos

#### `PUT /produtos/{id}`
Atualiza informações do produto.

**Request Body:**
```json
{
  "nome": "Notebook Dell Inspiron 15 Atualizado",
  "unidadePeso": "kg",
  "peso": 2.5
}
```

**Response:** `200 OK`

**Regras:**
- R1H9: Alterações de especificações do produto não devem afetar as cotações existentes

---

### H10: Inativar Produtos

#### `PUT /produtos/{id}/inativar`
Inativa um produto.

**Response:** `200 OK`

**Regras:**
- R1H10: Um produto não pode ser inativado se houver saldo positivo em qualquer estoque ou pedidos em andamento
- R2H10: Ao inativar um produto, todas as novas cotações e pedidos para ele devem ser bloqueados

**Erro:** `400 Bad Request` - "Produto com saldo positivo não pode ser inativado"

---

#### `GET /produtos`
Lista todos os produtos.

**Query Parameters:**
- `codigo` (opcional): Filtrar por código
- `nome` (opcional): Filtrar por nome
- `ativo` (opcional): Filtrar por status

#### `GET /produtos/{id}`
Obtém detalhes de um produto.

---

## 4. Gerenciar Pedidos

### H11: Criar Pedidos de Compra

#### `POST /pedidos`
Cria um novo pedido de compra.

**Request Body:**
```json
{
  "clienteId": 1,
  "fornecedorId": 1,
  "estoqueId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 10,
      "precoUnitario": 3500.00
    }
  ]
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "clienteId": 1,
  "fornecedorId": 1,
  "estoqueId": 1,
  "dataCriacao": "2024-01-15",
  "dataPrevistaEntrega": "2024-01-22",
  "status": "CRIADO",
  "itens": [...],
  "custo": {
    "valorItens": 35000.00,
    "frete": 500.00,
    "custosLogisticos": 200.00,
    "valorTotal": 35700.00
  }
}
```

**Regras:**
- R1H11: O pedido só pode ser criado se existir uma cotação válida para o produto
- R2H11: O pedido deve registrar a data prevista de entrega com base no Lead Time do fornecedor

---

### H12: Cancelar Pedidos

#### `PUT /pedidos/{id}/cancelar`
Cancela um pedido.

**Response:** `200 OK`
```json
{
  "id": 1,
  "status": "CANCELADO"
}
```

**Regras:**
- R1H12: Pedidos com status "Em transporte" não podem ser cancelados

**Erro:** `400 Bad Request` - "Pedido EM TRANSPORTE não pode ser cancelado"

---

### H13: Confirmar Recebimento de Pedidos

#### `PUT /pedidos/{id}/receber`
Confirma o recebimento de um pedido.

**Response:** `200 OK`
```json
{
  "id": 1,
  "status": "RECEBIDO"
}
```

**Regras:**
- R1H13: Ao confirmar o recebimento, o sistema gera automaticamente uma movimentação de entrada

---

#### `PUT /pedidos/{id}/enviar`
Envia o pedido ao fornecedor.

**Response:** `200 OK`
```json
{
  "id": 1,
  "status": "ENVIADO"
}
```

#### `PUT /pedidos/{id}/iniciar-transporte`
Marca o pedido como em transporte.

**Response:** `200 OK`
```json
{
  "id": 1,
  "status": "EM_TRANSPORTE"
}
```

#### `PUT /pedidos/{id}/concluir`
Conclui o pedido após recebimento.

**Response:** `200 OK`
```json
{
  "id": 1,
  "status": "CONCLUIDO"
}
```

#### `GET /pedidos`
Lista todos os pedidos.

**Query Parameters:**
- `clienteId` (opcional): Filtrar por cliente
- `fornecedorId` (opcional): Filtrar por fornecedor
- `status` (opcional): Filtrar por status

#### `GET /pedidos/{id}`
Obtém detalhes de um pedido.

---

## 5. Calcular ROP

### H14: Calcular ROP Automaticamente

#### `POST /estoques/{estoqueId}/produtos/{produtoId}/rop`
Calcula o ROP para um produto em um estoque.

**Request Body:**
```json
{
  "consumoMedio": 10.0,
  "leadTimeDias": 7,
  "estoqueSeguranca": 20
}
```

**Response:** `200 OK`
```json
{
  "estoqueId": 1,
  "produtoId": 1,
  "consumoMedio": 10.0,
  "leadTimeDias": 7,
  "estoqueSeguranca": 20,
  "valorROP": 90
}
```

**Regras:**
- R1H14: O ROP é calculado pela fórmula: ROP = (Consumo Médio Diário × Lead Time) + Estoque de Segurança
- R2H14: O histórico deve considerar o consumo médio dos últimos 90 dias

---

### H15: Visualizar Valores de ROP

#### `GET /estoques/{estoqueId}/rop`
Lista todos os ROPs de um estoque.

**Response:** `200 OK`
```json
[
  {
    "produtoId": 1,
    "produtoNome": "Notebook Dell",
    "consumoMedio": 10.0,
    "leadTimeDias": 7,
    "estoqueSeguranca": 20,
    "valorROP": 90
  }
]
```

#### `GET /produtos/{produtoId}/rop`
Lista todos os ROPs de um produto em diferentes estoques.

**Regras:**
- R1H15: Produtos sem dados históricos suficientes devem usar um ROP inicial padrão

---

## 6. Emitir Alertas

### H16: Ser Notificado ao Atingir ROP

#### `GET /alertas`
Lista todos os alertas ativos.

**Query Parameters:**
- `ativo` (opcional): Filtrar por status (padrão: true)
- `estoqueId` (opcional): Filtrar por estoque
- `produtoId` (opcional): Filtrar por produto

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "produtoId": 2,
    "produtoNome": "Mouse Logitech",
    "estoqueId": 1,
    "estoqueNome": "Estoque Central",
    "fornecedorSugeridoId": 2,
    "fornecedorSugeridoNome": "Eletrônicos Premium",
    "dataGeracao": "2024-01-21T08:00:00",
    "ativo": true
  }
]
```

**Regras:**
- R1H16: O alerta é gerado automaticamente ao atingir o ROP
- R2H16: O alerta deve indicar o produto, o estoque e o fornecedor sugerido com menor cotação válida

---

### H17: Visualizar Lista de Alertas Ativos

#### `GET /alertas/ativos`
Lista apenas alertas ativos.

**Response:** `200 OK`

#### `PUT /alertas/{id}/desativar`
Desativa um alerta.

**Response:** `200 OK`

**Regras:**
- R1H17: Um alerta é removido automaticamente após o recebimento do pedido correspondente

---

## 7. Selecionar Cotação

### H18: Selecionar Automaticamente Menor Preço

#### `GET /produtos/{produtoId}/melhor-cotacao`
Obtém a melhor cotação para um produto.

**Response:** `200 OK`
```json
{
  "produtoId": 1,
  "fornecedorId": 2,
  "fornecedorNome": "Eletrônicos Premium",
  "preco": 3450.00,
  "prazoDias": 5,
  "leadTime": 5
}
```

**Regras:**
- R1H18: Apenas cotações com validade ativa e fornecedor ativo podem ser consideradas
- R2H18: Em caso de empate de preços, o sistema prioriza o menor Lead Time

---

### H19: Revisar e Aprovar Cotação

#### `POST /pedidos/{pedidoId}/aprovar-cotacao`
Aprova a cotação selecionada e associa ao pedido.

**Request Body:**
```json
{
  "cotacaoId": 1
}
```

**Response:** `200 OK`

**Regras:**
- R1H19: A cotação aprovada deve ser registrada como "selecionada" e associada ao pedido gerado

---

## 8. Registrar Movimentações

### H20: Registrar Movimentações de Entrada e Saída

#### `POST /estoques/{estoqueId}/movimentacoes/entrada`
Registra uma entrada manual.

**Request Body:**
```json
{
  "produtoId": 1,
  "quantidade": 100,
  "responsavel": "João Silva",
  "motivo": "Recebimento de pedido",
  "meta": {
    "lote": "LOTE-001",
    "validade": "2024-12-31"
  }
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "tipo": "ENTRADA",
  "produtoId": 1,
  "quantidade": 100,
  "dataHora": "2024-01-22T10:30:00",
  "responsavel": "João Silva",
  "motivo": "Recebimento de pedido"
}
```

#### `POST /estoques/{estoqueId}/movimentacoes/saida`
Registra uma saída manual.

**Request Body:**
```json
{
  "produtoId": 1,
  "quantidade": 50,
  "responsavel": "Pedro Costa",
  "motivo": "Venda para cliente"
}
```

**Response:** `201 Created`

**Regras:**
- R1H20: Movimentações de entrada são geradas automaticamente após o recebimento de pedidos
- R2H20: Movimentações de saída devem indicar o motivo (venda, consumo interno, perda)

---

### H21: Visualizar Histórico de Movimentações

#### `GET /estoques/{estoqueId}/movimentacoes`
Lista todas as movimentações de um estoque.

**Query Parameters:**
- `produtoId` (opcional): Filtrar por produto
- `tipo` (opcional): Filtrar por tipo (ENTRADA/SAIDA)
- `dataInicio` (opcional): Filtrar por data inicial
- `dataFim` (opcional): Filtrar por data final

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "tipo": "ENTRADA",
    "produtoId": 1,
    "produtoNome": "Notebook Dell",
    "quantidade": 100,
    "dataHora": "2024-01-22T10:30:00",
    "responsavel": "João Silva",
    "motivo": "Recebimento de pedido"
  }
]
```

#### `GET /produtos/{produtoId}/movimentacoes`
Lista todas as movimentações de um produto em todos os estoques.

**Regras:**
- R1H21: O histórico deve ser mantido por pelo menos 12 meses

---

## 9. Transferir Produtos

### H22: Transferir Produtos entre Estoques

#### `POST /transferencias`
Realiza uma transferência entre estoques.

**Request Body:**
```json
{
  "produtoId": 1,
  "estoqueOrigemId": 1,
  "estoqueDestinoId": 2,
  "quantidade": 50,
  "responsavel": "Carlos Mendes",
  "motivo": "Reabastecimento da filial"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "produtoId": 1,
  "estoqueOrigemId": 1,
  "estoqueDestinoId": 2,
  "quantidade": 50,
  "dataHora": "2024-01-16T14:00:00",
  "responsavel": "Carlos Mendes",
  "motivo": "Reabastecimento da filial"
}
```

**Regras:**
- R1H22: A transferência só pode ocorrer entre estoques pertencentes ao mesmo cliente
- R2H22: O estoque de origem deve possuir quantidade suficiente do produto
- R3H22: Ao realizar a transferência, o sistema deve registrar automaticamente uma movimentação de saída no estoque de origem e uma movimentação de entrada no estoque de destino

**Erro:** `400 Bad Request` - "Saldo disponível insuficiente"

---

### H23: Visualizar Histórico de Transferências

#### `GET /transferencias`
Lista todas as transferências.

**Query Parameters:**
- `produtoId` (opcional): Filtrar por produto
- `estoqueOrigemId` (opcional): Filtrar por estoque de origem
- `estoqueDestinoId` (opcional): Filtrar por estoque de destino
- `dataInicio` (opcional): Filtrar por data inicial
- `dataFim` (opcional): Filtrar por data final

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "produtoId": 1,
    "produtoNome": "Notebook Dell",
    "estoqueOrigemId": 1,
    "estoqueOrigemNome": "Estoque Central",
    "estoqueDestinoId": 2,
    "estoqueDestinoNome": "Filial - Zona Sul",
    "quantidade": 50,
    "dataHora": "2024-01-16T14:00:00",
    "responsavel": "Carlos Mendes",
    "motivo": "Reabastecimento da filial"
  }
]
```

**Regras:**
- R1H23: O histórico de transferências deve conter data, produto, quantidade, origem e destino
- R2H23: Não deve ser possível cancelar uma transferência após sua conclusão

---

## 10. Reservar Estoque

### H24: Reservar Automaticamente ao Gerar Pedido

#### `POST /estoques/{estoqueId}/reservas`
Cria uma reserva manual (geralmente feito automaticamente ao criar pedido).

**Request Body:**
```json
{
  "produtoId": 1,
  "quantidade": 50
}
```

**Response:** `201 Created`
```json
{
  "produtoId": 1,
  "quantidade": 50,
  "dataHora": "2024-01-15T09:00:00",
  "tipo": "RESERVA"
}
```

**Regras:**
- R1H24: Ao gerar um pedido, o sistema deve reservar no estoque a quantidade correspondente dos produtos envolvidos
- R2H24: O saldo reservado não pode ser utilizado em movimentações de saída

---

### H25: Liberar Reserva

#### `POST /estoques/{estoqueId}/reservas/liberar`
Libera uma reserva.

**Request Body:**
```json
{
  "produtoId": 1,
  "quantidade": 50
}
```

**Response:** `200 OK`

**Regras:**
- R1H25: Caso o pedido seja cancelado, as reservas associadas devem ser liberadas automaticamente
- R2H25: O sistema deve manter registro histórico das reservas e liberações para fins de auditoria

---

#### `GET /estoques/{estoqueId}/reservas`
Lista todas as reservas de um estoque.

**Query Parameters:**
- `produtoId` (opcional): Filtrar por produto
- `tipo` (opcional): Filtrar por tipo (RESERVA/LIBERACAO)

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "produtoId": 1,
    "produtoNome": "Notebook Dell",
    "quantidade": 50,
    "dataHora": "2024-01-15T09:00:00",
    "tipo": "RESERVA"
  }
]
```

---

## Códigos de Status HTTP

- `200 OK` - Requisição bem-sucedida
- `201 Created` - Recurso criado com sucesso
- `400 Bad Request` - Erro de validação ou regra de negócio
- `404 Not Found` - Recurso não encontrado
- `500 Internal Server Error` - Erro interno do servidor

---

## Exemplos de Erros

### Erro de Validação
```json
{
  "erro": "Validação falhou",
  "mensagem": "Nome do estoque é obrigatório",
  "campo": "nome"
}
```

### Erro de Regra de Negócio
```json
{
  "erro": "Regra de negócio violada",
  "mensagem": "Estoque com produtos não pode ser inativado",
  "codigo": "R1H2"
}
```

---

## Notas de Implementação

⚠️ **Status Atual:** As rotas acima representam a especificação completa da API baseada nas funcionalidades e regras de negócio definidas. A implementação dos controladores REST ainda está pendente.

**Próximos Passos:**
1. Implementar todos os controladores REST
2. Implementar validações de entrada
3. Implementar tratamento de erros padronizado
4. Adicionar documentação Swagger/OpenAPI
5. Implementar autenticação e autorização

