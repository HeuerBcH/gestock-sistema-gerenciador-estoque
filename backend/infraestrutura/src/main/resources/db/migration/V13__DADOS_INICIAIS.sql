-- ============================================
-- Script de População de Dados Iniciais
-- ============================================

-- 1. CLIENTES
INSERT INTO CLIENTE (NOME, EMAIL, DOCUMENTO, SENHA) VALUES
('Admin Sistema', 'admin@gestock.com', '00000000000', 'admin123'),
('João Silva', 'joao.silva@gestock.com', '12345678900', 'senha123'),
('Maria Santos', 'maria.santos@gestock.com', '98765432100', 'senha123'),
('Pedro Costa', 'pedro.costa@gestock.com', '11122233344', 'senha123'),
('Ana Oliveira', 'ana.oliveira@gestock.com', '55566677788', 'senha123');

-- 2. FORNECEDORES
INSERT INTO FORNECEDOR (NOME, CNPJ, CONTATO, LEAD_TIME, CUSTO, STATUS) VALUES
('Distribuidora ABC', '12345678000190', 'contato@abc.com', 5, 25.50, 'ATIVO'),
('Fornecedor XYZ Ltda', '98765432000110', 'vendas@xyz.com', 7, 22.00, 'ATIVO'),
('Importadora Global', '11222333000144', 'global@import.com', 15, 18.75, 'INATIVO'),
('Atacado Premium', '55666777000188', 'premium@atacado.com', 3, 28.00, 'ATIVO'),
('Logística Express', '99888777000122', 'express@logistica.com', 2, 32.00, 'ATIVO');

-- 3. PRODUTOS
INSERT INTO PRODUTO (CODIGO, NOME, PESO, PERECIVEL, STATUS) VALUES
('PRD001', 'Arroz Integral 1kg', 1000, 'NAO', 'ATIVO'),
('PRD002', 'Feijão Preto 1kg', 1000, 'NAO', 'ATIVO'),
('PRD003', 'Leite UHT 1L', 1050, 'SIM', 'ATIVO'),
('PRD004', 'Azeite Extra Virgem 500ml', 500, 'NAO', 'ATIVO'),
('PRD005', 'Macarrão Espaguete 500g', 500, 'NAO', 'ATIVO'),
('PRD006', 'Queijo Mussarela 500g', 500, 'SIM', 'INATIVO');

-- 4. PRODUTO_FORNECEDOR (Relacionamento Produto-Fornecedor)
INSERT INTO PRODUTO_FORNECEDOR (PRODUTO_ID, FORNECEDOR_ID) VALUES
(1, 1), (1, 2),  -- Arroz: Distribuidora ABC e Fornecedor XYZ
(2, 1),          -- Feijão: Distribuidora ABC
(3, 2), (3, 4),  -- Leite: Fornecedor XYZ e Atacado Premium
(4, 3),          -- Azeite: Importadora Global
(5, 1), (5, 4),  -- Macarrão: Distribuidora ABC e Atacado Premium
(6, 5);          -- Queijo: Logística Express

-- 5. ESTOQUES
INSERT INTO ESTOQUE (NOME, ENDERECO, CAPACIDADE, STATUS) VALUES
('Centro de Distribuição São Paulo', 'Av. Industrial, 1000 - SP', 10000, 'ATIVO'),
('Armazém Rio de Janeiro', 'Rua do Porto, 500 - RJ', 5000, 'ATIVO'),
('Depósito Minas Gerais', 'Rod. BR-040, km 50 - MG', 8000, 'ATIVO'),
('Estoque Reserva Sul', 'Av. das Nações, 2000 - PR', 3000, 'INATIVO');

-- 6. ESTOQUE_PRODUTO (Quantidades de produtos em cada estoque)
INSERT INTO ESTOQUE_PRODUTO (ESTOQUE_ID, PRODUTO_ID, QUANTIDADE) VALUES
-- Estoque 1 (São Paulo)
(1, 1, 500),  -- Arroz: 500
(1, 2, 300),  -- Feijão: 300
(1, 3, 200),  -- Leite: 200
(1, 4, 150),  -- Azeite: 150
(1, 5, 800),  -- Macarrão: 800
(1, 6, 80),   -- Queijo: 80
-- Estoque 2 (Rio de Janeiro)
(2, 1, 250),  -- Arroz: 250
(2, 2, 200),  -- Feijão: 200
(2, 3, 150),  -- Leite: 150
(2, 4, 100),  -- Azeite: 100
(2, 5, 400),  -- Macarrão: 400
-- Estoque 3 (Minas Gerais)
(3, 1, 300),  -- Arroz: 300
(3, 2, 250),  -- Feijão: 250
(3, 3, 180),  -- Leite: 180
(3, 5, 600);  -- Macarrão: 600

