# language: pt
Funcionalidade: Registrar Movimentacoes de Estoque

  # H20: Registrar movimentacoes manuais
  Cenario: Registrar entrada de forma manual
    Dado que existe um estoque de movimentacao chamado "Estoque A"
    E existe um produto chamado "Produto X"
    Quando o cliente registra uma entrada de 100 unidades do produto
    Entao o saldo do estoque apos a movimentacao deve aumentar em 100 unidades
    E uma movimentacao do tipo ENTRADA deve ser criada

  # R1H20: Entradas geradas automaticamente apos recebimento de pedido
  Cenario: Registrar entrada automatica apos recebimento de pedido
    Dado que existe um pedido recebido para o produto "Produto X" com 50 unidades
    E o pedido esta associado ao estoque "Estoque A"
    Quando o sistema processa o recebimento do pedido
    Entao o sistema deve gerar automaticamente uma movimentacao do tipo ENTRADA
    E o saldo do estoque apos a movimentacao deve aumentar em 50 unidades

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
  Cenario: Visualizar historico completo
    Dado que existem 10 movimentacoes registradas para o produto
    Quando o cliente visualiza o historico do produto
    Entao o sistema deve exibir todas as 10 movimentacoes
    E cada movimentacao deve conter data, tipo, quantidade e responsavel

