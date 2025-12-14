CREATE TABLE CLIENTE (
    ID int generated always as identity not null,
    NOME varchar not null,
    EMAIL varchar not null unique,
    DOCUMENTO varchar not null unique,
    SENHA varchar not null,
    PRIMARY KEY (ID)
);

