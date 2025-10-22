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
    Entao o sistema deve rejeitar o cadastro de estoque
    E deve exibir a mensagem de estoque "Ja existe um estoque cadastrado neste endereco"

  # R3H1: Nao pode haver estoque com nome duplicado
  Cenario: Tentar cadastrar estoque com nome duplicado
    Dado que existe um cliente com id "01"
    E ja existe um estoque chamado "Estoque Central"
    Quando o cliente tenta cadastrar um estoque com nome "Estoque Central"
    Entao o sistema deve rejeitar o cadastro de estoque
    E deve exibir a mensagem de estoque "Ja existe um estoque com este nome"

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
    Entao o sistema deve impedir a operacao
    E deve exibir a mensagem de estoque "Estoque com produtos nao pode ser inativado"

  # R2H2: Estoque com pedido pendente nao pode ser removido
  Cenario: Tentar inativar estoque com pedido pendente
    Dado que existe um estoque chamado "Estoque A" sem produtos
    E existe um pedido pendente alocado ao estoque
    Quando o cliente tenta inativar o estoque "Estoque A"
    Entao o sistema deve impedir a operacao
    E deve exibir a mensagem de estoque "Estoque com pedido em andamento nao pode ser inativado"

  # H3: Editar parametros de estoque
  # R1H3: Nao pode reduzir capacidade se estiver cheia
  Cenario: Tentar reduzir capacidade de estoque cheio
    Dado que existe um estoque chamado "Estoque B" com capacidade 1000
    E o estoque esta com 1000 unidades armazenadas
    Quando o cliente tenta alterar a capacidade do estoque para 800
    Entao o sistema deve impedir a alteracao
    E deve exibir a mensagem de estoque "Nao e possivel reduzir capacidade de estoque cheio"

  # H4: Pesquisar e visualizar estoques
  # R1H4: Nao deve ser possivel pesquisar sem estoques cadastrados
  Cenario: Tentar pesquisar estoques sem cadastros
    Dado que nao existem estoques cadastrados
    Quando o cliente realiza uma pesquisa de estoques
    Entao o sistema deve exibir a mensagem "Nenhum estoque cadastrado"

  # R2H4: Pesquisa por multiplos parametros
  Cenario: Pesquisar estoque por nome e endereco
    Dado que existem estoques cadastrados
    Quando o cliente pesquisa pelo nome "Estoque Central" e endereco "Rua A, 123"
    Entao o sistema deve exibir o estoque correspondente
