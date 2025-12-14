-- Adiciona coluna ESTOQUE_ID na tabela PEDIDO
-- Esta coluna referencia o estoque de destino do pedido
-- Quando o pedido é recebido (status = RECEBIDO), os produtos são adicionados ao estoque

ALTER TABLE PEDIDO ADD COLUMN ESTOQUE_ID INTEGER REFERENCES ESTOQUE(ID);

