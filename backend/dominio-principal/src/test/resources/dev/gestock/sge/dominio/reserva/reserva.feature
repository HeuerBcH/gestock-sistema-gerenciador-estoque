# language: pt
Funcionalidade: Gerenciamento de Reserva
  Como um usuário do sistema
  Eu quero gerenciar reservas de produtos
  Para garantir disponibilidade para pedidos

  Contexto:
    Dado que existe um pedido cadastrado

  Cenário: Criar reservas automaticamente ao criar pedido
    Quando eu crio reservas para o pedido
    Então as reservas devem ser criadas com sucesso
    E cada item do pedido deve ter uma reserva ATIVA

  Cenário: Liberar reservas ao receber pedido
    Dado que existem reservas ativas para o pedido
    Quando eu libero as reservas do pedido com tipo RECEBIDO
    Então as reservas devem ter status LIBERADA
    E as reservas devem ter tipo de liberação RECEBIDO

  Cenário: Liberar reservas ao cancelar pedido
    Dado que existem reservas ativas para o pedido
    Quando eu libero as reservas do pedido com tipo CANCELADO
    Então as reservas devem ter status LIBERADA
    E as reservas devem ter tipo de liberação CANCELADO

  Cenário: Tentar liberar reserva já liberada
    Dado que existe uma reserva com status LIBERADA
    Quando eu tento liberar a reserva novamente
    Então deve ocorrer um erro informando que a reserva já está liberada

