# language: pt
Funcionalidade: Selecionar Cotacao Mais Vantajosa


  # H18: Selecionar automaticamente menor preco
  # R1H18: Apenas cotacoes validas e fornecedores ativos
  Cenario: Selecionar cotacao com menor preco
    Dado que existem as seguintes cotacoes para o produto "Produto X":
      | fornecedor   | preco | prazo | ativo |
      | Fornecedor A | 100.00| 10    | true  |
      | Fornecedor B | 95.00 | 15    | true  |
      | Fornecedor C | 90.00 | 20    | true  |
    Quando o sistema seleciona a melhor cotacao
    Entao a cotacao do "Fornecedor C" deve ser selecionada

  # R2H18: Em empate, priorizar menor Lead Time
  Cenario: Desempate por menor lead time
    Dado que existem as seguintes cotacoes para o produto "Produto X":
      | fornecedor   | preco | prazo |
      | Fornecedor A | 100.00| 10    |
      | Fornecedor B | 100.00| 7     |
    Quando o sistema seleciona a melhor cotacao
    Entao a cotacao do "Fornecedor B" deve ser selecionada

  Cenario: Ignorar fornecedor inativo
    Dado que existem as seguintes cotacoes para o produto "Produto X":
      | fornecedor   | preco | prazo | ativo |
      | Fornecedor A | 100.00| 10    | true  |
      | Fornecedor B | 80.00 | 15    | false |
    Quando o sistema seleciona a melhor cotacao
    Entao a cotacao do "Fornecedor A" deve ser selecionada

  # H19: Revisar e aprovar cotacao
  # R1H19: Cotacao aprovada e registrada
  Cenario: Aprovar cotacao selecionada
    Dado que o sistema selecionou a melhor cotacao para o produto "Produto X"
    Quando o cliente aprova a cotacao
    Entao a cotacao deve ser marcada como "selecionada"
    E um pedido deve ser gerado utilizando essa cotacao