-- 7. COTAÇÕES
INSERT INTO COTACAO (PRODUTO_ID, FORNECEDOR_ID, PRECO, LEAD_TIME, VALIDADE, STATUS_APROVACAO) VALUES
(1, 1, 25.50, 5, '2024-12-31', 'APROVADA'),   -- Arroz - Distribuidora ABC
(1, 2, 24.00, 7, '2024-12-31', 'PENDENTE'),   -- Arroz - Fornecedor XYZ
(2, 1, 18.50, 5, '2024-12-31', 'APROVADA'),   -- Feijão - Distribuidora ABC
(3, 2, 22.00, 7, '2024-12-31', 'APROVADA'),   -- Leite - Fornecedor XYZ
(3, 4, 24.00, 3, '2024-12-31', 'APROVADA'),   -- Leite - Atacado Premium
(4, 3, 18.75, 15, '2024-12-31', 'APROVADA'),  -- Azeite - Importadora Global
(5, 1, 12.50, 5, '2024-12-31', 'APROVADA'),   -- Macarrão - Distribuidora ABC
(5, 4, 13.00, 3, '2024-12-31', 'APROVADA'),   -- Macarrão - Atacado Premium
(6, 5, 32.00, 2, '2024-12-31', 'PENDENTE');   -- Queijo - Logística Express

-- 8. MOVIMENTAÇÕES
INSERT INTO MOVIMENTACAO (DATA_HORA, PRODUTO_ID, ESTOQUE_ID, QUANTIDADE, TIPO, MOTIVO, RESPONSAVEL) VALUES
('2024-01-15 09:30:00', 1, 1, 100, 'ENTRADA', 'Recebimento de pedido', 'João Silva'),
('2024-01-15 11:00:00', 3, 1, 50, 'SAIDA', 'Venda', 'Maria Santos'),
('2024-01-15 14:30:00', 2, 2, 30, 'SAIDA', 'Transferência', 'Pedro Costa'),
('2024-01-16 08:00:00', 5, 1, 200, 'ENTRADA', 'Recebimento de pedido', 'Ana Oliveira'),
('2024-01-16 10:15:00', 1, 3, 25, 'SAIDA', 'Venda', 'Carlos Mendes'),
('2024-01-17 09:00:00', 3, 2, 100, 'ENTRADA', 'Recebimento de pedido', 'João Silva'),
('2024-01-17 14:00:00', 4, 1, 30, 'ENTRADA', 'Recebimento de pedido', 'Maria Santos'),
('2024-01-18 08:30:00', 2, 1, 50, 'ENTRADA', 'Recebimento de pedido', 'Pedro Costa'),
('2024-01-18 11:00:00', 5, 2, 100, 'ENTRADA', 'Recebimento de pedido', 'Ana Oliveira'),
('2024-01-19 09:00:00', 1, 2, 75, 'SAIDA', 'Venda', 'Carlos Mendes');

-- 9. PONTOS DE RESSUPRIMENTO
INSERT INTO PONTO_RESSUPRIMENTO (ESTOQUE_ID, PRODUTO_ID, ESTOQUE_SEGURANCA) VALUES
(1, 1, 200),  -- Arroz no Estoque SP: estoque segurança 200
(1, 2, 150),  -- Feijão no Estoque SP: estoque segurança 150
(1, 3, 100),  -- Leite no Estoque SP: estoque segurança 100
(1, 4, 80),   -- Azeite no Estoque SP: estoque segurança 80
(1, 5, 300),  -- Macarrão no Estoque SP: estoque segurança 300
(2, 1, 150),  -- Arroz no Estoque RJ: estoque segurança 150
(2, 2, 100),  -- Feijão no Estoque RJ: estoque segurança 100
(2, 3, 80),   -- Leite no Estoque RJ: estoque segurança 80
(3, 1, 200),  -- Arroz no Estoque MG: estoque segurança 200
(3, 2, 150),  -- Feijão no Estoque MG: estoque segurança 150
(3, 3, 100);  -- Leite no Estoque MG: estoque segurança 100

