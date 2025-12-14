CREATE TABLE PRODUTO (
    ID int generated always as identity not null,
    CODIGO varchar not null unique,
    NOME varchar not null,
    PESO int not null,
    QUANTIDADE int not null,
    PERECIVEL varchar not null,
    STATUS varchar not null,
    PRIMARY KEY (ID)
);

CREATE TABLE PRODUTO_FORNECEDOR (
    PRODUTO_ID int not null,
    FORNECEDOR_ID int not null,
    PRIMARY KEY (PRODUTO_ID, FORNECEDOR_ID),
    FOREIGN KEY (PRODUTO_ID) REFERENCES PRODUTO(ID),
    FOREIGN KEY (FORNECEDOR_ID) REFERENCES FORNECEDOR(ID)
);

