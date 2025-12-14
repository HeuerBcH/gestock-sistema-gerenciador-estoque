# language: pt
Funcionalidade: Gerenciamento de Cotação
  Como um usuário do sistema
  Eu quero gerenciar cotações de produtos
  Para que possam ser utilizadas em pedidos

  Contexto:
    Dado que existe um produto cadastrado para cotação
    E que existe um fornecedor cadastrado

  Cenário: Criar cotação com dados válidos
    Quando eu crio uma cotação com produto, fornecedor, preço 10.50, lead time 7 dias, validade ATIVA e status PENDENTE
    Então a cotação deve ser criada com sucesso
    E a cotação deve ter preço 10.50
    E a cotação deve ter lead time de 7 dias
    E a cotação deve ter status de aprovação PENDENTE

  Cenário: Tentar criar cotação com produto inexistente
    Dado que não existe o produto especificado para cotação
    Quando eu tento criar uma cotação com produto inexistente, fornecedor, preço 10.50, lead time 7 dias, validade ATIVA e status PENDENTE
    Então deve ocorrer um erro informando que o produto da cotação não foi encontrado

  Cenário: Tentar criar cotação com fornecedor inexistente
    Dado que não existe o fornecedor especificado
    Quando eu tento criar uma cotação com produto, fornecedor inexistente, preço 10.50, lead time 7 dias, validade ATIVA e status PENDENTE
    Então deve ocorrer um erro informando que o fornecedor não foi encontrado

  Cenário: Aprovar cotação
    Dado que existe uma cotação cadastrada com status PENDENTE
    Quando eu aprovo a cotação
    Então a cotação deve ter status de aprovação APROVADA

  Cenário: Desaprovar cotação
    Dado que existe uma cotação cadastrada com status APROVADA
    Quando eu desaprovo a cotação
    Então a cotação deve ter status de aprovação PENDENTE

  Cenário: Obter cotação mais vantajosa por menor preço
    Dado que existem cotações para o produto:
      | Fornecedor | Preço | Lead Time | Validade |
      | 1          | 10.50 | 7         | ATIVA    |
      | 2          | 12.00 | 5         | ATIVA    |
      | 3          | 11.00 | 6         | ATIVA    |
    Quando eu busco a cotação mais vantajosa para o produto
    Então a cotação mais vantajosa deve ter preço 10.50

  Cenário: Obter cotação mais vantajosa considerando lead time quando preços são iguais
    Dado que existem cotações para o produto:
      | Fornecedor | Preço | Lead Time | Validade |
      | 1          | 10.50 | 7         | ATIVA    |
      | 2          | 10.50 | 5         | ATIVA    |
    Quando eu busco a cotação mais vantajosa para o produto
    Então a cotação mais vantajosa deve ter lead time de 5 dias

  Cenário: Obter cotação mais vantajosa priorizando validade ATIVA
    Dado que existem cotações para o produto:
      | Fornecedor | Preço | Lead Time | Validade |
      | 1          | 10.50 | 7         | EXPIRADA |
      | 2          | 10.50 | 7         | ATIVA    |
    Quando eu busco a cotação mais vantajosa para o produto
    Então a cotação mais vantajosa deve ter validade ATIVA

  Cenário: Atualizar preço da cotação
    Dado que existe uma cotação cadastrada com preço 10.50
    Quando eu atualizo o preço da cotação para 12.00
    Então a cotação deve ter preço 12.00

  Cenário: Atualizar lead time da cotação
    Dado que existe uma cotação cadastrada com lead time 7 dias
    Quando eu atualizo o lead time da cotação para 10 dias
    Então a cotação deve ter lead time de 10 dias

