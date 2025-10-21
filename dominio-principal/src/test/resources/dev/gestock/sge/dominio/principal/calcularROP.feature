# language: pt
Funcionalidade: Calcular Ponto de Ressuprimento (ROP)


  # H14: Calcular ROP automaticamente
  # R1H14: ROP = (Consumo Medio Diario × Lead Time) + Estoque de Seguranca
  Cenario: Calcular ROP com dados validos
    Dado que existe um produto chamado "Produto A"
    E o consumo medio diario do produto e 10 unidades
    E o lead time do fornecedor e 7 dias
    E o estoque de seguranca e 20 unidades
    Quando o ROP do produto for calculado
    Entao o ROP do produto deve ser 90 unidades

  # R2H14: O historico deve considerar consumo medio dos ultimos 90 dias
  Cenario: Calcular ROP com historico de 90 dias
    Dado que existe um produto chamado "Produto B"
    E o historico de consumo dos ultimos 90 dias
    Quando o sistema calcula o consumo medio
    Entao o ROP do produto e calculado com base nesse historico

  # H15: Visualizar valores de ROP
  Cenario: Visualizar ROP de um produto
    Dado que existe um produto chamado "Produto A" com ROP calculado
    Quando o cliente clica para visualizar o ROP dos produtos
    Entao o sistema deve exibir o valor do ROP
    E o sistema deve exibir o consumo medio utilizado no calculo

  # R1H15: Produtos sem historico usam ROP padrao
  Cenario: Produto sem historico usa ROP padrao
    Dado que existe um produto chamado "Produto C" sem historico
    Quando O sistema tentar calcular o ROP do produto
    Entao o sistema deve usar um ROP padrao de 1 unidade
