-- ============================================
-- Script de Povoamento do Banco de Dados Gestock
-- Sistema de Gestão de Estoques
-- Dados de exemplo para desenvolvimento e testes
-- ============================================

-- ============================================
-- CLIENTES
-- ============================================
insert into CLIENTE(NOME, DOCUMENTO, EMAIL) values
('Empresa ABC Ltda', '12.345.678/0001-90', 'contato@empresaabc.com.br'),
('Comércio XYZ S.A.', '98.765.432/0001-10', 'vendas@comercioxyz.com.br'),
('Distribuidora Norte Sul', '11.222.333/0001-44', 'admin@nortesul.com.br');

-- ============================================
-- ESTOQUES
-- ============================================
-- Estoque 1: Empresa ABC - Matriz
insert into ESTOQUE(CLIENTE_ID, NOME, ENDERECO, CAPACIDADE, ATIVO) values
(1, 'Matriz - Centro', 'Rua das Flores, 123 - Centro - São Paulo/SP', 10000, true);

-- Estoque 2: Empresa ABC - Filial
insert into ESTOQUE(CLIENTE_ID, NOME, ENDERECO, CAPACIDADE, ATIVO) values
(1, 'Filial - Zona Sul', 'Av. Paulista, 1000 - Zona Sul - São Paulo/SP', 5000, true);

-- Estoque 3: Comércio XYZ
insert into ESTOQUE(CLIENTE_ID, NOME, ENDERECO, CAPACIDADE, ATIVO) values
(2, 'Depósito Principal', 'Rodovia BR-101, Km 45 - Guarulhos/SP', 8000, true);

-- Estoque 4: Distribuidora Norte Sul
insert into ESTOQUE(CLIENTE_ID, NOME, ENDERECO, CAPACIDADE, ATIVO) values
(3, 'Armazém Central', 'Rua Industrial, 500 - Distrito Industrial - Campinas/SP', 15000, true);

-- ============================================
-- PRODUTOS
-- ============================================
insert into PRODUTO(CODIGO, NOME, UNIDADE_PESO, PESO, PERECIVEL, ATIVO) values
('PROD001', 'Notebook Dell Inspiron 15', 'kg', 2.5, false, true),
('PROD002', 'Mouse Logitech MX Master', 'g', 0.141, false, true),
('PROD003', 'Teclado Mecânico RGB', 'g', 0.850, false, true),
('PROD004', 'Monitor LG 27 polegadas', 'kg', 4.2, false, true),
('PROD005', 'Leite Integral 1L', 'kg', 1.03, true, true),
('PROD006', 'Açúcar Cristal 1kg', 'kg', 1.0, false, true),
('PROD007', 'Arroz Tipo 1 5kg', 'kg', 5.0, false, true),
('PROD008', 'Feijão Preto 1kg', 'kg', 1.0, false, true),
('PROD009', 'Óleo de Soja 900ml', 'kg', 0.9, false, true),
('PROD010', 'Café em Pó 500g', 'kg', 0.5, false, true);

-- ============================================
-- FORNECEDORES
-- ============================================
insert into FORNECEDOR(NOME, CNPJ, CONTATO, LEAD_TIME_MEDIO, ATIVO) values
('TechSupply Brasil', '12.345.678/0001-11', 'vendas@techsupply.com.br', 7, true),
('Eletrônicos Premium', '23.456.789/0001-22', 'compras@eletronicos.com.br', 5, true),
('Distribuidora Alimentos SA', '34.567.890/0001-33', 'pedidos@alimentos.com.br', 3, true),
('Atacadão Eletrônicos', '45.678.901/0001-44', 'contato@atacadao.com.br', 10, true),
('Supermercado Atacado', '56.789.012/0001-55', 'vendas@superatacado.com.br', 2, true);

