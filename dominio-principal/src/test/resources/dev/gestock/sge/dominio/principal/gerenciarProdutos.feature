# language: pt
Funcionalidade: Gerenciar Produtos
  Como cliente
  Desejo gerenciar produtos
  Para manter controle dos itens em estoque

  # H8: Cadastrar produtos
  Cenário: Cadastrar produto com sucesso
    Quando eu cadastro um produto com código "PROD-001", nome "Produto A", unidade "UN" e não perecível
    Então o produto deve ser cadastrado com sucesso
    E o produto deve estar ativo
    E o ROP deve estar nulo inicialmente

  Cenário: Cadastrar produto perecível
    Quando eu cadastro um produto com código "PROD-002", nome "Produto B", unidade "KG" e perecível
    Então o produto deve ser cadastrado com sucesso
    E o produto deve ser marcado como perecível

  # R1H8: Código único
  Cenário: Tentar cadastrar produto com código duplicado
    Dado que existe um produto com código "PROD-001"
    Quando eu tento cadastrar outro produto com código "PROD-001"
    Então o sistema deve rejeitar o cadastro
    E deve exibir a mensagem "Código do produto já existe"

  # R2H8: Produto fornecido por múltiplos fornecedores
  Cenário: Vincular produto a múltiplos fornecedores
    Dado que existe um produto chamado "Produto A" com id "prod-001"
    E existem os seguintes fornecedores:
      | nome         | cnpj              |
      | Fornecedor A | 11.111.111/0001-11|
      | Fornecedor B | 22.222.222/0001-22|
    Quando os fornecedores registram cotações para o produto:
      | fornecedor   | preco | prazo |
      | Fornecedor A | 100.00| 10    |
      | Fornecedor B | 95.00 | 15    |
    Então o produto deve ter cotações de 2 fornecedores

  # R3H8: Produto vinculado a pelo menos um estoque
  Cenário: Cadastrar produto vinculado a estoque
    Dado que existe um estoque ativo chamado "Estoque Central"
    Quando eu cadastro um produto chamado "Produto C" vinculado ao estoque "Estoque Central"
    Então o produto deve ser cadastrado com sucesso
    E o produto deve estar vinculado ao estoque "Estoque Central"

  # H9: Editar produtos
  Cenário: Atualizar nome e unidade de medida do produto
    Dado que existe um produto chamado "Produto A" com unidade "UN"
    Quando eu atualizo o nome para "Produto A Atualizado" e unidade para "CX"
    Então os dados do produto devem ser atualizados
    E o nome deve ser "Produto A Atualizado"
    E a unidade deve ser "CX"

  # R1H9: Alterações não afetam cotações existentes
  Cenário: Atualizar produto que possui cotações
    Dado que existe um produto chamado "Produto A" com cotações registradas
    Quando eu atualizo as especificações do produto
    Então as cotações existentes devem permanecer inalteradas
    E o produto deve estar atualizado

  # H10: Inativar produtos
  Cenário: Inativar produto sem saldo e sem pedidos
    Dado que existe um produto chamado "Produto A" sem saldo em estoque
    E não existem pedidos em andamento para o produto
    Quando eu inativo o produto "Produto A"
    Então o produto deve ser inativado com sucesso
    E o status do produto deve ser "inativo"

  # R1H10: Não inativar produto com saldo positivo
  Cenário: Tentar inativar produto com saldo positivo
    Dado que existe um produto chamado "Produto A" com saldo de 50 unidades
    Quando eu tento inativar o produto "Produto A"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Produto com saldo positivo não pode ser inativado"

  # R1H10: Não inativar produto com pedidos em andamento
  Cenário: Tentar inativar produto com pedidos em andamento
    Dado que existe um produto chamado "Produto A" sem saldo em estoque
    E existem pedidos em andamento para o produto
    Quando eu tento inativar o produto "Produto A"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Produto com pedidos em andamento não pode ser inativado"

  # R2H10: Bloquear novas cotações após inativação
  Cenário: Verificar bloqueio de novas cotações após inativação
    Dado que existe um produto chamado "Produto A" inativo
    Quando eu tento registrar uma nova cotação para o produto
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Produto inativo não pode receber novas cotações"

  # H14: Definir e calcular ROP
  Cenário: Definir ROP para o produto
    Dado que existe um produto chamado "Produto A"
    Quando eu defino o ROP com consumo médio 10, lead time 7 dias e estoque de segurança 20
    Então o ROP deve ser calculado corretamente
    E o valor do ROP deve ser 90 unidades

  Cenário: Verificar se produto atingiu o ROP
    Dado que existe um produto chamado "Produto A" com ROP de 90 unidades
    Quando o saldo atual é 85 unidades
    Então o produto deve ter atingido o ROP
    E deve ser necessário acionar reposição

  Cenário: Verificar produto acima do ROP
    Dado que existe um produto chamado "Produto A" com ROP de 90 unidades
    Quando o saldo atual é 100 unidades
    Então o produto não deve ter atingido o ROP
