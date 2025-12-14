CREATE TABLE RESERVA (
    ID int generated always as identity not null,
    PEDIDO_ID int not null,
    PRODUTO_ID int not null,
    QUANTIDADE int not null,
    DATA_HORA_RESERVA timestamp not null,
    STATUS varchar not null,
    TIPO_LIBERACAO varchar,
    DATA_HORA_LIBERACAO timestamp,
    PRIMARY KEY (ID),
    FOREIGN KEY (PEDIDO_ID) REFERENCES PEDIDO(ID),
    FOREIGN KEY (PRODUTO_ID) REFERENCES PRODUTO(ID)
);

