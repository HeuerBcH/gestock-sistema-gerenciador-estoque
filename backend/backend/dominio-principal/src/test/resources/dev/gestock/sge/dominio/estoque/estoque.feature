# language: pt
Funcionalidade: Gerenciamento de Estoque
  Como um usuário do sistema
  Eu quero gerenciar estoques
  Para que possam armazenar produtos

  Contexto:
    Dado que não existe nenhum estoque cadastrado

  Cenário: Criar estoque com dados válidos
    Quando eu crio um estoque com nome "Centro de Distribuição", endereço "Rua A, 123" e capacidade 10000
    Então o estoque deve ser criado com sucesso
    E o estoque deve ter o nome "Centro de Distribuição"
    E o estoque deve ter o endereço "Rua A, 123"
    E o estoque deve ter capacidade de 10000 unidades

  Cenário: Tentar criar estoque com endereço duplicado
    Dado que existe um estoque cadastrado com endereço "Rua A, 123"
    Quando eu tento criar um estoque com nome "Armazém Rio", endereço "Rua A, 123" e capacidade 5000
    Então deve ocorrer um erro informando que já existe um estoque neste endereço

  Cenário: Tentar criar estoque com nome duplicado
    Dado que existe um estoque cadastrado com nome "Centro de Distribuição"
    Quando eu tento criar um estoque com nome "Centro de Distribuição", endereço "Rua B, 456" e capacidade 5000
    Então deve ocorrer um erro informando que já existe um estoque com este nome

  Cenário: Tentar criar estoque com capacidade inválida
    Quando eu tento criar um estoque com nome "Armazém", endereço "Rua C, 789" e capacidade 0
    Então deve ocorrer um erro informando que a capacidade é inválida

  Cenário: Ativar estoque
    Dado que existe um estoque cadastrado com status INATIVO
    Quando eu ativo o estoque
    Então o estoque deve ter status ATIVO

  Cenário: Inativar estoque
    Dado que existe um estoque cadastrado com status ATIVO
    Quando eu inativo o estoque
    Então o estoque deve ter status INATIVO

  Cenário: Remover estoque sem produtos e sem pedidos
    Dado que existe um estoque cadastrado
    E que o estoque não possui produtos
    E que o estoque não possui pedidos em andamento
    Quando eu removo o estoque
    Então o estoque deve ser removido com sucesso

  Cenário: Tentar remover estoque com produtos
    Dado que existe um estoque cadastrado
    E que o estoque possui produtos
    Quando eu tento remover o estoque
    Então deve ocorrer um erro informando que não é possível remover o estoque pois ele ainda possui produtos

  Cenário: Tentar remover estoque com pedidos em andamento
    Dado que existe um estoque cadastrado
    E que o estoque não possui produtos
    E que o estoque possui pedidos em andamento
    Quando eu tento remover o estoque
    Então deve ocorrer um erro informando que não é possível remover o estoque pois ele possui pedidos em andamento

  Cenário: Tentar diminuir capacidade abaixo da ocupação atual
    Dado que existe um estoque cadastrado com capacidade 10000
    E que o estoque possui ocupação atual de 5000 unidades
    Quando eu tento atualizar a capacidade do estoque para 3000
    Então deve ocorrer um erro informando que não é possível diminuir a capacidade abaixo da ocupação atual

  Cenário: Aumentar capacidade do estoque
    Dado que existe um estoque cadastrado com capacidade 10000
    E que o estoque possui ocupação atual de 5000 unidades
    Quando eu atualizo a capacidade do estoque para 15000
    Então o estoque deve ter capacidade de 15000 unidades

