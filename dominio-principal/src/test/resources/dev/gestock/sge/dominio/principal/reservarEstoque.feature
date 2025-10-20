# language: pt
Funcionalidade: Reservar Estoque para Pedidos Pendentes

  # H24: Reservar automaticamente ao gerar pedido
  # R1H24: Sistema reserva ao gerar pedido
  Cenario: Reservar estoque ao criar pedido
    Dado que existe um estoque chamado "Estoque A" com 200 unidades disponiveis
    Quando o cliente cria um pedido de venda com 50 unidades do produto
    Entao 50 unidades devem ser reservadas
    E o saldo disponivel deve ser 150 unidades
    E o saldo fisico deve permanecer 200 unidades

  # R2H24: Saldo reservado nao pode ser usado em outras movimentacoes
  Cenario: Tentar usar saldo reservado
    Dado que existe um estoque chamado "Estoque A" com 100 unidades fisicas
    E 80 unidades estao reservadas
    Quando o cliente tenta registrar uma saida de 30 unidades do produto
    Entao o sistema deve rejeitar a operacao de reserva
    E deve exibir a mensagem de reserva "Saldo disponivel insuficiente"

  # H25: Liberar reserva ao cancelar pedido
  # R1H25: Reservas liberadas automaticamente
  Cenario: Liberar reserva ao cancelar pedido
    Dado que existe uma reserva de 50 unidades do produto
    Quando o pedido e cancelado
    Entao a reserva deve ser liberada
    E o saldo disponivel deve aumentar em 50 unidades

  # R2H25: Sistema mantem registro historico
  Cenario: Registrar historico de reservas
    Dado que foi criada uma reserva de 50 unidades
    E a reserva foi liberada
    Quando o cliente consulta o historico de reservas
    Entao o sistema deve exibir o registro da reserva
    E o sistema deve exibir o registro da liberacao

  # Consumir reserva ao atender pedido
  Cenario: Consumir reserva ao atender pedido
    Dado que existe uma reserva de 50 unidades do produto
    Quando o pedido e atendido
    Entao a reserva deve ser consumida
    E o saldo fisico do estoque deve diminuir em 50 unidades
    E o saldo reservado deve diminuir em 50 unidades
