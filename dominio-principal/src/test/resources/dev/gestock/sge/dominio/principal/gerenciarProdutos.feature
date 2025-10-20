# language: pt
Funcionalidade: Gerenciar Produtos


  # H8: Cadastrar produtos
  Cenario: Cadastrar produto com sucesso
    Dado que o cliente informa codigo "PROD-001", nome "Produto A", unidade "UN" e indica que nao e perecivel
    Quando o cliente confirma o cadastro do produto
    Entao o sistema deve cadastrar o produto com sucesso
    E o produto deve estar ativo
    E o ROP deve estar nulo inicialmente

  Cenario: Cadastrar produto perecivel
    Dado que o cliente informa codigo "PROD-002", nome "Produto B", unidade "KG" e indica que e perecivel
    Quando o cliente confirma o cadastro do produto
    Entao o sistema deve cadastrar o produto com sucesso
    E o produto deve ser marcado como perecivel

  # R1H8: Codigo unico
  Cenario: Tentar cadastrar produto com codigo duplicado
    Dado que existe um produto cadastrado com codigo "PROD-001"
    Quando o cliente tenta cadastrar outro produto com o mesmo codigo "PROD-001"
    Entao o sistema deve rejeitar o cadastro
    E o sistema deve exibir a mensagem "Codigo do produto ja existe"

  # R2H8: Produto fornecido por multiplos fornecedores
  Cenario: Vincular produto a multiplos fornecedores
    Dado que existe um produto chamado "Produto A" com id "01"
    E existem os seguintes fornecedores cadastrados:
      | nome         | cnpj               |
      | Fornecedor A | 11.111.111/0001-11 |
      | Fornecedor B | 22.222.222/0001-22 |
    Quando os fornecedores registram cotacoes para o produto:
      | fornecedor   | preco | prazo |
      | Fornecedor A | 100.00 | 10   |
      | Fornecedor B | 95.00  | 15   |
    Entao o produto deve possuir cotacoes de dois fornecedores

  # R3H8: Produto vinculado a pelo menos um estoque
  Cenario: Cadastrar produto vinculado a estoque
    Dado que existe um estoque ativo chamado "Estoque Central"
    Quando o cliente cadastra um produto chamado "Produto C" vinculado ao estoque "Estoque Central"
    Entao o sistema deve cadastrar o produto com sucesso
    E o produto deve estar vinculado ao estoque "Estoque Central"

  # H9: Editar produtos
  Cenario: Atualizar nome e unidade de medida do produto
    Dado que existe um produto chamado "Produto A" com unidade "UN"
    Quando o cliente atualiza o nome para "Produto A Atualizado" e a unidade para "CX"
    Entao o sistema deve atualizar os dados do produto
    E o nome deve ser "Produto A Atualizado"
    E a unidade deve ser "CX"

  # R1H9: Alteracoes nao afetam cotacoes existentes
  Cenario: Atualizar produto que possui cotacoes registradas
    Dado que existe um produto chamado "Produto A" com cotacoes registradas
    Quando o cliente atualiza as especificacoes do produto
    Entao o sistema deve manter as cotacoes existentes inalteradas
    E o produto deve estar atualizado

  # H10: Inativar produtos
  Cenario: Inativar produto sem saldo e sem pedidos
    Dado que existe um produto chamado "Produto A" sem saldo em estoque
    E nao existem pedidos em andamento para o produto
    Quando o cliente solicita a inativacao do produto "Produto A"
    Entao o sistema deve inativar o produto com sucesso
    E o status do produto deve ser "inativo"

  # R1H10: Nao inativar produto com saldo positivo
  Cenario: Tentar inativar produto com saldo positivo
    Dado que existe um produto chamado "Produto A" com saldo de 50 unidades
    Quando o cliente solicita a inativacao do produto "Produto A"
    Entao o sistema deve rejeitar a operacao
    E o sistema deve exibir a mensagem "Produto com saldo positivo nao pode ser inativado"

  # R1H10: Nao inativar produto com pedidos em andamento
  Cenario: Tentar inativar produto com pedidos em andamento
    Dado que existe um produto chamado "Produto A" sem saldo em estoque
    E existem pedidos em andamento para o produto
    Quando o cliente solicita a inativacao do produto "Produto A"
    Entao o sistema deve rejeitar a operacao
    E o sistema deve exibir a mensagem "Produto com pedidos em andamento nao pode ser inativado"

  # R2H10: Bloquear novas cotacoes apos inativacao
  Cenario: Verificar bloqueio de novas cotacoes apos inativacao
    Dado que existe um produto chamado "Produto A" inativo
    Quando o cliente tenta registrar uma nova cotacao para o produto
    Entao o sistema deve rejeitar a operacao
    E o sistema deve exibir a mensagem "Produto inativo nao pode receber novas cotacoes"

  # H14: Definir e calcular ROP
  Cenario: Definir ROP para o produto
    Dado que existe um produto chamado "Produto A"
    Quando o cliente define o ROP informando consumo medio de 10 unidades por dia, lead time de 7 dias e estoque de seguranca de 20 unidades
    Entao o sistema deve calcular o ROP corretamente
    E o valor do ROP deve ser 90 unidades

  Cenario: Verificar se produto atingiu o ROP
    Dado que existe um produto chamado "Produto A" com ROP definido em 90 unidades
    Quando o saldo atual e 85 unidades
    Entao o sistema deve identificar que o produto atingiu o ROP
    E deve ser necessario acionar reposicao

  Cenario: Verificar produto acima do ROP
    Dado que existe um produto chamado "Produto A" com ROP definido em 90 unidades
    Quando o saldo atual e 100 unidades
    Entao o sistema deve identificar que o produto esta acima do ROP
    E nao e necessario acionar reposicao
