# language: pt
Funcionalidade: Emitir Alertas de Estoque Baixo
  Como cliente
  Desejo ser notificado quando o estoque atingir o ROP
  Para fazer pedidos de reposição

  # H16: Ser notificado ao atingir ROP
  # R1H16: Alerta gerado automaticamente ao atingir ROP
  Cenário: Gerar alerta ao atingir ROP
    Dado que existe um produto com ROP de "100" unidades
    E o saldo atual é "100" unidades
    Quando o sistema verifica o estoque
    Então um alerta deve ser gerado automaticamente

  Cenário: Gerar alerta ao ficar abaixo do ROP
    Dado que existe um produto com ROP de "100" unidades
    E o saldo atual é "90" unidades
    Quando o sistema verifica o estoque
    Então um alerta deve ser gerado

  Cenário: Não gerar alerta acima do ROP
    Dado que existe um produto com ROP de "100" unidades
    E o saldo atual é "150" unidades
    Quando o sistema verifica o estoque
    Então nenhum alerta deve ser gerado

  # R2H16: Alerta indica produto, estoque e fornecedor sugerido
  Cenário: Alerta contém informações completas
    Dado que existe um alerta gerado
    Quando eu visualizo o alerta
    Então devo ver o nome do produto
    E devo ver o estoque afetado
    E devo ver o fornecedor com menor cotação

  # H17: Visualizar lista de alertas ativos
  Cenário: Listar todos os alertas ativos
    Dado que existem 3 alertas ativos
    Quando eu visualizo a lista de alertas
    Então devo ver 3 alertas

  # R1H17: Alerta removido após recebimento do pedido
  Cenário: Remover alerta após recebimento
    Dado que existe um alerta ativo para um produto
    E um pedido foi recebido para esse produto
    Quando o sistema atualiza os alertas
    Então o alerta deve ser removido automaticamente
