# language: pt
Funcionalidade: Gerenciamento de Movimentação
  Como um usuário do sistema
  Eu quero registrar movimentações de produtos
  Para controlar entradas e saídas de estoque

  Contexto:
    Dado que existe um produto cadastrado para movimentação
    E que existe um estoque cadastrado para movimentação

  Cenário: Registrar movimentação de entrada
    Quando eu registro uma movimentação de tipo ENTRADA com quantidade 50, motivo "Recebimento de pedido" e responsável "João Silva"
    Então a movimentação deve ser registrada com sucesso
    E a movimentação deve ter tipo ENTRADA
    E a movimentação deve ter quantidade 50

  Cenário: Registrar movimentação de saída
    Quando eu registro uma movimentação de tipo SAIDA com quantidade 30, motivo "Venda" e responsável "Maria Santos"
    Então a movimentação deve ser registrada com sucesso
    E a movimentação deve ter tipo SAIDA
    E a movimentação deve ter quantidade 30

  Cenário: Tentar registrar movimentação com produto inexistente
    Dado que não existe o produto especificado para movimentação
    Quando eu tento registrar uma movimentação de tipo ENTRADA com quantidade 50, motivo "Recebimento" e responsável "João"
    Então deve ocorrer um erro informando que o produto da movimentação não foi encontrado

  Cenário: Tentar registrar movimentação com estoque inexistente
    Dado que não existe o estoque especificado
    Quando eu tento registrar uma movimentação de tipo ENTRADA com quantidade 50, motivo "Recebimento" e responsável "João"
    Então deve ocorrer um erro informando que o estoque não foi encontrado

