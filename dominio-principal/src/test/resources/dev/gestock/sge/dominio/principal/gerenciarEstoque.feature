# language: pt
Funcionalidade: Gerenciar Estoques


  # H1: Cadastrar estoques
  Cenario: Cadastrar estoque com sucesso
    Dado que existe um cliente com id "01"
    Quando o cliente cadastra um estoque com nome "Estoque Central", endereco "Rua A, 123" e capacidade 1000
    Entao o estoque deve ser cadastrado com sucesso
    E o estoque deve estar ativo
    E o estoque deve estar visivel na listagem de estoques
    E o estoque deve pertencer ao cliente com id "01"

  Cenario: Cadastrar multiplos estoques para o mesmo cliente
    Dado que existe um cliente com id "01"
    E ja existe um estoque chamado "Estoque A" no endereco "Rua A, 100"
    Quando o cliente cadastra um estoque com nome "Estoque B", endereco "Rua B, 200" e capacidade 500
    Entao o estoque deve ser cadastrado com sucesso
    E devem existir 2 estoques cadastrados para o cliente

  # R2H1: Nao pode haver estoque no mesmo endereco
  Cenario: Tentar cadastrar estoque em endereco ja existente
    Dado que existe um cliente com id "01"
    E ja existe um estoque no endereco "Rua A, 123"
    Quando o cliente tenta cadastrar um estoque com endereco "Rua A, 123"
    Entao o sistema deve rejeitar o cadastro
    E deve exibir a mensagem "Ja existe um estoque cadastrado neste endereco"

  # R3H1: Nao pode haver estoque com nome duplicado
  Cenario: Tentar cadastrar estoque com nome duplicado
    Dado que existe um cliente com id "01"
    E ja existe um estoque chamado "Estoque Central"
    Quando o cliente tenta cadastrar um estoque com nome "Estoque Central"
    Entao o sistema deve rejeitar o cadastro
    E deve exibir a mensagem "Ja existe um estoque com este nome"

  # H2: Inativar estoques
  Cenario: Inativar estoque vazio com sucesso
    Dado que existe um estoque chamado "Estoque A" sem produtos
    Quando o cliente inativa o estoque "Estoque A"
    Entao o estoque deve ser inativado com sucesso
    E o status do estoque deve ser "inativo"

  # R1H2: Estoque com produtos nao pode ser removido
  Cenario: Tentar inativar estoque com produtos
    Dado que existe um estoque chamado "Estoque A" com produtos
    E o produto "Produto X" tem saldo fisico de 10 unidades
    Quando o cliente tenta inativar o estoque "Estoque A"
    Entao o sistema deve rejeitar a operacao
    E deve exibir a mensagem "Estoque com produtos nao pode ser inativado"

  # R2H2: Estoque com pedido pendente nao pode ser removido
  Cenario: Tentar inativar estoque com pedido pendente
    Dado que existe um estoque chamado "Estoque A" sem produtos
    E existe um pedido pendente alocado ao estoque
    Quando o cliente tenta inativar o estoque "Estoque A"
    Entao o sistema deve rejeitar a operacao
    E deve exibir a mensagem "Estoque com pedido em andamento nao pode ser inativado"

  # H3: Editar parametros de estoques
  Cenario: Alterar nome do estoque
    Dado que existe um estoque chamado "Estoque A"
    Quando o cliente altera o nome para "Estoque Principal"
    Entao o nome do estoque deve ser atualizado

  Cenario: Aumentar capacidade do estoque
    Dado que existe um estoque com capacidade 1000
    E a ocupacao atual do estoque e de 600 unidades
    Quando o cliente altera a capacidade para 1500
    Entao a capacidade deve ser atualizada com sucesso

  # R1H3: Nao reduzir capacidade abaixo da ocupacao
  Cenario: Tentar diminuir capacidade abaixo da ocupacao atual
    Dado que existe um estoque com capacidade 1000
    E a ocupacao atual do estoque e de 800 unidades
    Quando o cliente tenta alterar a capacidade para 700
    Entao o sistema deve rejeitar a operacao
    E deve exibir a mensagem "Capacidade nao pode ser reduzida abaixo da ocupacao atual"

  # H4: Pesquisar e visualizar estoques
  Cenario: Pesquisar estoques por nome
    Dado que existem os seguintes estoques:
      | nome            | endereco       | capacidade |
      | Estoque Central | Rua A, 123     | 1000       |
      | Estoque Filial  | Rua B, 456     | 500        |
      | Deposito Norte  | Av Norte, 789  | 2000       |
    Quando o cliente pesquisa por estoques com nome contendo "Estoque"
    Entao o sistema deve exibir 2 estoques
    E os resultados devem incluir "Estoque Central" e "Estoque Filial"

  # R1H4: Pesquisar estoques sem estoque cadastrado
  Cenario: Tentar pesquisar sem estoques cadastrados
    Dado que nao existem estoques cadastrados
    Quando o cliente tenta pesquisar estoques
    Entao o sistema deve informar "Nenhum estoque cadastrado"

  # R2H4: Pesquisar estoques por endereco
  Cenario: Pesquisar estoques por endereco
    Dado que existem os seguintes estoques:
      | nome            | endereco       | capacidade |
      | Estoque Central | Rua A, 123     | 1000       |
      | Estoque Filial  | Rua A, 456     | 500        |
    Quando o cliente pesquisa por estoques com endereco contendo "Rua A"
    Entao o sistema deve exibir 2 estoques
    E os resultados devem incluir "Estoque Central" e "Estoque Filial"

  Cenario: Visualizar detalhes de um estoque
    Dado que existe um estoque chamado "Estoque Central" no endereco "Rua A, 123" com capacidade 1000
    Quando o cliente visualiza os detalhes do estoque
    Entao o sistema deve exibir o nome "Estoque Central"
    E o sistema deve exibir o endereco "Rua A, 123"
    E o sistema deve exibir a capacidade 1000
    E o sistema deve exibir o status "ativo"
