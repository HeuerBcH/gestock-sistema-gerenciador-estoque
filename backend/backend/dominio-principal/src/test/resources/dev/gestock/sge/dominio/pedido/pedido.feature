# language: pt
Funcionalidade: Gerenciamento de Pedido
  Como um usuário do sistema
  Eu quero gerenciar pedidos de compra
  Para abastecer os estoques

  Contexto:
    Dado que existe um fornecedor ativo cadastrado
    E que existe um estoque ativo cadastrado
    E que existe um produto ativo cadastrado
    E que existe uma cotação aprovada para o produto

  Cenário: Criar pedido com dados válidos
    Quando eu crio um pedido com itens
    Então o pedido deve ser criado com sucesso
    E o valor total do pedido deve ser calculado corretamente
    E a data prevista do pedido deve ser calculada baseada no lead time do fornecedor

  Cenário: Tentar criar pedido com fornecedor inativo
    Dado que o fornecedor está inativo
    Quando eu tento criar um pedido com itens
    Então deve ocorrer um erro informando que o fornecedor deve estar ativo

  Cenário: Tentar criar pedido com estoque inativo
    Dado que o estoque está inativo
    Quando eu tento criar um pedido com itens
    Então deve ocorrer um erro informando que o estoque deve estar ativo

  Cenário: Tentar criar pedido excedendo capacidade do estoque
    Dado que o estoque tem capacidade 1000
    E que o pedido tem quantidade total 1500
    Quando eu tento criar um pedido com itens
    Então deve ocorrer um erro informando que a quantidade excede a capacidade do estoque

  Cenário: Confirmar recebimento do pedido
    Dado que existe um pedido cadastrado com status EM_TRANSPORTE
    Quando eu confirmo o recebimento do pedido
    Então o pedido deve ter status RECEBIDO
    E devem ser criadas movimentações de ENTRADA para cada item

  Cenário: Cancelar pedido
    Dado que existe um pedido cadastrado com status CRIADO
    Quando eu cancelo o pedido
    Então o pedido deve ter status CANCELADO

  Cenário: Tentar cancelar pedido em transporte
    Dado que existe um pedido cadastrado com status EM_TRANSPORTE
    Quando eu tento cancelar o pedido
    Então deve ocorrer um erro informando que não é possível cancelar um pedido em transporte

