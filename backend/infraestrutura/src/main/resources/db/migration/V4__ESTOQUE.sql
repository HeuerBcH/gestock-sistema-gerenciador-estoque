CREATE TABLE ESTOQUE (
    ID int generated always as identity not null,
    NOME varchar not null,
    ENDERECO varchar not null,
    CAPACIDADE int not null,
    STATUS varchar not null,
    PRIMARY KEY (ID)
);

CREATE TABLE ESTOQUE_PRODUTO (
    ESTOQUE_ID int not null,
    PRODUTO_ID int not null,
    QUANTIDADE int not null,
    PRIMARY KEY (ESTOQUE_ID, PRODUTO_ID),
    FOREIGN KEY (ESTOQUE_ID) REFERENCES ESTOQUE(ID),
    FOREIGN KEY (PRODUTO_ID) REFERENCES PRODUTO(ID)
);

