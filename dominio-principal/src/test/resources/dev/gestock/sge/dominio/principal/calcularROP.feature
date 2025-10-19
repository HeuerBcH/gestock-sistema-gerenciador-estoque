# language: pt
Funcionalidade: Calcular Ponto de Ressuprimento (ROP)


  # H14: Calcular ROP automaticamente
  # R1H14: ROP = (Consumo Médio Diário × Lead Time) + Estoque de Segurança
  Cenário: Calcular ROP com dados válidos
    Dado que existe um produto chamado "Produto A"
    E o consumo médio diário do produto é 10 unidades
    E o lead time do fornecedor é 7 dias
    E o estoque de segurança é 20 unidades
    Quando o ROP do produto for calculado
    Então o ROP do produto deve ser 90 unidades

  # R2H14: O histórico deve considerar consumo médio dos últimos 90 dias
  Cenário: Calcular ROP com histórico de 90 dias
    Dado que existe um produto chamado "Produto B"
    E o histórico de consumo dos últimos 90 dias
    Quando o sistema calcula o consumo médio
    Então o ROP do produto é calculado com base nesse histórico

  # H15: Visualizar valores de ROP
  Cenário: Visualizar ROP de um produto
    Dado que existe um produto chamado "Produto A" com ROP calculado
    Quando o cliente clica para visualizar o ROP dos produtos
    Então o sistema deve exibir o valor do ROP
    E o sistema deve exibir o consumo médio utilizado no cálculo

  # R1H15: Produtos sem histórico usam ROP padrão
  Cenário: Produto sem histórico usa ROP padrão
    Dado que existe um produto chamado "Produto C" sem histórico
    Quando O sistema tentar calcular o ROP do produto
    Então o sistema deve usar um ROP padrão de 50 unidades