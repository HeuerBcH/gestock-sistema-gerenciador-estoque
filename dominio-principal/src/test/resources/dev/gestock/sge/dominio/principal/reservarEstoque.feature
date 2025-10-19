# language: pt
Funcionalidade: Reservar Estoque para Pedidos Pendentes
  Como cliente
  Desejo reservar estoque para pedidos pendentes
  Para evitar falta de estoque

  # H24: Reservar automaticamente ao gerar pedido
  # R1H24: Sistema reserva ao gerar pedido
  Cenário: Reservar estoque ao criar pedido
    Dado que existe um estoque com 200 unidades disponíveis
    Quando eu crio um pedido de venda com 50 unidades
    Então 50 unidades devem ser reservadas
    E o saldo disponível deve ser 150 unidades
    E o saldo físico deve permanecer 200 unidades

  # R2H24: Saldo reservado não pode ser usado em outras movimentações
  Cenário: Tentar usar saldo reservado
    Dado que existe um estoque com 100 unidades físicas
    E 80 unidades estão reservadas
    Quando eu tento registrar uma saída de 30 unidades
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Saldo disponível insuficiente"

  # H25: Liberar reserva ao cancelar pedido
  # R1H25: Reservas liberadas automaticamente
  Cenário: Liberar reserva ao cancelar pedido
    Dado que existe uma reserva de 50 unidades
    Quando o pedido é cancelado
    Então a reserva deve ser liberada
    E o saldo disponível deve aumentar em 50 unidades

  # R2H25: Sistema mantém registro histórico
  Cenário: Registrar histórico de reservas
    Dado que foi criada uma reserva
    E a reserva foi liberada
    Quando eu consulto o histórico
    Então devo ver o registro da reserva
    E devo ver o registro da liberação

  Cenário: Consumir reserva ao atender pedido
    Dado que existe uma reserva de 50 unidades
    Quando o pedido é atendido
    Então a reserva deve ser consumida
    E o saldo físico deve diminuir em 50 unidades
    E o saldo reservado deve diminuir em 50 unidades
