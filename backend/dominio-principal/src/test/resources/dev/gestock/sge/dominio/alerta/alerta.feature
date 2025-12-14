# language: pt
Funcionalidade: Gerenciamento de Alerta
  Como um usuário do sistema
  Eu quero receber alertas sobre produtos abaixo do ROP
  Para tomar ações preventivas de ressuprimento

  Cenário: Determinar nível crítico de alerta
    Dado que o percentual abaixo do ROP é -65.5
    Quando eu determino o nível do alerta
    Então o nível do alerta deve ser CRITICO

