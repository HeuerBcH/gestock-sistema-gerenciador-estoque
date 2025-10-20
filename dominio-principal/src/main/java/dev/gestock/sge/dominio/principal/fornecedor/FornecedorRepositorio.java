package dev.gestock.sge.dominio.principal.fornecedor;

import java.util.Optional;

/**
 * Repositório do agregado Fornecedor.
 * Suporta as funcionalidades H5-H7 (Gerenciar Fornecedores) e H18-H19 (Selecionar Cotação).
 * Implementação concreta ficará na camada de infraestrutura (ex.: JPA).
 */
public interface FornecedorRepositorio {
    
    /** Persiste ou atualiza um fornecedor (H5, H6) */
    void salvar(Fornecedor fornecedor);
    
    /** Busca um fornecedor por seu ID */
    Optional<Fornecedor> buscarPorId(FornecedorId id);
    
    /** Busca um fornecedor pelo CNPJ único */
    Optional<Fornecedor> buscarPorCnpj(String cnpj);
}
