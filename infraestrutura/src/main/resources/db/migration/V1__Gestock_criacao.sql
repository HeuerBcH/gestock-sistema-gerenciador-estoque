-- ============================================
-- Script de Criação do Banco de Dados Gestock
-- Sistema de Gestão de Estoques
-- ============================================

-- Tabela: CLIENTE
-- Representa o dono dos estoques (Aggregate Root)
-- Regras: R1H1 - Cada estoque pertence a um único cliente
create table CLIENTE (
    ID bigint generated always as identity not null,
    NOME varchar(255) not null,
    DOCUMENTO varchar(20) not null,
    EMAIL varchar(255) not null,
    primary key (ID),
    constraint UK_CLIENTE_DOCUMENTO unique (DOCUMENTO),
    constraint UK_CLIENTE_EMAIL unique (EMAIL)
);

-- Tabela: ESTOQUE
-- Representa um depósito/armazém físico
-- Regras: R1H1, R2H1, R3H1, R1H2, R2H2, R1H3
create table ESTOQUE (
    ID bigint generated always as identity not null,
    CLIENTE_ID bigint not null,
    NOME varchar(255) not null,
    ENDERECO varchar(500) not null,
    CAPACIDADE integer not null,
    ATIVO boolean not null default true,
    primary key (ID),
    foreign key (CLIENTE_ID) references CLIENTE(ID),
    constraint UK_ESTOQUE_NOME_CLIENTE unique (CLIENTE_ID, NOME),
    constraint UK_ESTOQUE_ENDERECO unique (ENDERECO),
    constraint CK_ESTOQUE_CAPACIDADE check (CAPACIDADE > 0)
);

-- Tabela: PRODUTO
-- Representa um item gerenciado no estoque
-- Regras: R1H8 - Código único por cliente, R1H10, R2H10
create table PRODUTO (
    ID bigint generated always as identity not null,
    CODIGO varchar(100) not null,
    NOME varchar(255) not null,
    UNIDADE_PESO varchar(50) not null,
    PESO decimal(10, 3) not null,
    PERECIVEL boolean not null default false,
    ATIVO boolean not null default true,
    primary key (ID),
    constraint UK_PRODUTO_CODIGO unique (CODIGO),
    constraint CK_PRODUTO_PESO check (PESO > 0)
);

-- Tabela: FORNECEDOR
-- Representa fornecedores de produtos
-- Regras: R2H5 - Lead Time positivo, R1H7
create table FORNECEDOR (
    ID bigint generated always as identity not null,
    NOME varchar(255) not null,
    CNPJ varchar(18) not null,
    CONTATO varchar(255),
    LEAD_TIME_MEDIO integer not null default 0,
    ATIVO boolean not null default true,
    primary key (ID),
    constraint UK_FORNECEDOR_CNPJ unique (CNPJ),
    constraint CK_FORNECEDOR_LEAD_TIME check (LEAD_TIME_MEDIO >= 0)
);

-- Tabela: COTACAO
-- Representa cotações de preço e prazo de fornecedores para produtos
-- Regras: R1H5 - Uma cotação por produto+fornecedor, R1H18, R2H18
create table COTACAO (
    ID bigint generated always as identity not null,
    FORNECEDOR_ID bigint not null,
    PRODUTO_ID bigint not null,
    PRECO decimal(15, 2) not null,
    PRAZO_DIAS integer not null,
    VALIDADE_ATIVA boolean not null default true,
    primary key (ID),
    foreign key (FORNECEDOR_ID) references FORNECEDOR(ID),
    foreign key (PRODUTO_ID) references PRODUTO(ID),
    constraint UK_COTACAO_FORNECEDOR_PRODUTO unique (FORNECEDOR_ID, PRODUTO_ID),
    constraint CK_COTACAO_PRECO check (PRECO > 0),
    constraint CK_COTACAO_PRAZO check (PRAZO_DIAS > 0)
);

