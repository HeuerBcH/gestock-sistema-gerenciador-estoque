# language: pt
Funcionalidade: Gerenciar Pedidos
  Como cliente
  Desejo gerenciar pedidos de compra
  Para reabastecer meus estoques

  # H11: Como cliente, desejo criar pedidos de compra
  Cenário: Criar pedido de compra com sucesso
    Dado que existe um fornecedor "Fornecedor A" ativo
    E existe um produto "Produto X" com cotação válida
    Quando eu crio um pedido com 100 unidades do produto
    Então o pedido deve ser criado com sucesso
    E o status do pedido deve ser "CRIADO"
    E a data prevista de entrega deve ser calculada com base no lead time

  # R1H11: O pedido só pode ser criado se existir uma cotação válida
  Cenário: Tentar criar pedido sem cotação válida
    Dado que existe um fornecedor "Fornecedor A"
    E existe um produto "Produto X" sem cotações
    Quando eu tento criar um pedido para o produto
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Nenhuma cotação encontrada para o produto"

  Cenário: Criar pedido com múltiplos itens
    Dado que existe um fornecedor "Fornecedor A"
    E existem os seguintes produtos com cotações:
      | produto    | preco | prazo |
      | Produto X  | 50.00 | 10    |
      | Produto Y  | 75.00 | 15    |
    Quando eu crio um pedido com os seguintes itens:
      | produto   | quantidade |
      | Produto X | 50         |
      | Produto Y | 30         |
    Então o pedido deve conter 2 itens
    E o valor total dos itens deve ser calculado corretamente

  # R2H11: O pedido deve registrar a data prevista de entrega com base no Lead Time
  Cenário: Verificar cálculo da data prevista de entrega
    Dado que existe um fornecedor com lead time de "10" dias
    E existe um produto com cotação válida
    Quando eu crio um pedido hoje
    Então a data prevista de entrega deve ser "10" dias a partir de hoje

  # H12: Como cliente, desejo cancelar pedidos ainda não recebidos
  Cenário: Cancelar pedido em estado CRIADO
    Dado que existe um pedido no estado "CRIADO"
    Quando eu cancelo o pedido
    Então o pedido deve ser cancelado com sucesso
    E o status do pedido deve ser "CANCELADO"

  Cenário: Cancelar pedido em estado ENVIADO
    Dado que existe um pedido no estado "ENVIADO"
    Quando eu cancelo o pedido
    Então o pedido deve ser cancelado com sucesso
    E o status do pedido deve ser "CANCELADO"

  # R1H12: Pedidos com status "Em transporte" não podem ser cancelados
  Cenário: Tentar cancelar pedido concluído
    Dado que existe um pedido no estado "CONCLUIDO"
    Quando eu tento cancelar o pedido
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Pedido CONCLUIDO não pode ser cancelado"

  # H13: Como cliente, desejo confirmar o recebimento de um pedido
  Cenário: Confirmar recebimento de pedido enviado
    Dado que existe um pedido no estado "ENVIADO"
    Quando eu confirmo o recebimento do pedido
    Então o pedido deve ser marcado como "RECEBIDO"
    E o status do pedido deve ser "RECEBIDO"

  # R1H13: Ao confirmar o recebimento, o sistema gera automaticamente uma movimentação de entrada
  Cenário: Verificar geração de movimentação ao receber pedido
    Dado que existe um pedido no estado "ENVIADO" com 100 unidades
    E existe um estoque para receber o produto
    Quando eu confirmo o recebimento do pedido
    Então uma movimentação de entrada deve ser gerada
    E o saldo do estoque deve aumentar em 100 unidades

  Cenário: Tentar confirmar recebimento de pedido não enviado
    Dado que existe um pedido no estado "CRIADO"
    Quando eu tento confirmar o recebimento
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Somente pedidos ENVIADO podem ser recebidos"

  Cenário: Enviar pedido criado
    Dado que existe um pedido no estado "CRIADO" com itens
    Quando eu envio o pedido
    Então o pedido deve ser enviado com sucesso
    E o status do pedido deve ser "ENVIADO"

  Cenário: Concluir pedido após recebimento
    Dado que existe um pedido no estado "RECEBIDO"
    Quando eu concluo o pedido
    Então o pedido deve ser concluído com sucesso
    E o status do pedido deve ser "CONCLUIDO"

  Cenário: Calcular custo total do pedido
    Dado que existe um pedido com os seguintes itens:
      | produto   | quantidade | precoUnitario |
      | Produto X | 100        | 50.00         |
      | Produto Y | 50         | 75.00         |
    Quando eu calculo o custo total
    Então o valor total dos itens deve ser "8750.00"
