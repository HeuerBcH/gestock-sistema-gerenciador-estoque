# language: pt
Funcionalidade: Gerenciar Fornecedores


  # H5: Como cliente, desejo cadastrar fornecedores com seu Lead Time e contato e suas respectivas cotacoes
  Cenario: Cadastrar fornecedor com sucesso
    Dado que nao existe um fornecedor cadastrado com o CNPJ "12.345.678/0001-90"
    Quando o cliente cadastra um fornecedor com nome "Fornecedor A", CNPJ "12.345.678/0001-90" e contato "contato@fornecedora.com"
    Entao o fornecedor deve ser cadastrado com sucesso
    E o fornecedor deve estar ativo na listagem
    E o lead time inicial deve ser 0 dias

  Cenario: Cadastrar fornecedor com lead time especifico
    Dado que nao existe um fornecedor cadastrado com o CNPJ "98.765.432/0001-10"
    Quando o cliente cadastra um fornecedor com nome "Fornecedor B", CNPJ "98.765.432/0001-10", contato "contato@b.com" e lead time de "7" dias
    Entao o fornecedor deve ser cadastrado com sucesso
    E o lead time deve ser "7" dias

  # R1H5: Cada fornecedor deve possuir uma cotacao vinculada a um produto
  Cenario: Registrar cotacao para um produto
    Dado que existe um fornecedor "Fornecedor A"
    E existe um produto "Produto X" com id "01"
    Quando o cliente registra uma cotacao de "150.00" reais com prazo de "10" dias para o produto "PROD-001"
    Entao a cotacao deve ser registrada com sucesso
    E o fornecedor deve ter cotacao para o produto "PROD-001"

  Cenario: Registrar multiplas cotacoes para produtos diferentes
    Dado que existe um fornecedor "Fornecedor A"
    E existem os seguintes produtos:
      | id | nome       |
      | 01 | Produto X  |
      | 02 | Produto Y  |
    Quando o cliente registra as seguintes cotacoes:
      | id | preco | prazo |
      | 01 | 150.00| 10    |
      | 02 | 200.00| 15    |
    Entao o fornecedor deve ter 2 cotacoes cadastradas

  # R2H5: O Lead Time informado deve ser um numero positivo (dias)
  Cenario: Tentar registrar cotacao com prazo invalido
    Dado que existe um fornecedor chamado "Fornecedor A"
    E existe um produto chamado "Produto X" com id "01"
    Quando o cliente tenta registrar uma cotacao para o produto "Produto X" com prazo -5 dias
    Entao o sistema deve rejeitar a operacao de fornecedor
    E deve exibir a mensagem de fornecedor "Prazo deve ser positivo"

  Cenario: Tentar registrar cotacao com preco invalido
    Dado que existe um fornecedor chamado "Fornecedor A"
    E existe um produto chamado "Produto X" com id "01"
    Quando o cliente tenta registrar uma cotacao para o produto "Produto X" com preco 0 reais
    Entao o sistema deve rejeitar a operacao de fornecedor
    E deve exibir a mensagem de fornecedor "Preco deve ser positivo"

  # H6: Como cliente, desejo atualizar informacoes de fornecedores
  Cenario: Atualizar dados cadastrais do fornecedor
    Dado que existe um fornecedor chamado "Fornecedor A" com contato "antigo@email.com"
    Quando o cliente atualiza os dados do fornecedor para nome "Fornecedor A Atualizado" e contato "novo@email.com"
    Entao os dados do fornecedor devem ser atualizados
    E o nome do fornecedor deve ser "Fornecedor A Atualizado"
    E o contato do fornecedor deve ser "novo@email.com"

  # R1H6: Alterar o Lead Time de um fornecedor recalcula o ponto de ressuprimento dos produtos associados
  Cenario: Recalibrar lead time com base no historico de entregas
    Dado que existe um fornecedor chamado "Fornecedor A" com lead time de 10 dias
    E o fornecedor possui historico de entregas de 8, 9, 7, 10 e 8 dias
    Quando o lead timedo forncedor e recalibrado com base no historico de entregas
    Entao o lead time do fornecedor deve ser atualizado para 8 dias

  Cenario: Tentar recalibrar lead time sem historico de entregas
    Dado que existe um fornecedor chamado "Fornecedor A" sem historico de entregas
    Quando o cliente tenta recalibrar o lead time
    Entao o sistema deve rejeitar a operacao de fornecedor
    E deve exibir a mensagem de fornecedor "Historico insuficiente para recalibrar lead time"

  # H7: Como cliente, desejo inativar fornecedores que nao pretendo mais utilizar
  Cenario: Inativar fornecedor sem pedidos pendentes
    Dado que existe um fornecedor chamado "Fornecedor A" sem pedidos pendentes
    Quando o cliente inativa o fornecedor "Fornecedor A"
    Entao o fornecedor deve ser inativado com sucesso
    E o status do fornecedor deve ser "inativo"

  # R1H7: Um fornecedor nao pode ser inativado se houver pedidos pendentes com ele
  Cenario: Tentar inativar fornecedor com pedidos pendentes
    Dado que existe um fornecedor chamado "Fornecedor A" com pedidos pendentes
    Quando o cliente tenta inativar o fornecedor "Fornecedor A"
    Entao o sistema deve rejeitar a operacao de fornecedor
    E deve exibir a mensagem de fornecedor "Fornecedor com pedidos pendentes nao pode ser inativado"

  Cenario: Reativar fornecedor inativo
    Dado que existe um fornecedor chamado "Fornecedor A" inativo
    Quando o cliente reativa o fornecedor "Fornecedor A"
    Entao o fornecedor deve ser reativado com sucesso
    E o status do fornecedor deve ser "ativo"

  Cenario: Remover cotacao de um produto
    Dado que existe um fornecedor chamado "Fornecedor A"
    E o fornecedor possui cotacao para o produto "PROD-001"
    Quando o cliente remove a cotacao do produto "PROD-001"
    Entao a cotacao deve ser removida
    E o fornecedor nao deve possuir cotacao para o produto "PROD-001"
