CREATE TABLE MOVIMENTACAO (
    ID int generated always as identity not null,
    DATA_HORA timestamp not null,
    PRODUTO_ID int not null,
    ESTOQUE_ID int not null,
    QUANTIDADE int not null,
    TIPO varchar not null,
    MOTIVO varchar not null,
    RESPONSAVEL varchar not null,
    PRIMARY KEY (ID),
    FOREIGN KEY (PRODUTO_ID) REFERENCES PRODUTO(ID),
    FOREIGN KEY (ESTOQUE_ID) REFERENCES ESTOQUE(ID)
);