-- Tabela: PEDIDO
-- Representa pedidos de compra para reabastecimento
-- Regras: R1H11, R2H11, R1H12, R1H13, R1H24, R1H25
create table PEDIDO (
    ID bigint generated always as identity not null,
    CLIENTE_ID bigint not null,
    FORNECEDOR_ID bigint not null,
    ESTOQUE_ID bigint,
    DATA_CRIACAO date not null,
    DATA_PREVISTA_ENTREGA date,
    STATUS varchar(20) not null,
    primary key (ID),
    foreign key (CLIENTE_ID) references CLIENTE(ID),
    foreign key (FORNECEDOR_ID) references FORNECEDOR(ID),
    foreign key (ESTOQUE_ID) references ESTOQUE(ID),
    constraint CK_PEDIDO_STATUS check (STATUS in ('CRIADO', 'ENVIADO', 'EM_TRANSPORTE', 'RECEBIDO', 'CANCELADO', 'CONCLUIDO'))
);

-- Tabela: ITEM_PEDIDO
-- Representa itens de um pedido
create table ITEM_PEDIDO (
    PEDIDO_ID bigint not null,
    PRODUTO_ID bigint not null,
    QUANTIDADE integer not null,
    PRECO_UNITARIO decimal(15, 2) not null,
    primary key (PEDIDO_ID, PRODUTO_ID),
    foreign key (PEDIDO_ID) references PEDIDO(ID),
    foreign key (PRODUTO_ID) references PRODUTO(ID),
    constraint CK_ITEM_PEDIDO_QUANTIDADE check (QUANTIDADE > 0),
    constraint CK_ITEM_PEDIDO_PRECO check (PRECO_UNITARIO >= 0)
);

-- Tabela: CUSTO_PEDIDO
-- Representa custos totais do pedido (R26)
create table CUSTO_PEDIDO (
    PEDIDO_ID bigint not null,
    VALOR_ITENS decimal(15, 2) not null,
    FRETE decimal(15, 2) not null,
    CUSTOS_LOGISTICOS decimal(15, 2) not null,
    primary key (PEDIDO_ID),
    foreign key (PEDIDO_ID) references PEDIDO(ID),
    constraint CK_CUSTO_PEDIDO_VALOR_ITENS check (VALOR_ITENS >= 0),
    constraint CK_CUSTO_PEDIDO_FRETE check (FRETE >= 0),
    constraint CK_CUSTO_PEDIDO_CUSTOS_LOGISTICOS check (CUSTOS_LOGISTICOS >= 0)
);

-- Tabela: SALDO_PRODUTO
-- Representa saldos físicos e reservados de produtos por estoque
-- Regras: R15 - Saldo disponível = físico - reservado
create table SALDO_PRODUTO (
    ESTOQUE_ID bigint not null,
    PRODUTO_ID bigint not null,
    FISICO integer not null default 0,
    RESERVADO integer not null default 0,
    primary key (ESTOQUE_ID, PRODUTO_ID),
    foreign key (ESTOQUE_ID) references ESTOQUE(ID),
    foreign key (PRODUTO_ID) references PRODUTO(ID),
    constraint CK_SALDO_PRODUTO_FISICO check (FISICO >= 0),
    constraint CK_SALDO_PRODUTO_RESERVADO check (RESERVADO >= 0),
    constraint CK_SALDO_PRODUTO_RESERVADO_FISICO check (RESERVADO <= FISICO)
);

-- Tabela: ROP
-- Representa Ponto de Ressuprimento por produto em cada estoque
-- Regras: R1H14, R2H14 - ROP = (Consumo Médio Diário × Lead Time) + Estoque de Segurança
create table ROP (
    ESTOQUE_ID bigint not null,
    PRODUTO_ID bigint not null,
    CONSUMO_MEDIO decimal(10, 3) not null,
    LEAD_TIME_DIAS integer not null,
    ESTOQUE_SEGURANCA integer not null,
    VALOR_ROP integer not null,
    primary key (ESTOQUE_ID, PRODUTO_ID),
    foreign key (ESTOQUE_ID) references ESTOQUE(ID),
    foreign key (PRODUTO_ID) references PRODUTO(ID),
    constraint CK_ROP_CONSUMO_MEDIO check (CONSUMO_MEDIO >= 0),
    constraint CK_ROP_LEAD_TIME check (LEAD_TIME_DIAS >= 0),
    constraint CK_ROP_ESTOQUE_SEGURANCA check (ESTOQUE_SEGURANCA >= 0),
    constraint CK_ROP_VALOR check (VALOR_ROP >= 0)
);

