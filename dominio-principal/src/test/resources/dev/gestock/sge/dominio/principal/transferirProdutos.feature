# language: pt
Funcionalidade: Transferir Produtos entre Estoques
  Como cliente
  Desejo transferir produtos entre estoques
  Para equilibrar quantidades

  # H22: Transferir produtos
  # R1H22: Transferência apenas entre estoques do mesmo cliente
  Cenário: Transferir produto com sucesso
    Dado que existem dois estoques do mesmo cliente
    E o estoque origem tem 100 unidades do produto
    Quando eu transfiro 50 unidades para o estoque destino
    Então o estoque origem deve ter 50 unidades
    E o estoque destino deve ter 50 unidades

  # R2H22: Origem deve ter quantidade suficiente
  Cenário: Tentar transferir sem saldo suficiente
    Dado que o estoque origem tem 30 unidades
    Quando eu tento transferir 50 unidades
    Então o sistema deve rejeitar a operação
    E deve exibir a mensagem "Saldo disponível insuficiente"

  # R3H22: Transferência registra saída e entrada
  Cenário: Verificar movimentações da transferência
    Dado que eu transferi 50 unidades entre estoques
    Quando eu verifico as movimentações
    Então deve existir uma SAIDA no estoque origem
    E deve existir uma ENTRADA no estoque destino

  # H23: Visualizar histórico de transferências
  # R1H23: Histórico contém data, produto, quantidade, origem e destino
  Cenário: Visualizar histórico de transferências
    Dado que foram realizadas 3 transferências
    Quando eu visualizo o histórico de transferências
    Então devo ver 3 registros
    E cada registro deve conter origem e destino

  # R2H23: Não pode cancelar transferência concluída
  Cenário: Tentar cancelar transferência concluída
    Dado que existe uma transferência concluída
    Quando eu tento cancelar a transferência
    Então o sistema deve rejeitar a operação