-- ============================================
-- COTAÇÕES
-- ============================================
-- Cotações para produtos de tecnologia
insert into COTACAO(FORNECEDOR_ID, PRODUTO_ID, PRECO, PRAZO_DIAS, VALIDADE_ATIVA) values
-- Notebook (PROD001)
(1, 1, 3500.00, 7, true),
(2, 1, 3450.00, 5, true),
(4, 1, 3600.00, 10, true),
-- Mouse (PROD002)
(1, 2, 299.90, 7, true),
(2, 2, 289.90, 5, true),
(4, 2, 310.00, 10, true),
-- Teclado (PROD003)
(1, 3, 450.00, 7, true),
(2, 3, 435.00, 5, true),
(4, 3, 470.00, 10, true),
-- Monitor (PROD004)
(1, 4, 1200.00, 7, true),
(2, 4, 1150.00, 5, true),
(4, 4, 1250.00, 10, true);

-- Cotações para produtos alimentícios
insert into COTACAO(FORNECEDOR_ID, PRODUTO_ID, PRECO, PRAZO_DIAS, VALIDADE_ATIVA) values
-- Leite (PROD005)
(3, 5, 4.50, 3, true),
(5, 5, 4.30, 2, true),
-- Açúcar (PROD006)
(3, 6, 5.80, 3, true),
(5, 6, 5.50, 2, true),
-- Arroz (PROD007)
(3, 7, 28.90, 3, true),
(5, 7, 27.50, 2, true),
-- Feijão (PROD008)
(3, 8, 8.90, 3, true),
(5, 8, 8.50, 2, true),
-- Óleo (PROD009)
(3, 9, 6.50, 3, true),
(5, 9, 6.20, 2, true),
-- Café (PROD010)
(3, 10, 12.90, 3, true),
(5, 10, 12.50, 2, true);

-- ============================================
-- SALDOS DE PRODUTOS POR ESTOQUE
-- ============================================
-- Estoque 1 (Matriz - Empresa ABC)
insert into SALDO_PRODUTO(ESTOQUE_ID, PRODUTO_ID, FISICO, RESERVADO) values
(1, 1, 15, 2),   -- Notebook: 15 físicos, 2 reservados
(1, 2, 50, 5),   -- Mouse: 50 físicos, 5 reservados
(1, 3, 30, 0),   -- Teclado: 30 físicos, 0 reservados
(1, 4, 20, 3);   -- Monitor: 20 físicos, 3 reservados

-- Estoque 2 (Filial - Empresa ABC)
insert into SALDO_PRODUTO(ESTOQUE_ID, PRODUTO_ID, FISICO, RESERVADO) values
(2, 1, 8, 0),    -- Notebook: 8 físicos
(2, 2, 25, 2),   -- Mouse: 25 físicos, 2 reservados
(2, 4, 10, 0);   -- Monitor: 10 físicos

-- Estoque 3 (Comércio XYZ)
insert into SALDO_PRODUTO(ESTOQUE_ID, PRODUTO_ID, FISICO, RESERVADO) values
(3, 5, 200, 50), -- Leite: 200 físicos, 50 reservados
(3, 6, 150, 0),  -- Açúcar: 150 físicos
(3, 7, 100, 20), -- Arroz: 100 físicos, 20 reservados
(3, 8, 80, 10),  -- Feijão: 80 físicos, 10 reservados
(3, 9, 120, 0),  -- Óleo: 120 físicos
(3, 10, 90, 15); -- Café: 90 físicos, 15 reservados

-- Estoque 4 (Distribuidora Norte Sul)
insert into SALDO_PRODUTO(ESTOQUE_ID, PRODUTO_ID, FISICO, RESERVADO) values
(4, 5, 500, 100), -- Leite: 500 físicos, 100 reservados
(4, 6, 400, 0),   -- Açúcar: 400 físicos
(4, 7, 300, 50),  -- Arroz: 300 físicos, 50 reservados
(4, 8, 250, 30),  -- Feijão: 250 físicos, 30 reservados
(4, 9, 350, 0),   -- Óleo: 350 físicos
(4, 10, 200, 40); -- Café: 200 físicos, 40 reservados

