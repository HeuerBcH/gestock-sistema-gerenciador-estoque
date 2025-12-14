# language: pt
Funcionalidade: Gerenciamento de Produto
  Como um usuário do sistema
  Eu quero gerenciar produtos
  Para que possam ser utilizados em pedidos e movimentações

  Contexto:
    Dado que não existe nenhum produto cadastrado

  Cenário: Criar produto com dados válidos
    Quando eu crio um produto com código "PROD001", nome "Arroz Integral 1kg", peso 1000 gramas, perecível NAO e fornecedores
    Então o produto deve ser criado com sucesso
    E o produto deve ter o código "PROD001"
    E o produto deve ter o nome "Arroz Integral 1kg"
    E o produto deve ter peso de 1000 gramas

  Cenário: Tentar criar produto com código duplicado
    Dado que existe um produto cadastrado com código "PROD001"
    Quando eu tento criar um produto com código "PROD001", nome "Feijão Preto 1kg", peso 1000 gramas, perecível NAO e fornecedores
    Então deve ocorrer um erro informando que já existe um produto com este código

  Cenário: Tentar criar produto com peso inválido
    Quando eu tento criar um produto com código "PROD002", nome "Feijão Preto 1kg", peso 0 gramas, perecível NAO e fornecedores
    Então deve ocorrer um erro informando que o peso é inválido

  Cenário: Ativar produto vinculado a estoque ativo
    Dado que existe um produto cadastrado com status INATIVO
    E que o produto está vinculado a um estoque ativo
    Quando eu ativo o produto
    Então o produto deve ter status ATIVO

  Cenário: Tentar ativar produto sem estoque ativo
    Dado que existe um produto cadastrado com status INATIVO
    E que o produto não está vinculado a nenhum estoque ativo
    Quando eu tento ativar o produto
    Então deve ocorrer um erro informando que o produto deve estar vinculado a pelo menos um estoque ativo

  Cenário: Inativar produto sem saldo e sem pedidos em andamento
    Dado que existe um produto cadastrado com status ATIVO
    E que o produto não possui saldo em estoque
    E que o produto não possui pedidos em andamento
    Quando eu inativo o produto
    Então o produto deve ter status INATIVO

  Cenário: Tentar inativar produto com saldo em estoque
    Dado que existe um produto cadastrado com status ATIVO
    E que o produto possui saldo em estoque
    Quando eu tento inativar o produto
    Então deve ocorrer um erro informando que não é possível inativar o produto pois ele possui saldo em estoque

  Cenário: Tentar inativar produto com pedidos em andamento
    Dado que existe um produto cadastrado com status ATIVO
    E que o produto não possui saldo em estoque
    E que o produto possui pedidos em andamento
    Quando eu tento inativar o produto
    Então deve ocorrer um erro informando que não é possível inativar o produto pois existem pedidos em andamento

  Cenário: Atualizar nome do produto
    Dado que existe um produto cadastrado com nome "Arroz Integral 1kg"
    Quando eu atualizo o nome do produto para "Arroz Integral Premium 1kg"
    Então o produto deve ter o nome "Arroz Integral Premium 1kg"

  Cenário: Atualizar peso do produto
    Dado que existe um produto cadastrado com peso 1000 gramas
    Quando eu atualizo o peso do produto para 1500 gramas
    Então o produto deve ter peso de 1500 gramas

