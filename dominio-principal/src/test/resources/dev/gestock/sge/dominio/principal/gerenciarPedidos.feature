# language: pt
Funcionalidade: Gerenciar Pedidos

  # H11: Criar pedidos de compra
  Cenario: Criar pedido de compra com sucesso
    Dado que existe um fornecedor chamado "Fornecedor A" ativo
    E existe um produto chamado "Produto X" com cotacao valida
    Quando o cliente cria um pedido com 100 unidades do produto
    Entao o pedido deve ser criado com sucesso
    E o status do pedido deve ser "CRIADO"
    E a data prevista de entrega deve ser calculada com base no lead time

  # R1H11: Pedido so pode ser criado se existir cotacao valida
  Cenario: Tentar criar pedido sem cotacao valida
    Dado que existe um fornecedor chamado "Fornecedor A"
    E existe um produto chamado "Produto X" sem cotacoes
    Quando o cliente tenta criar um pedido para o produto
    Entao o sistema deve rejeitar a operacao de pedido
    E deve exibir a mensagem de pedido "Nenhuma cotacao encontrada para o produto"

  Cenario: Criar pedido com multiplos itens
    Dado que existe um fornecedor chamado "Fornecedor A"
    E existem os seguintes produtos com cotacoes:
      | produto    | preco | prazo |
      | Produto X  | 50.00 | 10    |
      | Produto Y  | 75.00 | 15    |
    Quando o cliente cria um pedido com os seguintes itens:
      | produto   | quantidade |
      | Produto X | 50         |
      | Produto Y | 30         |
    Entao o pedido deve conter 2 itens
    E o valor total dos itens deve ser calculado corretamente

  # R2H11: Pedido registra data prevista de entrega baseada no Lead Time
  Cenario: Verificar calculo da data prevista de entrega
    Dado que existe um fornecedor com lead time de 10 dias
    E existe um produto chamado "Produto X" com cotacao valida
    Quando o cliente cria um pedido hoje
    Entao a data prevista de entrega deve ser 10 dias a partir de hoje

  # H12: Cancelar pedidos
  Cenario: Cancelar pedido em estado CRIADO
    Dado que existe um pedido no estado "CRIADO"
    Quando o cliente cancela o pedido
    Entao o pedido deve ser cancelado com sucesso
    E o status do pedido deve ser "CANCELADO"

  Cenario: Cancelar pedido em estado ENVIADO
    Dado que existe um pedido no estado "ENVIADO"
    Quando o cliente cancela o pedido
    Entao o pedido deve ser cancelado com sucesso
    E o status do pedido deve ser "CANCELADO"

  # R1H12: Pedidos com status EM TRANSPORTE nao podem ser cancelados
  Cenario: Tentar cancelar pedido em transporte
    Dado que existe um pedido no estado "EM TRANSPORTE"
    Quando o cliente tenta cancelar o pedido
    Entao o sistema deve rejeitar a operacao de pedido
    E deve exibir a mensagem de pedido "Pedido EM TRANSPORTE nao pode ser cancelado"

  Cenario: Tentar cancelar pedido concluido
    Dado que existe um pedido no estado "CONCLUIDO"
    Quando o cliente tenta cancelar o pedido
    Entao o sistema deve rejeitar a operacao de pedido
    E deve exibir a mensagem de pedido "Pedido CONCLUIDO nao pode ser cancelado"

  # H13: Confirmar recebimento de pedidos
  Cenario: Confirmar recebimento de pedido enviado
    Dado que existe um pedido no estado "ENVIADO"
    Quando o cliente confirma o recebimento do pedido
    Entao o pedido deve ser marcado como "RECEBIDO"
    E o status do pedido deve ser "RECEBIDO"

  # R1H13: Confirmar recebimento gera movimentacao de entrada
  Cenario: Verificar geracao de movimentacao ao receber pedido
    Dado que existe um pedido no estado "ENVIADO" com 100 unidades
    E existe um estoque para receber o produto
    Quando o cliente confirma o recebimento do pedido
    Entao uma movimentacao de entrada deve ser gerada
    E o saldo do estoque do pedido deve aumentar em 100 unidades

  Cenario: Tentar confirmar recebimento de pedido nao enviado
    Dado que existe um pedido no estado "CRIADO"
    Quando o cliente tenta confirmar o recebimento
    Entao o sistema deve rejeitar a operacao de pedido
    E deve exibir a mensagem de pedido "Somente pedidos ENVIADO podem ser recebidos"

  Cenario: Enviar pedido criado
    Dado que existe um pedido no estado "CRIADO" com itens
    Quando o cliente envia o pedido
    Entao o pedido deve ser enviado com sucesso
    E o status do pedido deve ser "ENVIADO"

  Cenario: Concluir pedido apos recebimento
    Dado que existe um pedido no estado "RECEBIDO"
    Quando o cliente conclui o pedido
    Entao o pedido deve ser concluido com sucesso
    E o status do pedido deve ser "CONCLUIDO"