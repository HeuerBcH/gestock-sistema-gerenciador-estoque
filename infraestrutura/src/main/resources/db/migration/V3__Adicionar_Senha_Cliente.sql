-- ============================================
-- Migration: Adicionar coluna SENHA_HASH na tabela CLIENTE
-- ============================================

SET search_path TO gestock;

-- Adiciona a coluna SENHA_HASH
ALTER TABLE CLIENTE 
ADD COLUMN IF NOT EXISTS SENHA_HASH varchar(255);

-- Atualiza os clientes existentes com senha padrão "senha123"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
UPDATE CLIENTE 
SET SENHA_HASH = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE SENHA_HASH IS NULL;

-- Torna a coluna obrigatória após popular os dados
ALTER TABLE CLIENTE 
ALTER COLUMN SENHA_HASH SET NOT NULL;
