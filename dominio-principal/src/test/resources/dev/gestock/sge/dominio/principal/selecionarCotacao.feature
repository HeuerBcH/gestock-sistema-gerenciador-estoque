# language: pt
Funcionalidade: Selecionar Cotação Mais Vantajosa


  # H18: Selecionar automaticamente menor preço
  # R1H18: Apenas cotações válidas e fornecedores ativos
  Cenário: Selecionar cotação com menor preço
    Dado que existem as seguintes cotações para o produto "Produto X":
      | fornecedor   | preco | prazo | ativo |
      | Fornecedor A | 100.00| 10    | true  |
      | Fornecedor B | 95.00 | 15    | true  |
      | Fornecedor C | 90.00 | 20    | true  |
    Quando o sistema seleciona a melhor cotação
    Então a cotação do "Fornecedor C" deve ser selecionada

  # R2H18: Em empate, priorizar menor Lead Time
  Cenário: Desempate por menor lead time
    Dado que existem as seguintes cotações para o produto "Produto X":
      | fornecedor   | preco | prazo |
      | Fornecedor A | 100.00| 10    |
      | Fornecedor B | 100.00| 7     |
    Quando o sistema seleciona a melhor cotação
    Então a cotação do "Fornecedor B" deve ser selecionada

  Cenário: Ignorar fornecedor inativo
    Dado que existem as seguintes cotações para o produto "Produto X":
      | fornecedor   | preco | prazo | ativo |
      | Fornecedor A | 100.00| 10    | true  |
      | Fornecedor B | 80.00 | 15    | false |
    Quando o sistema seleciona a melhor cotação
    Então a cotação do "Fornecedor A" deve ser selecionada

  # H19: Revisar e aprovar cotação
  # R1H19: Cotação aprovada é registrada
  Cenário: Aprovar cotação selecionada
    Dado que o sistema selecionou a melhor cotação para o produto "Produto X"
    Quando o cliente aprova a cotação
    Então a cotação deve ser marcada como "selecionada"
    E um pedido deve ser gerado utilizando essa cotação