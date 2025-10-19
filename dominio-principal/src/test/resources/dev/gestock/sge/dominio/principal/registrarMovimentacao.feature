# language: pt
Funcionalidade: Registrar Movimentações de Estoque
  Como cliente
  Desejo registrar movimentações de entrada e saída
  Para manter o saldo atualizado

  # H20: Registrar movimentações
  # R1H20: Entradas geradas automaticamente após recebimento
  Cenário: Registrar entrada manual
    Dado que existe um estoque chamado "Estoque A"
    E existe um produto chamado "Produto X"
    Quando eu registro uma entrada de 100 unidades do produto
    Então o saldo do estoque deve aumentar em 100 unidades
    E uma movimentação do tipo ENTRADA deve ser criada

  # R2H20: Saídas indicam motivo
  Cenário: Registrar saída com motivo
    Dado que existe um estoque com 200 unidades do produto
    Quando eu registro uma saída de 50 unidades com motivo "Venda"
    Então o saldo do estoque deve diminuir em 50 unidades
    E a movimentação deve conter o motivo "Venda"

  Cenário: Tentar registrar saída com saldo insuficiente
    Dado que existe um estoque com 30 unidades disponíveis do produto
    Quando eu tento registrar uma saída de 50 unidades
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Saldo disponível insuficiente"

  # H21: Visualizar histórico
  # R1H21: Histórico mantido por 12 meses
  Cenário: Visualizar histórico completo
    Dado que existem 10 movimentações registradas para o produto
    Quando eu visualizo o histórico do produto
    Então devo ver todas as 10 movimentações
    E cada movimentação deve conter data, tipo, quantidade e responsável
