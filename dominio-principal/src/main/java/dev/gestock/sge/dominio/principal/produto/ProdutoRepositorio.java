package dev.gestock.sge.dominio.principal.produto;

import java.util.Optional;

/*
 Repositório do agregado Produto.
 Suporta as funcionalidades H8-H10 (Gerenciar Produtos) e suas regras de negócio.
 Responsável por buscar, salvar e validar unicidade do código (R1H8).
 */
public interface ProdutoRepositorio {
    
    // Persiste ou atualiza um produto (H8, H9)
    void salvar(Produto produto);
    
    // Busca um produto por seu ID - necessário para operações gerais do sistema
    Optional<Produto> buscarPorId(ProdutoId id);
    
    // Busca um produto pelo código único (R1H8)
    Optional<Produto> buscarPorCodigo(CodigoProduto codigo);
    
    // Verifica se um código já existe no sistema (R1H8) 
    boolean codigoExiste(CodigoProduto codigo);
    
    // Remove um produto do sistema (H10)
    void inativar(Produto produto);
}
