# language: pt
Funcionalidade: Gerenciar Fornecedores
  Como cliente
  Desejo gerenciar fornecedores
  Para manter cadastro atualizado de quem fornece meus produtos

  # H5: Como cliente, desejo cadastrar fornecedores com seu Lead Time e contato e suas respectivas cotações
  Cenário: Cadastrar fornecedor com sucesso
    Quando eu cadastro um fornecedor com nome "Fornecedor A", CNPJ "12.345.678/0001-90" e contato "contato@fornecedora.com"
    Então o fornecedor deve ser cadastrado com sucesso
    E o fornecedor deve estar ativo
    E o lead time inicial deve ser 0 dias

  Cenário: Cadastrar fornecedor com lead time específico
    Quando eu cadastro um fornecedor com nome "Fornecedor B", CNPJ "98.765.432/0001-10", contato "contato@b.com" e lead time de "7" dias
    Então o fornecedor deve ser cadastrado com sucesso
    E o lead time deve ser "7" dias

  # R1H5: Cada fornecedor deve possuir uma cotação vinculada a um produto
  Cenário: Registrar cotação para um produto
    Dado que existe um fornecedor "Fornecedor A"
    E existe um produto "Produto X" com id "prod-001"
    Quando eu registro uma cotação de "150.00" reais com prazo de "10" dias para o produto "prod-001"
    Então a cotação deve ser registrada com sucesso
    E o fornecedor deve ter cotação para o produto "prod-001"

  Cenário: Registrar múltiplas cotações para produtos diferentes
    Dado que existe um fornecedor "Fornecedor A"
    E existem os seguintes produtos:
      | id       | nome       |
      | prod-001 | Produto X  |
      | prod-002 | Produto Y  |
    Quando eu registro as seguintes cotações:
      | produtoId | preco | prazo |
      | prod-001  | 150.00| 10    |
      | prod-002  | 200.00| 15    |
    Então o fornecedor deve ter 2 cotações cadastradas

  # R2H5: O Lead Time informado deve ser um número positivo (dias)
  Cenário: Tentar registrar cotação com prazo inválido
    Dado que existe um fornecedor "Fornecedor A"
    E existe um produto "Produto X" com id "prod-001"
    Quando eu tento registrar uma cotação com prazo "-5" dias
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Prazo deve ser positivo"

  Cenário: Tentar registrar cotação com preço inválido
    Dado que existe um fornecedor "Fornecedor A"
    E existe um produto "Produto X" com id "prod-001"
    Quando eu tento registrar uma cotação com preço "0" reais
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Preço deve ser positivo"

  # H6: Como cliente, desejo atualizar informações de fornecedores
  Cenário: Atualizar dados cadastrais do fornecedor
    Dado que existe um fornecedor "Fornecedor A" com contato "antigo@email.com"
    Quando eu atualizo o nome para "Fornecedor A Atualizado" e contato para "novo@email.com"
    Então os dados do fornecedor devem ser atualizados
    E o nome deve ser "Fornecedor A Atualizado"
    E o contato deve ser "novo@email.com"

  # R1H6: Alterar o Lead Time de um fornecedor recalcula o ponto de ressuprimento dos produtos associados
  Cenário: Recalibrar lead time com base no histórico de entregas
    Dado que existe um fornecedor "Fornecedor A" com lead time de "10" dias
    E o fornecedor possui histórico de entregas: "8, 9, 7, 10, 8" dias
    Quando eu recalibro o lead time com base no histórico
    Então o lead time deve ser atualizado para "8" dias

  Cenário: Tentar recalibrar lead time sem histórico
    Dado que existe um fornecedor "Fornecedor A"
    Quando eu tento recalibrar o lead time sem histórico
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Histórico insuficiente para recalibrar lead time"

  # H7: Como cliente, desejo inativar fornecedores que não pretendo mais utilizar
  Cenário: Inativar fornecedor sem pedidos pendentes
    Dado que existe um fornecedor "Fornecedor A" sem pedidos pendentes
    Quando eu inativo o fornecedor "Fornecedor A"
    Então o fornecedor deve ser inativado com sucesso
    E o status do fornecedor deve ser "inativo"

  # R1H7: Um fornecedor não pode ser inativado se houver pedidos pendentes com ele
  Cenário: Tentar inativar fornecedor com pedidos pendentes
    Dado que existe um fornecedor "Fornecedor A" com pedidos pendentes
    Quando eu tento inativar o fornecedor "Fornecedor A"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Fornecedor com pedidos pendentes não pode ser inativado"

  Cenário: Reativar fornecedor inativo
    Dado que existe um fornecedor "Fornecedor A" inativo
    Quando eu reativo o fornecedor "Fornecedor A"
    Então o fornecedor deve ser reativado com sucesso
    E o status do fornecedor deve ser "ativo"

  Cenário: Remover cotação de um produto
    Dado que existe um fornecedor "Fornecedor A"
    E o fornecedor tem cotação para o produto "prod-001"
    Quando eu removo a cotação do produto "prod-001"
    Então a cotação deve ser removida
    E o fornecedor não deve ter cotação para o produto "prod-001"
