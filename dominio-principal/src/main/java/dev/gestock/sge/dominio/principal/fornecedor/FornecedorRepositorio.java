package src.main.java.dev.gestock.sge.dominio.principal.fornecedor;

import java.util.Optional;

/**
 * Repositório do agregado Fornecedor.
 * Implementação concreta ficará na camada de infraestrutura (ex.: JPA).
 */
public interface FornecedorRepositorio {
    void salvar(Fornecedor fornecedor);
    Optional<Fornecedor> buscarPorId(FornecedorId id);
    Optional<Fornecedor> buscarPorCnpj(String cnpj);
}