-- ============================================
-- ROP (Ponto de Ressuprimento)
-- ============================================
-- ROP para produtos de tecnologia no Estoque 1
insert into ROP(ESTOQUE_ID, PRODUTO_ID, CONSUMO_MEDIO, LEAD_TIME_DIAS, ESTOQUE_SEGURANCA, VALOR_ROP) values
(1, 1, 2.5, 7, 5, 23),   -- Notebook: consumo 2.5/dia, lead 7 dias, segurança 5 → ROP = 23
(1, 2, 8.0, 7, 10, 66),  -- Mouse: consumo 8/dia, lead 7 dias, segurança 10 → ROP = 66
(1, 3, 3.0, 7, 5, 26),   -- Teclado: consumo 3/dia, lead 7 dias, segurança 5 → ROP = 26
(1, 4, 1.5, 7, 3, 14);   -- Monitor: consumo 1.5/dia, lead 7 dias, segurança 3 → ROP = 14

-- ROP para produtos alimentícios no Estoque 3
insert into ROP(ESTOQUE_ID, PRODUTO_ID, CONSUMO_MEDIO, LEAD_TIME_DIAS, ESTOQUE_SEGURANCA, VALOR_ROP) values
(3, 5, 50.0, 3, 30, 180),  -- Leite: consumo 50/dia, lead 3 dias, segurança 30 → ROP = 180
(3, 6, 30.0, 3, 20, 110),  -- Açúcar: consumo 30/dia, lead 3 dias, segurança 20 → ROP = 110
(3, 7, 15.0, 3, 10, 55),  -- Arroz: consumo 15/dia, lead 3 dias, segurança 10 → ROP = 55
(3, 8, 12.0, 3, 8, 44),    -- Feijão: consumo 12/dia, lead 3 dias, segurança 8 → ROP = 44
(3, 9, 25.0, 3, 15, 90),   -- Óleo: consumo 25/dia, lead 3 dias, segurança 15 → ROP = 90
(3, 10, 20.0, 3, 12, 72);  -- Café: consumo 20/dia, lead 3 dias, segurança 12 → ROP = 72

-- ROP para produtos alimentícios no Estoque 4
insert into ROP(ESTOQUE_ID, PRODUTO_ID, CONSUMO_MEDIO, LEAD_TIME_DIAS, ESTOQUE_SEGURANCA, VALOR_ROP) values
(4, 5, 120.0, 3, 50, 410), -- Leite: consumo 120/dia, lead 3 dias, segurança 50 → ROP = 410
(4, 6, 80.0, 3, 40, 280),   -- Açúcar: consumo 80/dia, lead 3 dias, segurança 40 → ROP = 280
(4, 7, 40.0, 3, 20, 140),  -- Arroz: consumo 40/dia, lead 3 dias, segurança 20 → ROP = 140
(4, 8, 35.0, 3, 15, 120),  -- Feijão: consumo 35/dia, lead 3 dias, segurança 15 → ROP = 120
(4, 9, 60.0, 3, 30, 210),  -- Óleo: consumo 60/dia, lead 3 dias, segurança 30 → ROP = 210
(4, 10, 50.0, 3, 25, 175); -- Café: consumo 50/dia, lead 3 dias, segurança 25 → ROP = 175

-- ============================================
-- PEDIDOS
-- ============================================
-- Pedido 1: Empresa ABC comprando notebooks
insert into PEDIDO(CLIENTE_ID, FORNECEDOR_ID, ESTOQUE_ID, DATA_CRIACAO, DATA_PREVISTA_ENTREGA, STATUS) values
(1, 2, 1, '2024-01-15', '2024-01-20', 'ENVIADO');

-- Pedido 2: Comércio XYZ comprando alimentos
insert into PEDIDO(CLIENTE_ID, FORNECEDOR_ID, ESTOQUE_ID, DATA_CRIACAO, DATA_PREVISTA_ENTREGA, STATUS) values
(2, 5, 3, '2024-01-20', '2024-01-22', 'RECEBIDO');

-- Pedido 3: Distribuidora Norte Sul comprando alimentos
insert into PEDIDO(CLIENTE_ID, FORNECEDOR_ID, ESTOQUE_ID, DATA_CRIACAO, DATA_PREVISTA_ENTREGA, STATUS) values
(3, 3, 4, '2024-01-18', '2024-01-21', 'EM_TRANSPORTE');

-- Pedido 4: Empresa ABC comprando mouses
insert into PEDIDO(CLIENTE_ID, FORNECEDOR_ID, ESTOQUE_ID, DATA_CRIACAO, DATA_PREVISTA_ENTREGA, STATUS) values
(1, 1, 1, '2024-01-22', '2024-01-29', 'CRIADO');

