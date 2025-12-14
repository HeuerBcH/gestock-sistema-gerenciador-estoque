-- Remove coluna QUANTIDADE da tabela PRODUTO
-- A quantidade agora é controlada exclusivamente pela tabela ESTOQUE_PRODUTO
ALTER TABLE PRODUTO DROP COLUMN IF EXISTS QUANTIDADE;

