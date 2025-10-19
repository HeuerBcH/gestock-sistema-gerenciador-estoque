# language: pt
Funcionalidade: Reservar Estoque para Pedidos Pendentes


  # H24: Reservar automaticamente ao gerar pedido
  # R1H24: Sistema reserva ao gerar pedido
  Cenário: Reservar estoque ao criar pedido
    Dado que existe um estoque chamado "Estoque A" com 200 unidades disponíveis
    Quando o cliente cria um pedido de venda com 50 unidades do produto
    Então 50 unidades devem ser reservadas
    E o saldo disponível deve ser 150 unidades
    E o saldo físico deve permanecer 200 unidades

  # R2H24: Saldo reservado não pode ser usado em outras movimentações
  Cenário: Tentar usar saldo reservado
    Dado que existe um estoque chamado "Estoque A" com 100 unidades físicas
    E 80 unidades estão reservadas
    Quando o cliente tenta registrar uma saída de 30 unidades do produto
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Saldo disponível insuficiente"

  # H25: Liberar reserva ao cancelar pedido
  # R1H25: Reservas liberadas automaticamente
  Cenário: Liberar reserva ao cancelar pedido
    Dado que existe uma reserva de 50 unidades do produto
    Quando o pedido é cancelado
    Então a reserva deve ser liberada
    E o saldo disponível deve aumentar em 50 unidades

  # R2H25: Sistema mantém registro histórico
  Cenário: Registrar histórico de reservas
    Dado que foi criada uma reserva de 50 unidades
    E a reserva foi liberada
    Quando o cliente consulta o histórico de reservas
    Então o sistema deve exibir o registro da reserva
    E o sistema deve exibir o registro da liberação

  # Consumir reserva ao atender pedido
  Cenário: Consumir reserva ao atender pedido
    Dado que existe uma reserva de 50 unidades do produto
    Quando o pedido é atendido
    Então a reserva deve ser consumida
    E o saldo físico do estoque deve diminuir em 50 unidades
    E o saldo reservado deve diminuir em 50 unidades