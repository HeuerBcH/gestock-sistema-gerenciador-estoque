CREATE TABLE PEDIDO (
    ID int generated always as identity not null,
    FORNECEDOR_ID int not null,
    VALOR_TOTAL decimal(10,2) not null,
    DATA_PEDIDO date not null,
    DATA_PREVISTA date not null,
    STATUS varchar not null,
    PRIMARY KEY (ID),
    FOREIGN KEY (FORNECEDOR_ID) REFERENCES FORNECEDOR(ID)
);

CREATE TABLE PEDIDO_ITEM (
    PEDIDO_ID int not null,
    PRODUTO_ID int not null,
    QUANTIDADE int not null,
    PRECO_UNITARIO decimal(10,2) not null,
    PRIMARY KEY (PEDIDO_ID, PRODUTO_ID),
    FOREIGN KEY (PEDIDO_ID) REFERENCES PEDIDO(ID),
    FOREIGN KEY (PRODUTO_ID) REFERENCES PRODUTO(ID)
);

