# language: pt
Funcionalidade: Gerenciar Estoques


  # H1: Cadastrar estoques
  Cenário: Cadastrar estoque com sucesso
    Dado que existe um cliente com id "01"
    Quando o clinte cadastra um estoque com nome "Estoque Central", endereço "Rua A, 123" e capacidade 1000
    Então o estoque deve ser cadastrado com sucesso
    E o estoque deve estar ativo
    E o estoque deve estar visível na listagem de estoques
    E o estoque deve pertencer ao cliente com id "01"

  Cenário: Cadastrar múltiplos estoques para o mesmo cliente
    Dado que existe um cliente com id "01"
    E já existe um estoque chamado "Estoque A" no endereço "Rua A, 100"
    Quando o cliente cadastra um estoque com nome "Estoque B", endereço "Rua B, 200" e capacidade 500
    Então o estoque deve ser cadastrado com sucesso
    E devem existir 2 estoques cadastrados para o cliente

  # R2H1: Não pode haver estoque no mesmo endereço
  Cenário: Tentar cadastrar estoque em endereço já existente
    Dado que existe um cliente com id "01"
    E já existe um estoque no endereço "Rua A, 123"
    Quando o cliente tenta cadastrar um estoque com endereço "Rua A, 123"
    Então o sistema deve rejeitar o cadastro
    E deve exibir a mensagem "Já existe um estoque cadastrado neste endereço"

  # R3H1: Não pode haver estoque com nome duplicado
  Cenário: Tentar cadastrar estoque com nome duplicado
    Dado que existe um cliente com id "01"
    E já existe um estoque chamado "Estoque Central"
    Quando o cliente tenta cadastrar um estoque com nome "Estoque Central"
    Então o sistema deve rejeitar o cadastro
    E deve exibir a mensagem "Já existe um estoque com este nome"

  # H2: Inativar estoques
  Cenário: Inativar estoque vazio com sucesso
    Dado que existe um estoque chamado "Estoque A" sem produtos
    Quando o cliente inativa o estoque "Estoque A"
    Então o estoque deve ser inativado com sucesso
    E o status do estoque deve ser "inativo"

  # R1H2: Estoque com produtos não pode ser removido
  Cenário: Tentar inativar estoque com produtos
    Dado que existe um estoque chamado "Estoque A" com produtos
    E o produto "Produto X" tem saldo físico de 10 unidades
    Quando o cliente tenta inativar o estoque "Estoque A"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Estoque com produtos não pode ser inativado"

  # R2H2: Estoque com pedido pendente não pode ser removido
  Cenário: Tentar inativar estoque com pedido pendente
    Dado que existe um estoque chamado "Estoque A" sem produtos
    E existe um pedido pendente alocado ao estoque
    Quando o cliente tento inativar o estoque "Estoque A"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Estoque com pedido em andamento não pode ser inativado"

  # H3: Editar parâmetros de estoques
  Cenário: Alterar nome do estoque
    Dado que existe um estoque chamado "Estoque A"
    Quando o cliente altera o nome para "Estoque Principal"
    Então o nome do estoque deve ser atualizado

  Cenário: Aumentar capacidade do estoque
    Dado que existe um estoque com capacidade 1000
    E a ocupação atual do estoque é de 600 unidades
    Quando o cliente altera a capacidade para 1500
    Então a capacidade deve ser atualizada com sucesso

  # R1H3: Não reduzir capacidade abaixo da ocupação
  Cenário: Tentar diminuir capacidade abaixo da ocupação atual
    Dado que existe um estoque com capacidade 1000
    E a ocupação atual do estoque é de 800 unidades
    Quando o cliente tenta alterar a capacidade para 700
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Capacidade não pode ser reduzida abaixo da ocupação atual"

  # H4: Pesquisar e visualizar estoques
  Cenário: Pesquisar estoques por nome
    Dado que existem os seguintes estoques:
      | nome            | endereço       | capacidade |
      | Estoque Central | Rua A, 123     | 1000       |
      | Estoque Filial  | Rua B, 456     | 500        |
      | Depósito Norte  | Av Norte, 789  | 2000       |
    Quando o cliente pesquisa por estoques com nome contendo "Estoque"
    Então o sistema deve exibir 2 estoques
    E os resultados devem incluir "Estoque Central" e "Estoque Filial"

  # R1H4: Pesquisar estoques sem estoque cadastrado
  Cenário: Tentar pesquisar sem estoques cadastrados
    Dado que não existem estoques cadastrados
    Quando o cliente tenta pesquisar estoques
    Então o sistema deve informar "Nenhum estoque cadastrado"

  # R2H4: Pesquisar estoques por endereço
  Cenário: Pesquisar estoques por endereço
    Dado que existem os seguintes estoques:
      | nome            | endereço       | capacidade |
      | Estoque Central | Rua A, 123     | 1000       |
      | Estoque Filial  | Rua A, 456     | 500        |
    Quando o cliente pesquisa por estoques com endereço contendo "Rua A"
    Então o sistema deve exibir 2 estoques
    E os resultados devem incluir "Estoque Central" e "Estoque Filial"

  Cenário: Visualizar detalhes de um estoque
    Dado que existe um estoque chamado "Estoque Central" no endereço "Rua A, 123" com capacidade 1000
    Quando o cliente visualiza os detalhes do estoque
    Então o sistema deve exibir o nome "Estoque Central"
    E o sistema deve exibir o endereço "Rua A, 123"
    E o sistema deve exibir a capacidade 1000
    E o sistema deve exibir o status "ativo"