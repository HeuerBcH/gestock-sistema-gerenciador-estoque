# language: pt
Funcionalidade: Transferir Produtos entre Estoques
  Como cliente
  Desejo transferir produtos entre estoques
  Para equilibrar quantidades

  # H22: Transferir produtos
  # R1H22: Transferência apenas entre estoques do mesmo cliente
  Cenário: Transferir produto com sucesso
    Dado que existem dois estoques do mesmo cliente chamados "Estoque Origem" e "Estoque Destino"
    E o "Estoque Origem" possui 100 unidades do produto "Produto X"
    Quando eu transfiro 50 unidades do produto para o "Estoque Destino"
    Então o "Estoque Origem" deve ter 50 unidades do produto
    E o "Estoque Destino" deve ter 50 unidades do produto

  # R2H22: Origem deve ter quantidade suficiente
  Cenário: Tentar transferir sem saldo suficiente
    Dado que o "Estoque Origem" possui 30 unidades do produto
    Quando eu tento transferir 50 unidades do produto para o "Estoque Destino"
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Saldo disponível insuficiente"

  # R3H22: Transferência registra saída e entrada
  Cenário: Verificar movimentações da transferência
    Dado que eu transferi 50 unidades do produto do "Estoque Origem" para o "Estoque Destino"
    Quando eu verifico as movimentações
    Então deve existir uma SAÍDA no "Estoque Origem"
    E deve existir uma ENTRADA no "Estoque Destino"

  # H23: Visualizar histórico de transferências
  # R1H23: Histórico contém data, produto, quantidade, origem e destino
  Cenário: Visualizar histórico de transferências
    Dado que foram realizadas 3 transferências do produto entre estoques
    Quando eu visualizo o histórico de transferências
    Então devo ver 3 registros
    E cada registro deve conter produto, quantidade, estoque origem e estoque destino

  # R2H23: Não pode cancelar transferência concluída
  Cenário: Tentar cancelar transferência concluída
    Dado que existe uma transferência concluída do produto entre estoques
    Quando eu tento cancelar a transferência
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Transferência concluída não pode ser cancelada"