-- Tabela: MOVIMENTACAO
-- Representa histórico de movimentações de estoque (entrada/saída)
-- Regras: R1H20, R2H20, R1H21 - Histórico mantido por 12 meses
create table MOVIMENTACAO (
    ID bigint generated always as identity not null,
    ESTOQUE_ID bigint not null,
    TIPO varchar(10) not null,
    PRODUTO_ID bigint not null,
    QUANTIDADE integer not null,
    DATA_HORA timestamp not null,
    RESPONSAVEL varchar(255) not null,
    MOTIVO varchar(500),
    META varchar(2000), -- JSON ou texto estruturado para metadados
    primary key (ID),
    foreign key (ESTOQUE_ID) references ESTOQUE(ID),
    foreign key (PRODUTO_ID) references PRODUTO(ID),
    constraint CK_MOVIMENTACAO_TIPO check (TIPO in ('ENTRADA', 'SAIDA')),
    constraint CK_MOVIMENTACAO_QUANTIDADE check (QUANTIDADE > 0)
);

-- Tabela: RESERVA_REGISTRO
-- Representa histórico de reservas e liberações de estoque
-- Regras: R2H25 - Auditoria de reservas e liberações
create table RESERVA_REGISTRO (
    ID bigint generated always as identity not null,
    ESTOQUE_ID bigint not null,
    PRODUTO_ID bigint not null,
    QUANTIDADE integer not null,
    DATA_HORA timestamp not null,
    TIPO varchar(10) not null,
    primary key (ID),
    foreign key (ESTOQUE_ID) references ESTOQUE(ID),
    foreign key (PRODUTO_ID) references PRODUTO(ID),
    constraint CK_RESERVA_REGISTRO_TIPO check (TIPO in ('RESERVA', 'LIBERACAO')),
    constraint CK_RESERVA_REGISTRO_QUANTIDADE check (QUANTIDADE > 0)
);

-- Tabela: TRANSFERENCIA
-- Representa transferências de produtos entre estoques
-- Regras: R1H22, R2H22, R3H22, R1H23, R2H23
create table TRANSFERENCIA (
    ID bigint generated always as identity not null,
    PRODUTO_ID bigint not null,
    ESTOQUE_ORIGEM_ID bigint not null,
    ESTOQUE_DESTINO_ID bigint not null,
    QUANTIDADE integer not null,
    DATA_HORA timestamp not null,
    RESPONSAVEL varchar(255) not null,
    MOTIVO varchar(500),
    primary key (ID),
    foreign key (PRODUTO_ID) references PRODUTO(ID),
    foreign key (ESTOQUE_ORIGEM_ID) references ESTOQUE(ID),
    foreign key (ESTOQUE_DESTINO_ID) references ESTOQUE(ID),
    constraint CK_TRANSFERENCIA_QUANTIDADE check (QUANTIDADE > 0),
    constraint CK_TRANSFERENCIA_ESTOQUES_DIFERENTES check (ESTOQUE_ORIGEM_ID != ESTOQUE_DESTINO_ID)
);

-- Tabela: ALERTA
-- Representa alertas de estoque baixo (atingiu ROP)
-- Regras: R1H16, R2H16, R1H17
create table ALERTA (
    ID bigint generated always as identity not null,
    PRODUTO_ID bigint not null,
    ESTOQUE_ID bigint not null,
    FORNECEDOR_SUGERIDO_ID bigint,
    DATA_GERACAO timestamp not null,
    ATIVO boolean not null default true,
    primary key (ID),
    foreign key (PRODUTO_ID) references PRODUTO(ID),
    foreign key (ESTOQUE_ID) references ESTOQUE(ID),
    foreign key (FORNECEDOR_SUGERIDO_ID) references FORNECEDOR(ID)
);
