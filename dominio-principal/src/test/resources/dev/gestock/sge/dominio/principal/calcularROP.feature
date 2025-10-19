# language: pt
Funcionalidade: Calcular Ponto de Ressuprimento (ROP)
  Como cliente
  Desejo que o sistema calcule automaticamente o ROP e que eu possa ver os valores de ROP dos meus produtos
  Para saber quando reabastecer produtos

  # H14: Calcular ROP automaticamente
  # R1H14: ROP = (Consumo Médio Diário × Lead Time) + Estoque de Segurança
  Cenário: Calcular ROP com dados válidos
    Dado que existe um produto chamado "Produto A"
    E o consumo médio diário do produto é 10 unidades
    E o lead time do fornecedor é 7 dias
    E o estoque de segurança é 20 unidades
    Quando eu calculo o ROP
    Então o ROP do produto deve ser 90 unidades

  # R2H14: O histórico deve considerar consumo médio dos últimos 90 dias
  Cenário: Calcular ROP com histórico de 90 dias
    Dado que existe um produto chamado "Produto B"
    E o histórico de consumo dos últimos 90 dias
    Quando o sistema calcula o consumo médio
    Então o ROP do produto deve ser calculado com base nesse histórico

  # H15: Visualizar valores de ROP
  Cenário: Visualizar ROP de um produto
    Dado que existe um produto chamado "Produto A" com ROP calculado
    Quando eu clico para visualizar o ROP dos produtos
    Então devo ver o valor do ROP
    E devo ver o consumo médio utilizado no cálculo

  # R1H15: Produtos sem histórico usam ROP padrão
  Cenário: Produto sem histórico usa ROP padrão
    Dado que existe um produto chamado "Produto C" sem histórico
    Quando eu tento calcular o ROP
    Então o sistema deve usar um ROP padrão de 50 unidades
