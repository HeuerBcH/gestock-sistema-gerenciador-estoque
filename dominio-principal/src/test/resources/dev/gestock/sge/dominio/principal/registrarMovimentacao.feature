# language: pt
Funcionalidade: Registrar Movimentacoes de Estoque


  # H20: Registrar movimentacoes
  # R1H20: Entradas geradas automaticamente apos recebimento
  Cenario: Registrar entrada manual
    Dado que existe um estoque chamado "Estoque A"
    E existe um produto chamado "Produto X"
    Quando o cliente registra uma entrada de 100 unidades do produto
    Entao o saldo do estoque deve aumentar em 100 unidades
    E uma movimentacao do tipo ENTRADA deve ser criada

  # R2H20: Saidas indicam motivo
  Cenario: Registrar saida com motivo
    Dado que existe um estoque com 200 unidades do produto
    Quando o cliente registra uma saida de 50 unidades com motivo "Venda"
    Entao o saldo do estoque deve diminuir em 50 unidades
    E a movimentacao deve conter o motivo "Venda"

  Cenario: Tentar registrar saida com saldo insuficiente
    Dado que existe um estoque com 30 unidades disponiveis do produto
    Quando o cliente tenta registrar uma saida de 50 unidades
    Entao o sistema deve rejeitar a operacao
    E deve exibir a mensagem "Saldo disponivel insuficiente"

  # H21: Visualizar historico
  # R1H21: Historico mantido por 12 meses
  Cenario: Visualizar historico completo
    Dado que existem 10 movimentacoes registradas para o produto
    Quando o cliente visualiza o historico do produto
    Entao o sistema deve exibir todas as 10 movimentacoes
    E cada movimentacao deve conter data, tipo, quantidade e responsavel