-- ============================================
-- ITENS DOS PEDIDOS
-- ============================================
-- Itens do Pedido 1 (Notebooks)
insert into ITEM_PEDIDO(PEDIDO_ID, PRODUTO_ID, QUANTIDADE, PRECO_UNITARIO) values
(1, 1, 10, 3450.00);

-- Itens do Pedido 2 (Alimentos)
insert into ITEM_PEDIDO(PEDIDO_ID, PRODUTO_ID, QUANTIDADE, PRECO_UNITARIO) values
(2, 5, 100, 4.30),
(2, 6, 80, 5.50),
(2, 7, 50, 27.50),
(2, 8, 40, 8.50);

-- Itens do Pedido 3 (Alimentos)
insert into ITEM_PEDIDO(PEDIDO_ID, PRODUTO_ID, QUANTIDADE, PRECO_UNITARIO) values
(3, 5, 200, 4.50),
(3, 9, 150, 6.50),
(3, 10, 100, 12.90);

-- Itens do Pedido 4 (Mouses)
insert into ITEM_PEDIDO(PEDIDO_ID, PRODUTO_ID, QUANTIDADE, PRECO_UNITARIO) values
(4, 2, 30, 299.90);

-- ============================================
-- CUSTOS DOS PEDIDOS
-- ============================================
-- Custo do Pedido 1
insert into CUSTO_PEDIDO(PEDIDO_ID, VALOR_ITENS, FRETE, CUSTOS_LOGISTICOS) values
(1, 34500.00, 500.00, 200.00);

-- Custo do Pedido 2
insert into CUSTO_PEDIDO(PEDIDO_ID, VALOR_ITENS, FRETE, CUSTOS_LOGISTICOS) values
(2, 8930.00, 150.00, 50.00);

-- Custo do Pedido 3
insert into CUSTO_PEDIDO(PEDIDO_ID, VALOR_ITENS, FRETE, CUSTOS_LOGISTICOS) values
(3, 10050.00, 300.00, 100.00);

-- Custo do Pedido 4
insert into CUSTO_PEDIDO(PEDIDO_ID, VALOR_ITENS, FRETE, CUSTOS_LOGISTICOS) values
(4, 8997.00, 200.00, 80.00);

-- ============================================
-- MOVIMENTAÇÕES DE ESTOQUE
-- ============================================
-- Movimentações de entrada (recebimentos)
insert into MOVIMENTACAO(ESTOQUE_ID, TIPO, PRODUTO_ID, QUANTIDADE, DATA_HORA, RESPONSAVEL, MOTIVO) values
-- Estoque 3 - Recebimento do Pedido 2
(3, 'ENTRADA', 5, 100, '2024-01-22 10:30:00', 'João Silva', 'Recebimento de pedido #2'),
(3, 'ENTRADA', 6, 80, '2024-01-22 10:30:00', 'João Silva', 'Recebimento de pedido #2'),
(3, 'ENTRADA', 7, 50, '2024-01-22 10:30:00', 'João Silva', 'Recebimento de pedido #2'),
(3, 'ENTRADA', 8, 40, '2024-01-22 10:30:00', 'João Silva', 'Recebimento de pedido #2'),
-- Estoque 1 - Entrada inicial de produtos
(1, 'ENTRADA', 1, 20, '2024-01-10 08:00:00', 'Maria Santos', 'Estoque inicial'),
(1, 'ENTRADA', 2, 60, '2024-01-10 08:00:00', 'Maria Santos', 'Estoque inicial'),
(1, 'ENTRADA', 3, 30, '2024-01-10 08:00:00', 'Maria Santos', 'Estoque inicial'),
(1, 'ENTRADA', 4, 25, '2024-01-10 08:00:00', 'Maria Santos', 'Estoque inicial');

