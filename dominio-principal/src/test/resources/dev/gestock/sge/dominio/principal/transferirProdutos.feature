# language: pt
Funcionalidade: Transferir Produtos entre Estoques


  # H22: Transferir produtos
  # R1H22: Transferencia apenas entre estoques do mesmo cliente
  Cenario: Transferir produto com sucesso
    Dado que existem dois estoques do mesmo cliente chamados "Estoque Origem" e "Estoque Destino"
    E o "Estoque Origem" possui 100 unidades do produto "Produto X"
    Quando o cliente transfere 50 unidades do produto para o "Estoque Destino"
    Entao o "Estoque Origem" deve ter 50 unidades do produto
    E o "Estoque Destino" deve receber 50 unidades do produto

  # R2H22: Origem deve ter quantidade suficiente
  Cenario: Tentar transferir sem saldo suficiente
    Dado que o "Estoque Origem" possui 30 unidades do produto
    Quando o cliente tenta transferir 50 unidades do produto para o "Estoque Destino"
    Entao o sistema deve rejeitar a operacao
    E deve exibir a mensagem "Saldo disponivel insuficiente"

  # R3H22: Transferencia registra saida e entrada
  Cenario: Verificar movimentacoes da transferencia
    Dado que um cliente transfere 50 unidades do produto do "Estoque Origem" para o "Estoque Destino"
    Quando o cliente verifica as movimentacoes
    Entao o sistema deve exibir uma SAIDA no "Estoque Origem"
    E o sistema deve exibir uma ENTRADA no "Estoque Destino"

  # H23: Visualizar historico de transferencias
  # R1H23: Historico contem data, produto, quantidade, origem e destino
  Cenario: Visualizar historico de transferencias
    Dado que foram realizadas 3 transferencias de produto entre estoques
    Quando o clinete visualiza o historico de transferencias
    Entao o sistema deve exibir 3 registros
    E cada registro deve conter produto, quantidade, estoque origem e estoque destino

  # R2H23: Nao pode cancelar transferencia concluida
  Cenario: Tentar cancelar transferencia concluida
    Dado que existe uma transferencia concluida de produto entre estoques
    Quando o cliente tenta cancelar a transferencia
    Entao o sistema deve rejeitar a operacao
    E deve exibir a mensagem "Transferencia concluida nao pode ser cancelada"
