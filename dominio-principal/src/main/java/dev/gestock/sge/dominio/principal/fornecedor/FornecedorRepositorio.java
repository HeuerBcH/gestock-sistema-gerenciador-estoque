package dev.gestock.sge.dominio.principal.fornecedor;

import java.util.Optional;

// Repositório do agregado Fornecedor.
public interface FornecedorRepositorio {
    
    // Persiste ou atualiza um fornecedor (H5, H6)
    void salvar(Fornecedor fornecedor);
    
    // Busca um fornecedor por seu ID
    Optional<Fornecedor> buscarPorId(FornecedorId id);
    
    // Busca um fornecedor pelo CNPJ único
    Optional<Fornecedor> buscarPorCnpj(String cnpj);
}
