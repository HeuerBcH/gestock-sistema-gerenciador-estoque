# language: pt
Funcionalidade: Gerenciar Pedidos


  # H11: Criar pedidos de compra
  Cenário: Criar pedido de compra com sucesso
    Dado que existe um fornecedor chamado "Fornecedor A" ativo
    E existe um produto chamado "Produto X" com cotação válida
    Quando o cliente cria um pedido com 100 unidades do produto
    Então o pedido deve ser criado com sucesso
    E o status do pedido deve ser "CRIADO"
    E a data prevista de entrega deve ser calculada com base no lead time

  # R1H11: Pedido só pode ser criado se existir cotação válida
  Cenário: Tentar criar pedido sem cotação válida
    Dado que existe um fornecedor chamado "Fornecedor A"
    E existe um produto chamado "Produto X" sem cotações
    Quando o cliente tenta criar um pedido para o produto
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Nenhuma cotação encontrada para o produto"

  Cenário: Criar pedido com múltiplos itens
    Dado que existe um fornecedor chamado "Fornecedor A"
    E existem os seguintes produtos com cotações:
      | produto    | preco | prazo |
      | Produto X  | 50.00 | 10    |
      | Produto Y  | 75.00 | 15    |
    Quando o cliente cria um pedido com os seguintes itens:
      | produto   | quantidade |
      | Produto X | 50         |
      | Produto Y | 30         |
    Então o pedido deve conter 2 itens
    E o valor total dos itens deve ser calculado corretamente

  # R2H11: Pedido registra data prevista de entrega baseada no Lead Time
  Cenário: Verificar cálculo da data prevista de entrega
    Dado que existe um fornecedor com lead time de 10 dias
    E existe um produto chamado "Produto X" com cotação válida
    Quando o cliente cria um pedido hoje
    Então a data prevista de entrega deve ser 10 dias a partir de hoje

  # H12: Cancelar pedidos
  Cenário: Cancelar pedido em estado CRIADO
    Dado que existe um pedido no estado "CRIADO"
    Quando o cliente cancela o pedido
    Então o pedido deve ser cancelado com sucesso
    E o status do pedido deve ser "CANCELADO"

  Cenário: Cancelar pedido em estado ENVIADO
    Dado que existe um pedido no estado "ENVIADO"
    Quando o cliente cancela o pedido
    Então o pedido deve ser cancelado com sucesso
    E o status do pedido deve ser "CANCELADO"

  # R1H12: Pedidos com status CONCLUIDO não podem ser cancelados
  Cenário: Tentar cancelar pedido concluído
    Dado que existe um pedido no estado "CONCLUIDO"
    Quando o cliente tenta cancelar o pedido
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Pedido CONCLUIDO não pode ser cancelado"

    Cenário: Tentar cancelar pedido EM TRANSPORTE
    Dado que existe um pedido no estado "EM TRANSPORTE"
    Quando o cliente tenta cancelar o ped ido
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Pedido EM TRANSPORTE não pode ser cancelado"

  # H13: Confirmar recebimento de pedidos
  Cenário: Confirmar recebimento de pedido enviado
    Dado que existe um pedido no estado "ENVIADO"
    Quando o cliente confirma o recebimento do pedido
    Então o pedido deve ser marcado como "RECEBIDO"
    E o status do pedido deve ser "RECEBIDO"

  # R1H13: Confirmar recebimento gera movimentação de entrada
  Cenário: Verificar geração de movimentação ao receber pedido
    Dado que existe um pedido no estado "ENVIADO" com 100 unidades
    E existe um estoque para receber o produto
    Quando o cliente confirma o recebimento do pedido
    Então uma movimentação de entrada deve ser gerada
    E o saldo do estoque deve aumentar em 100 unidades

  Cenário: Tentar confirmar recebimento de pedido não enviado
    Dado que existe um pedido no estado "CRIADO"
    Quando o cliente tenta confirmar o recebimento
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Somente pedidos ENVIADO podem ser recebidos"

  Cenário: Enviar pedido criado
    Dado que existe um pedido no estado "CRIADO" com itens
    Quando o cliente envia o pedido
    Então o pedido deve ser enviado com sucesso
    E o status do pedido deve ser "ENVIADO"

  Cenário: Concluir pedido após recebimento
    Dado que existe um pedido no estado "RECEBIDO"
    Quando o cliente conclui o pedido
    Então o pedido deve ser concluído com sucesso
    E o status do pedido deve ser "CONCLUIDO"

  Cenário: Calcular custo total do pedido
    Dado que existe um pedido com os seguintes itens:
      | produto   | quantidade | precoUnitario |
      | Produto X | 100        | 50.00         |
      | Produto Y | 50         | 75.00         |
    Quando o custo total do pedido é calculado
    Então o valor total dos itens deve ser 8750.00