# language: pt
Funcionalidade: Gerenciar Estoques
  Como cliente
  Desejo gerenciar meus estoques
  Para controlar meus produtos de forma organizada

  # H1: Como cliente desejo ser capaz de cadastrar um ou mais estoques
  Cenário: Cadastrar estoque com sucesso
    Dado que existe um cliente com id "cliente-001"
    Quando eu cadastro um estoque com nome "Estoque Central", endereço "Rua A, 123" e capacidade "1000"
    Então o estoque deve ser cadastrado com sucesso
    E o estoque deve estar ativo
    E o estoque deve pertencer ao cliente "cliente-001"

  Cenário: Cadastrar múltiplos estoques para o mesmo cliente
    Dado que existe um cliente com id "cliente-001"
    E já existe um estoque "Estoque A" no endereço "Rua A, 100"
    Quando eu cadastro um estoque com nome "Estoque B", endereço "Rua B, 200" e capacidade "500"
    Então o estoque deve ser cadastrado com sucesso
    E devem existir 2 estoques cadastrados para o cliente

  # R2H1: Não pode haver mais de um estoque cadastrado em um mesmo endereço
  Cenário: Tentar cadastrar estoque em endereço já existente
    Dado que existe um cliente com id "cliente-001"
    E já existe um estoque no endereço "Rua A, 123"
    Quando eu tento cadastrar um estoque com endereço "Rua A, 123"
    Então o sistema deve rejeitar o cadastro
    E deve exibir a mensagem "Já existe um estoque cadastrado neste endereço"

  # R3H1: Dois ou mais estoques não podem ter o mesmo nome
  Cenário: Tentar cadastrar estoque com nome duplicado
    Dado que existe um cliente com id "cliente-001"
    E já existe um estoque com nome "Estoque Central"
    Quando eu tento cadastrar um estoque com nome "Estoque Central"
    Então o sistema deve rejeitar o cadastro
    E deve exibir a mensagem "Já existe um estoque com este nome"

  # H2: Como cliente desejo ser capaz de inativar meus estoques
  Cenário: Inativar estoque vazio com sucesso
    Dado que existe um estoque "Estoque A" sem produtos
    Quando eu inativo o estoque "Estoque A"
    Então o estoque deve ser inativado com sucesso
    E o status do estoque deve ser "inativo"

  # R1H2: Um estoque que ainda possui produtos não pode ser removido
  Cenário: Tentar inativar estoque com produtos
    Dado que existe um estoque "Estoque A" com produtos
    E o produto "Produto X" tem saldo físico de 10 unidades
    Quando eu tento inativar o estoque "Estoque A"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Estoque com produtos não pode ser inativado"

  # R2H2: Um estoque que possui um pedido alocado em andamento não pode ser removido
  Cenário: Tentar inativar estoque com pedido pendente
    Dado que existe um estoque "Estoque A" sem produtos
    E existe um pedido pendente alocado ao estoque
    Quando eu tento inativar o estoque "Estoque A"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Estoque com pedido em andamento não pode ser inativado"

  # H3: Como cliente desejo editar os parâmetros dos meus estoques
  Cenário: Alterar nome do estoque
    Dado que existe um estoque "Estoque A"
    Quando eu altero o nome para "Estoque Principal"
    Então o nome do estoque deve ser atualizado

  Cenário: Aumentar capacidade do estoque
    Dado que existe um estoque com capacidade "1000"
    E a ocupação atual é de "600" unidades
    Quando eu altero a capacidade para "1500"
    Então a capacidade deve ser atualizada com sucesso

  # R1H3: O tamanho de um estoque não pode ser diminuído caso o mesmo esteja com produtos ocupando a capacidade máxima
  Cenário: Tentar diminuir capacidade abaixo da ocupação atual
    Dado que existe um estoque com capacidade "1000"
    E a ocupação atual é de "800" unidades
    Quando eu tento alterar a capacidade para "700"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Capacidade não pode ser reduzida abaixo da ocupação atual"

  # H4: Como cliente desejo pesquisar e visualizar meus estoques
  Cenário: Pesquisar estoques por nome
    Dado que existem os seguintes estoques:
      | nome             | endereço       | capacidade |
      | Estoque Central  | Rua A, 123     | 1000       |
      | Estoque Filial   | Rua B, 456     | 500        |
      | Depósito Norte   | Av Norte, 789  | 2000       |
    Quando eu pesquiso por estoques com nome contendo "Estoque"
    Então devo encontrar 2 estoques
    E os resultados devem incluir "Estoque Central" e "Estoque Filial"

  # R1H4: Não deve ser possível pesquisar estoques se não houver estoques cadastrados
  Cenário: Tentar pesquisar sem estoques cadastrados
    Dado que não existem estoques cadastrados
    Quando eu tento pesquisar estoques
    Então o sistema deve informar "Nenhum estoque cadastrado"

  # R2H4: Deve ser possível pesquisar estoques por mais parâmetros além do nome
  Cenário: Pesquisar estoques por endereço
    Dado que existem os seguintes estoques:
      | nome             | endereço       | capacidade |
      | Estoque Central  | Rua A, 123     | 1000       |
      | Estoque Filial   | Rua A, 456     | 500        |
    Quando eu pesquiso por estoques com endereço contendo "Rua A"
    Então devo encontrar 2 estoques

  Cenário: Visualizar detalhes de um estoque
    Dado que existe um estoque "Estoque Central" no endereço "Rua A, 123" com capacidade "1000"
    Quando eu visualizo os detalhes do estoque
    Então devo ver o nome "Estoque Central"
    E devo ver o endereço "Rua A, 123"
    E devo ver a capacidade "1000"
    E devo ver o status "ativo"
