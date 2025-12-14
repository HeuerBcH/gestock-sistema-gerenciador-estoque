# Gestock - Sistema Gerenciador de Estoque

## 1. Descrição do Domínio

O **Gestock** é um sistema voltado para o **gerenciamento inteligente de estoques**.

O sistema tem como objetivo principal **otimizar o controle de produtos, pedidos, fornecedores e estoques**, garantindo visibilidade, rastreabilidade e automação de processos logísticos e operacionais.  

Localização no repositório:  
`Documentacao/Descrição do Domínio - Sistema de Gestão de Estoques.pdf`

---

## 2. Mapa de Histórias do Usuário

O **User Story Map** foi desenvolvido utilizando a ferramenta **Avion.io** e apresenta a estrutura das funcionalidades sob a ótica das histórias de usuário.

[Acessar o Mapa de Histórias do Usuário](https://gestock.avion.io/share/bp92kjfD92MCfSDRT)

---

## 3. Protótipos de Interface

Os protótipos foram criados para validar os fluxos principais do sistema e garantir uma experiência de uso fluida e intuitiva.  
O vídeo demonstra as principais telas e interações da aplicação.

[Assista ao Protótipo no YouTube](https://www.youtube.com/watch?v=PDMaYk-0dAI)

---

## 4. Modelo(s) de Subdomínio(s)

O(s) modelo(s) do domínio e subdomínios foram elaborados com o **Context Mapper**, respeitando os princípios de **Bounded Contexts** e **Ubiquitous Language**.  

Localização no repositório:  
`Gestock-Domain-Model.cml`

---

## 5. Definição de Funcionalidades, User Stories e Regras de Negócio

Este documento apresenta a **descrição detalhada das funcionalidades do sistema**, acompanhada das respectivas **histórias de usuário (User Stories)** e **regras de negócio (Business Rules)**, construídas a partir da **linguagem onipresente** e das necessidades identificadas no domínio.  

Localização no repositório:  
`Documentacao/Definição de Funcionalidades, User Stories e Regras de Negócio - Gestock.pdf`

---

## 6. Cenários de Teste BDD

Os **cenários de teste comportamentais (BDD)** foram escritos no formato **Gherkin**, descrevendo os comportamentos esperados do sistema sob a ótica do usuário e do domínio.  

Localização no repositório:  
`dominio-principal/src/test/resources/dev/gestock/sge/dominio/principal`

---

## 7. Automação de Testes BDD com Cucumber

Os cenários foram **automatizados com o framework Cucumber**, integrando as definições dos testes em Java com o domínio de negócio.  

Localização no repositório:  
`dominio-principal/src/test/java/dev/gestock/sge/dominio/principal`

Cada classe de step (`Funcionalidade`) implementa os comportamentos descritos nos arquivos `.feature`, assegurando a **validade das regras de domínio** e o **funcionamento das interações entre agregados**.  

---

## 8. Estruturação de pastas

O projeto segue a estrutura de pastas recomendada pelo professor na pasta backend. Já a pasta frontend foi criada para armazenar a estrutura de código necessária para disponibilizar a interface da aplicação.

---

## 9. Sceencast da aplicação

Link para acesso ao screencast https://youtu.be/tCxSNgAJKMM

## Autores

Projeto desenvolvido no contexto da disciplina **Requisitos, Projeto de Software e Validação** – CESAR School.

**Equipe Gestock:**  
- Bernardo Heuer  
- Eduardo Roma  
- Rodrigo Nunes  
- Ronaldo Souto Maior  
- Sílvio Fitipaldi  