-- Movimentações de saída (vendas/consumo)
insert into MOVIMENTACAO(ESTOQUE_ID, TIPO, PRODUTO_ID, QUANTIDADE, DATA_HORA, RESPONSAVEL, MOTIVO) values
-- Estoque 1 - Saídas
(1, 'SAIDA', 1, 5, '2024-01-12 14:20:00', 'Pedro Costa', 'Venda para cliente'),
(1, 'SAIDA', 2, 10, '2024-01-13 09:15:00', 'Pedro Costa', 'Venda para cliente'),
(1, 'SAIDA', 4, 5, '2024-01-14 11:30:00', 'Pedro Costa', 'Venda para cliente'),
-- Estoque 3 - Saídas
(3, 'SAIDA', 5, 50, '2024-01-19 16:45:00', 'Ana Paula', 'Venda no varejo'),
(3, 'SAIDA', 6, 30, '2024-01-19 16:45:00', 'Ana Paula', 'Venda no varejo'),
(3, 'SAIDA', 7, 20, '2024-01-19 16:45:00', 'Ana Paula', 'Venda no varejo'),
(3, 'SAIDA', 8, 15, '2024-01-19 16:45:00', 'Ana Paula', 'Venda no varejo'),
(3, 'SAIDA', 9, 25, '2024-01-20 10:00:00', 'Ana Paula', 'Venda no varejo'),
(3, 'SAIDA', 10, 20, '2024-01-20 10:00:00', 'Ana Paula', 'Venda no varejo');

-- ============================================
-- REGISTROS DE RESERVA
-- ============================================
-- Reservas para o Pedido 1 (ainda não recebido)
insert into RESERVA_REGISTRO(ESTOQUE_ID, PRODUTO_ID, QUANTIDADE, DATA_HORA, TIPO) values
(1, 1, 10, '2024-01-15 09:00:00', 'RESERVA');

-- Reservas para o Pedido 4 (criado)
insert into RESERVA_REGISTRO(ESTOQUE_ID, PRODUTO_ID, QUANTIDADE, DATA_HORA, TIPO) values
(1, 2, 30, '2024-01-22 10:00:00', 'RESERVA');

-- Reservas para o Pedido 3 (em transporte)
insert into RESERVA_REGISTRO(ESTOQUE_ID, PRODUTO_ID, QUANTIDADE, DATA_HORA, TIPO) values
(4, 5, 200, '2024-01-18 08:00:00', 'RESERVA'),
(4, 9, 150, '2024-01-18 08:00:00', 'RESERVA'),
(4, 10, 100, '2024-01-18 08:00:00', 'RESERVA');

-- ============================================
-- ALERTAS DE ESTOQUE BAIXO
-- ============================================
-- Alerta para Mouse no Estoque 1 (saldo abaixo do ROP)
-- Saldo atual: 50, ROP: 66 → abaixo do ROP
insert into ALERTA(PRODUTO_ID, ESTOQUE_ID, FORNECEDOR_SUGERIDO_ID, DATA_GERACAO, ATIVO) values
(2, 1, 2, '2024-01-21 08:00:00', true);

-- Alerta para Açúcar no Estoque 3 (saldo próximo do ROP)
-- Saldo atual: 150, ROP: 110 → ainda acima, mas pode gerar alerta preventivo
-- (Este é apenas um exemplo, na prática o alerta seria gerado quando saldo <= ROP)

-- ============================================
-- TRANSFERÊNCIAS ENTRE ESTOQUES
-- ============================================
-- Transferência de Notebook do Estoque 1 (Matriz) para Estoque 2 (Filial)
insert into TRANSFERENCIA(PRODUTO_ID, ESTOQUE_ORIGEM_ID, ESTOQUE_DESTINO_ID, QUANTIDADE, DATA_HORA, RESPONSAVEL, MOTIVO) values
(1, 1, 2, 5, '2024-01-16 14:00:00', 'Carlos Mendes', 'Reabastecimento da filial');

-- Transferência de Mouse do Estoque 1 para Estoque 2
insert into TRANSFERENCIA(PRODUTO_ID, ESTOQUE_ORIGEM_ID, ESTOQUE_DESTINO_ID, QUANTIDADE, DATA_HORA, RESPONSAVEL, MOTIVO) values
(2, 1, 2, 10, '2024-01-17 10:30:00', 'Carlos Mendes', 'Reabastecimento da filial');
