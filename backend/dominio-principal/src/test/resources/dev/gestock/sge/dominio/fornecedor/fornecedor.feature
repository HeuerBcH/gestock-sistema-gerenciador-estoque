# language: pt
Funcionalidade: Gerenciamento de Fornecedor
  Como um usuário do sistema
  Eu quero gerenciar fornecedores
  Para que possam ser utilizados em pedidos e cotações

  Contexto:
    Dado que não existe nenhum fornecedor cadastrado

  Cenário: Criar fornecedor com dados válidos
    Quando eu crio um fornecedor com nome "Fornecedor ABC", CNPJ "12345678000190", email "contato@abc.com", lead time 7 dias e custo 10.50
    Então o fornecedor deve ser criado com sucesso
    E o fornecedor deve ter o nome "Fornecedor ABC"
    E o fornecedor deve ter o CNPJ "12345678000190"
    E o fornecedor deve ter status ATIVO

  Cenário: Tentar criar fornecedor com CNPJ inválido
    Quando eu tento criar um fornecedor com nome "Fornecedor XYZ", CNPJ "123", email "contato@xyz.com", lead time 5 dias e custo 15.75
    Então deve ocorrer um erro informando que o CNPJ é inválido

  Cenário: Tentar criar fornecedor com email inválido
    Quando eu tento criar um fornecedor com nome "Fornecedor XYZ", CNPJ "98765432000111", email "email-invalido", lead time 5 dias e custo 15.75
    Então deve ocorrer um erro informando que o email do fornecedor é inválido

  Cenário: Tentar criar fornecedor com CNPJ duplicado
    Dado que existe um fornecedor cadastrado com CNPJ "12345678000190"
    Quando eu tento criar um fornecedor com nome "Fornecedor XYZ", CNPJ "12345678000190", email "contato@xyz.com", lead time 5 dias e custo 15.75
    Então deve ocorrer um erro informando que já existe um fornecedor com este CNPJ

  Cenário: Tentar criar fornecedor com lead time inválido
    Quando eu tento criar um fornecedor com nome "Fornecedor XYZ", CNPJ "98765432000111", email "contato@xyz.com", lead time 0 dias e custo 15.75
    Então deve ocorrer um erro informando que o lead time é inválido

  Cenário: Tentar criar fornecedor com custo inválido
    Quando eu tento criar um fornecedor com nome "Fornecedor XYZ", CNPJ "98765432000111", email "contato@xyz.com", lead time 5 dias e custo 0
    Então deve ocorrer um erro informando que o custo é inválido

  Cenário: Ativar fornecedor
    Dado que existe um fornecedor cadastrado com status INATIVO
    Quando eu ativo o fornecedor
    Então o fornecedor deve ter status ATIVO

  Cenário: Inativar fornecedor sem pedidos pendentes
    Dado que existe um fornecedor cadastrado com status ATIVO
    E que o fornecedor não possui pedidos pendentes
    Quando eu inativo o fornecedor
    Então o fornecedor deve ter status INATIVO

  Cenário: Tentar inativar fornecedor com pedidos pendentes
    Dado que existe um fornecedor cadastrado com status ATIVO
    E que o fornecedor possui pedidos pendentes
    Quando eu tento inativar o fornecedor
    Então deve ocorrer um erro informando que não é possível inativar o fornecedor pois existem pedidos pendentes

  Cenário: Atualizar lead time do fornecedor
    Dado que existe um fornecedor cadastrado com lead time 7 dias
    Quando eu atualizo o lead time do fornecedor para 10 dias
    Então o fornecedor deve ter lead time de 10 dias

  Cenário: Atualizar custo do fornecedor
    Dado que existe um fornecedor cadastrado com custo 10.50
    Quando eu atualizo o custo do fornecedor para 12.00
    Então o fornecedor deve ter custo 12.00

