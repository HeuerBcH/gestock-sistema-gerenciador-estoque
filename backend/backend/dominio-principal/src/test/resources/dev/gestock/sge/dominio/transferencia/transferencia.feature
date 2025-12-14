# language: pt
Funcionalidade: Gerenciamento de Transferência
  Como um usuário do sistema
  Eu quero gerenciar transferências entre estoques
  Para redistribuir produtos

  Contexto:
    Dado que existem duas movimentações correspondentes

  Cenário: Criar transferência com movimentações válidas
    Quando eu crio uma transferência a partir das movimentações
    Então a transferência deve ser criada com sucesso
    E a transferência deve ter estoque de origem e destino diferentes

  Cenário: Tentar criar transferência com estoques iguais
    Dado que as movimentações têm o mesmo estoque
    Quando eu tento criar uma transferência a partir das movimentações
    Então deve ocorrer um erro informando que os estoques devem ser diferentes

  Cenário: Tentar criar transferência com produtos diferentes
    Dado que as movimentações têm produtos diferentes
    Quando eu tento criar uma transferência a partir das movimentações
    Então deve ocorrer um erro informando que as movimentações devem ser do mesmo produto

