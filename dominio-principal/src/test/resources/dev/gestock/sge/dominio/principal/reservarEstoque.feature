# language: pt
Funcionalidade: Reservar Estoque para Pedidos Pendentes

  # H24: Reservar automaticamente ao gerar pedido de compra
  # R1H24: Sistema reserva automaticamente quantidades pendentes
  Cenario: Reservar estoque ao criar pedido para fornecedor
    Dado que existe um estoque chamado "Estoque X" com 200 unidades fisicas do produto "Produto Y"
    E existe um fornecedor ativo chamado "Fornecedor A"
    Quando o cliente cria um pedido de compra de 50 unidades do produto "Produto Y" para o fornecedor "Fornecedor A"
    Entao o sistema deve atualizar o estoque fisico projetado para 250 unidades
    E o saldo disponivel deve permanecer 200 unidades
    E 50 unidades devem ser marcadas como reservadas ate a conclusao do pedido

  # R2H24: Saldo reservado nao pode ser usado antes da conclusao do pedido
  Cenario: Tentar usar unidades reservadas antes do recebimento
    Dado que existe um estoque chamado "Estoque X" com 200 unidades disponiveis
    E 50 unidades estao reservadas aguardando entrega de fornecedor
    Quando o cliente tenta registrar uma saida de 230 unidades do produto "Produto Y"
    Entao o sistema deve rejeitar a operacao
    E deve exibir a mensagem "Saldo disponivel insuficiente para movimentacao"

  # H25: Liberar reserva ao concluir pedido
  # R1H25: Reservas liberadas automaticamente apos recebimento
  Cenario: Liberar reserva apos conclusao do pedido
    Dado que existe um pedido de compra de 50 unidades do produto "Produto Y" reservado no estoque
    Quando o pedido e concluido apos o recebimento do fornecedor "Fornecedor A"
    Entao o sistema deve liberar automaticamente as 50 unidades reservadas
    E o saldo disponivel deve aumentar para 250 unidades
    E o saldo reservado deve ser 0

  # R2H25: Sistema mantem registro historico de reservas
  Cenario: Registrar historico de reserva e liberacao
    Dado que foi criada uma reserva de 50 unidades para o produto "Produto Y"
    E a reserva foi liberada apos o recebimento do pedido
    Quando o cliente consulta o historico de reservas
    Entao o sistema deve exibir o registro da criacao e da liberacao da reserva