-- 10. PEDIDOS
INSERT INTO PEDIDO (FORNECEDOR_ID, ESTOQUE_ID, VALOR_TOTAL, DATA_PEDIDO, DATA_PREVISTA, STATUS) VALUES
(1, 1, 3825.00, '2024-01-10', '2024-01-15', 'RECEBIDO'),      -- Pedido 1: Distribuidora ABC para SP
(2, 1, 4400.00, '2024-01-12', '2024-01-19', 'EM_TRANSPORTE'), -- Pedido 2: Fornecedor XYZ para SP
(3, 1, 3362.50, '2024-01-14', '2024-01-29', 'ENVIADO'),      -- Pedido 3: Importadora Global para SP
(1, 1, 12750.00, '2024-01-15', '2024-01-20', 'CRIADO'),       -- Pedido 4: Distribuidora ABC para SP
(2, 2, 3300.00, '2024-01-16', '2024-01-23', 'EM_TRANSPORTE'), -- Pedido 5: Fornecedor XYZ para RJ
(4, 1, 2600.00, '2024-01-17', '2024-01-20', 'RECEBIDO');      -- Pedido 6: Atacado Premium para SP

-- 11. PEDIDO_ITEM (Itens dos pedidos)
INSERT INTO PEDIDO_ITEM (PEDIDO_ID, PRODUTO_ID, QUANTIDADE, PRECO_UNITARIO) VALUES
-- Pedido 1: Arroz (100) e Feijão (50)
(1, 1, 100, 25.50),
(1, 2, 50, 18.50),
-- Pedido 2: Leite (200)
(2, 3, 200, 22.00),
-- Pedido 3: Azeite (30) e Macarrão (100)
(3, 4, 30, 18.75),
(3, 5, 100, 12.50),
-- Pedido 4: Arroz (500)
(4, 1, 500, 25.50),
-- Pedido 5: Leite (150)
(5, 3, 150, 22.00),
-- Pedido 6: Macarrão (200)
(6, 5, 200, 13.00);

-- 12. RESERVAS (Baseadas nos pedidos)
INSERT INTO RESERVA (PEDIDO_ID, PRODUTO_ID, QUANTIDADE, DATA_HORA_RESERVA, STATUS, TIPO_LIBERACAO, DATA_HORA_LIBERACAO) VALUES
(1, 1, 100, '2024-01-10 10:00:00', 'ATIVA', NULL, NULL),      -- Reserva Arroz do Pedido 1
(1, 2, 50, '2024-01-10 10:00:00', 'LIBERADA', 'AUTOMATICA', '2024-01-15 09:30:00'), -- Reserva Feijão do Pedido 1 (liberada)
(2, 3, 200, '2024-01-12 11:00:00', 'ATIVA', NULL, NULL),     -- Reserva Leite do Pedido 2
(3, 4, 30, '2024-01-14 14:00:00', 'ATIVA', NULL, NULL),       -- Reserva Azeite do Pedido 3
(3, 5, 100, '2024-01-14 14:00:00', 'ATIVA', NULL, NULL),      -- Reserva Macarrão do Pedido 3
(4, 1, 500, '2024-01-15 08:00:00', 'ATIVA', NULL, NULL),      -- Reserva Arroz do Pedido 4
(5, 3, 150, '2024-01-16 09:00:00', 'ATIVA', NULL, NULL),     -- Reserva Leite do Pedido 5
(6, 5, 200, '2024-01-17 10:00:00', 'LIBERADA', 'AUTOMATICA', '2024-01-17 14:00:00'); -- Reserva Macarrão do Pedido 6 (liberada)

-- 13. TRANSFERÊNCIAS (Entre estoques)
INSERT INTO TRANSFERENCIA (PRODUTO_ID, QUANTIDADE, ESTOQUE_ORIGEM_ID, ESTOQUE_DESTINO_ID, DATA_HORA_TRANSFERENCIA, RESPONSAVEL, MOTIVO, MOVIMENTACAO_SAIDA_ID, MOVIMENTACAO_ENTRADA_ID) VALUES
(2, 30, 1, 2, '2024-01-15 14:30:00', 'Pedro Costa', 'Balanceamento de estoque', 3, NULL), -- Transferência Feijão SP -> RJ
(1, 50, 1, 3, '2024-01-20 10:00:00', 'João Silva', 'Distribuição regional', NULL, NULL),   -- Transferência Arroz SP -> MG
(3, 25, 1, 2, '2024-01-22 11:00:00', 'Maria Santos', 'Atendimento demanda', NULL, NULL);   -- Transferência Leite SP -> RJ

