# language: pt
Funcionalidade: Emitir Alertas de Estoque Baixo

  # H16: Ser notificado ao atingir ROP
  # R1H16: Alerta gerado automaticamente ao atingir ROP
  Cenario: Gerar alerta ao atingir ROP
    Dado que existe um produto com ROP de 100 unidades
    E o saldo atual do produto e 100 unidades
    Quando o sistema verifica o estoque
    Entao um alerta deve ser gerado automaticamente

  Cenario: Gerar alerta ao ficar abaixo do ROP
    Dado que existe um produto com ROP de 100 unidades
    E o saldo atual do produto e 90 unidades
    Quando o sistema verifica o estoque
    Entao um alerta deve ser gerado

  Cenario: Nao gerar alerta acima do ROP
    Dado que existe um produto com ROP de 100 unidades
    E o saldo atual do produto e 150 unidades
    Quando o sistema verifica o estoque
    Entao nenhum alerta deve ser gerado

  # R2H16: Alerta indica produto, estoque e fornecedor sugerido
  Cenario: Alerta contem informacoes completas
    Dado que existe um alerta gerado para um produto
    E o fornecedor sugerido possui cotacao valida e ativa
    Quando o cliente visualiza o alerta
    Entao o sistema deve exibir o nome do produto
    E o sistema deve exibir o estoque afetado
    E o sistema deve exibir o fornecedor com menor cotacao

  # H17: Visualizar lista de alertas ativos
  Cenario: Listar todos os alertas ativos
    Dado que existem 3 alertas ativos
    Quando o cliente visualiza a lista de alertas
    Entao o sistema deve exibir 3 alertas

  # R1H17: Alerta removido apos recebimento do pedido
  Cenario: Remover alerta apos recebimento
    Dado que existe um alerta ativo para um produto
    E um pedido foi recebido para suprir o estoque do produto
    Quando o sistema atualiza o estoque
    Entao o alerta deve ser removido automaticamente