# language: pt
Funcionalidade: Gerenciamento de Cliente
  Como um usuário do sistema
  Eu quero registrar e autenticar clientes
  Para que possam acessar o sistema de gestão de estoques

  Contexto:
    Dado que não existe nenhum cliente cadastrado

  Cenário: Registrar cliente com dados válidos
    Quando eu registro um cliente com nome "João Silva", email "joao@example.com", documento "12345678900" e senha "senha123"
    Então o cliente deve ser registrado com sucesso
    E o cliente deve ter o nome "João Silva"
    E o cliente deve ter o email "joao@example.com"

  Cenário: Tentar registrar cliente com email inválido
    Quando eu tento registrar um cliente com nome "João Silva", email "email-invalido", documento "12345678900" e senha "senha123"
    Então deve ocorrer um erro informando que o email do cliente é inválido

  Cenário: Tentar registrar cliente com CPF inválido
    Quando eu tento registrar um cliente com nome "João Silva", email "joao@example.com", documento "123" e senha "senha123"
    Então deve ocorrer um erro informando que o documento é inválido

  Cenário: Tentar registrar cliente com email duplicado
    Dado que existe um cliente cadastrado com email "joao@example.com"
    Quando eu tento registrar um cliente com nome "Maria Silva", email "joao@example.com", documento "98765432100" e senha "senha456"
    Então deve ocorrer um erro informando que já existe um cliente com este email

  Cenário: Tentar registrar cliente com documento duplicado
    Dado que existe um cliente cadastrado com documento "12345678900"
    Quando eu tento registrar um cliente com nome "Maria Silva", email "maria@example.com", documento "12345678900" e senha "senha456"
    Então deve ocorrer um erro informando que já existe um cliente com este documento

  Cenário: Autenticar cliente com credenciais válidas
    Dado que existe um cliente cadastrado com email "joao@example.com" e senha "senha123"
    Quando eu autentico com email "joao@example.com" e senha "senha123"
    Então a autenticação deve ser bem-sucedida
    E o cliente retornado deve ter o email "joao@example.com"

  Cenário: Tentar autenticar com email inexistente
    Quando eu tento autenticar com email "inexistente@example.com" e senha "senha123"
    Então deve ocorrer um erro informando que o email ou senha são inválidos

  Cenário: Tentar autenticar com senha incorreta
    Dado que existe um cliente cadastrado com email "joao@example.com" e senha "senha123"
    Quando eu tento autenticar com email "joao@example.com" e senha "senhaErrada"
    Então deve ocorrer um erro informando que o email ou senha são inválidos

  Cenário: Registrar cliente com CNPJ válido
    Quando eu registro um cliente com nome "Empresa XYZ", email "contato@empresa.com", documento "12345678000190" e senha "senha123"
    Então o cliente deve ser registrado com sucesso
    E o documento do cliente deve ser identificado como CNPJ

  Cenário: Alterar senha do cliente
    Dado que existe um cliente cadastrado com email "joao@example.com" e senha "senha123"
    Quando eu altero a senha do cliente para "novaSenha456"
    Então o cliente deve aceitar a nova senha "novaSenha456"
    E o cliente não deve mais aceitar a senha antiga "senha123"

