# language: pt
Funcionalidade: Gerenciamento de Ponto de Ressuprimento
  Como um usuário do sistema
  Eu quero gerenciar pontos de ressuprimento
  Para controlar quando reabastecer produtos

  Contexto:
    Dado que existe um produto cadastrado para ponto de ressuprimento
    E que existe um estoque cadastrado para ponto de ressuprimento

  Cenário: Registrar ponto de ressuprimento
    Quando eu registro um ponto de ressuprimento com estoque de segurança 20
    Então o ponto de ressuprimento deve ser registrado com sucesso
    E o ponto de ressuprimento deve ter estoque de segurança 20

  Cenário: Determinar status adequado
    Dado que existe um ponto de ressuprimento
    E que o saldo atual é 100
    E que o ROP calculado é 58
    Quando eu determino o status do ROP
    Então o status deve ser ADEQUADO

  Cenário: Determinar status inadequado
    Dado que existe um ponto de ressuprimento
    E que o saldo atual é 30
    E que o ROP calculado é 58
    Quando eu determino o status do ROP
    Então o status deve ser INADEQUADO

  Cenário: Atualizar estoque de segurança
    Dado que existe um ponto de ressuprimento com estoque de segurança 20
    Quando eu atualizo o estoque de segurança para 25
    Então o ponto de ressuprimento deve ter estoque de segurança 25

  Cenário: Tentar registrar ponto de ressuprimento duplicado
    Dado que existe um ponto de ressuprimento para o estoque e produto
    Quando eu tento registrar outro ponto de ressuprimento para o mesmo estoque e produto
    Então deve ocorrer um erro informando que já existe um ponto de ressuprimento

