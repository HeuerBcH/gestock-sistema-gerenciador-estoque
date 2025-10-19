# language: pt
Funcionalidade: Gerenciar Produtos


  # H8: Cadastrar produtos
  Cenário: Cadastrar produto com sucesso
    Dado que o cliente informa código "01", nome "Produto A", unidade "UN" e indica que não é perecível
    Quando o cliente confirma o cadastro do produto
    Então o sistema deve cadastrar o produto com sucesso
    E o produto deve estar ativo
    E o ROP deve estar nulo inicialmente

  Cenário: Cadastrar produto perecível
    Dado que o cliente informa código "02", nome "Produto B", unidade "KG" e indica que é perecível
    Quando o cliente confirma o cadastro do produto
    Então o sistema deve cadastrar o produto com sucesso
    E o produto deve ser marcado como perecível

  # R1H8: Código único
  Cenário: Tentar cadastrar produto com código duplicado
    Dado que existe um produto cadastrado com código "01"
    Quando o cliente tenta cadastrar outro produto com o mesmo código "01"
    Então o sistema deve rejeitar o cadastro
    E o sistema deve exibir a mensagem "Código do produto já existe"

  # R2H8: Produto fornecido por múltiplos fornecedores
  Cenário: Vincular produto a múltiplos fornecedores
    Dado que existe um produto chamado "Produto A" com id "01"
    E existem os seguintes fornecedores cadastrados:
      | nome         | cnpj               |
      | Fornecedor A | 11.111.111/0001-11 |
      | Fornecedor B | 22.222.222/0001-22 |
    Quando os fornecedores registram cotações para o produto:
      | fornecedor   | preco | prazo |
      | Fornecedor A | 100.00 | 10   |
      | Fornecedor B | 95.00  | 15   |
    Então o produto deve possuir cotações de dois fornecedores

  # R3H8: Produto vinculado a pelo menos um estoque
  Cenário: Cadastrar produto vinculado a estoque
    Dado que existe um estoque ativo chamado "Estoque Central"
    Quando o cliente cadastra um produto chamado "Produto C" vinculado ao estoque "Estoque Central"
    Então o sistema deve cadastrar o produto com sucesso
    E o produto deve estar vinculado ao estoque "Estoque Central"

  # H9: Editar produtos
  Cenário: Atualizar nome e unidade de medida do produto
    Dado que existe um produto chamado "Produto A" com unidade "UN"
    Quando o cliente atualiza o nome para "Produto A Atualizado" e a unidade para "CX"
    Então o sistema deve atualizar os dados do produto
    E o nome deve ser "Produto A Atualizado"
    E a unidade deve ser "CX"

  # R1H9: Alterações não afetam cotações existentes
  Cenário: Atualizar produto que possui cotações registradas
    Dado que existe um produto chamado "Produto A" com cotações registradas
    Quando o cliente atualiza as especificações do produto
    Então o sistema deve manter as cotações existentes inalteradas
    E o produto deve estar atualizado

  # H10: Inativar produtos
  Cenário: Inativar produto sem saldo e sem pedidos
    Dado que existe um produto chamado "Produto A" sem saldo em estoque
    E não existem pedidos em andamento para o produto
    Quando o cliente solicita a inativação do produto "Produto A"
    Então o sistema deve inativar o produto com sucesso
    E o status do produto deve ser "inativo"

  # R1H10: Não inativar produto com saldo positivo
  Cenário: Tentar inativar produto com saldo positivo
    Dado que existe um produto chamado "Produto A" com saldo de 50 unidades
    Quando o cliente solicita a inativação do produto "Produto A"
    Então o sistema deve rejeitar a operação
    E o sistema deve exibir a mensagem "Produto com saldo positivo não pode ser inativado"

  # R1H10: Não inativar produto com pedidos em andamento
  Cenário: Tentar inativar produto com pedidos em andamento
    Dado que existe um produto chamado "Produto A" sem saldo em estoque
    E existem pedidos em andamento para o produto
    Quando o cliente solicita a inativação do produto "Produto A"
    Então o sistema deve rejeitar a operação
    E o sistema deve exibir a mensagem "Produto com pedidos em andamento não pode ser inativado"

  # R2H10: Bloquear novas cotações após inativação
  Cenário: Verificar bloqueio de novas cotações após inativação
    Dado que existe um produto chamado "Produto A" inativo
    Quando o cliente tenta registrar uma nova cotação para o produto
    Então o sistema deve rejeitar a operação
    E o sistema deve exibir a mensagem "Produto inativo não pode receber novas cotações"

  # H14: Definir e calcular ROP
  Cenário: Definir ROP para o produto
    Dado que existe um produto chamado "Produto A"
    Quando o cliente define o ROP informando consumo médio de 10 unidades por dia, lead time de 7 dias e estoque de segurança de 20 unidades
    Então o sistema deve calcular o ROP corretamente
    E o valor do ROP deve ser 90 unidades

  Cenário: Verificar se produto atingiu o ROP
    Dado que existe um produto chamado "Produto A" com ROP definido em 90 unidades
    Quando o saldo atual é 85 unidades
    Então o sistema deve identificar que o produto atingiu o ROP
    E deve ser necessário acionar reposição

  Cenário: Verificar produto acima do ROP
    Dado que existe um produto chamado "Produto A" com ROP definido em 90 unidades
    Quando o saldo atual é 100 unidades
    Então o sistema deve identificar que o produto está acima do ROP
    E não é necessário acionar reposição
