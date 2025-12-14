CREATE TABLE COTACAO (
    ID int generated always as identity not null,
    PRODUTO_ID int not null,
    FORNECEDOR_ID int not null,
    PRECO decimal(10,2) not null,
    LEAD_TIME int not null,
    VALIDADE varchar not null,
    STATUS_APROVACAO varchar not null,
    PRIMARY KEY (ID),
    FOREIGN KEY (PRODUTO_ID) REFERENCES PRODUTO(ID),
    FOREIGN KEY (FORNECEDOR_ID) REFERENCES FORNECEDOR(ID),
    UNIQUE (PRODUTO_ID, FORNECEDOR_ID)
);

