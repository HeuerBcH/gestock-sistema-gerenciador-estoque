CREATE TABLE FORNECEDOR (
    ID int generated always as identity not null,
    NOME varchar not null,
    CNPJ varchar not null unique,
    CONTATO varchar not null,
    LEAD_TIME int not null,
    CUSTO decimal(10,2) not null,
    STATUS varchar not null,
    PRIMARY KEY (ID)
);

