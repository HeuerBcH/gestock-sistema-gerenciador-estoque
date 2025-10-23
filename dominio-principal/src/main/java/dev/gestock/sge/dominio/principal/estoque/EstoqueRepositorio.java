package dev.gestock.sge.dominio.principal.estoque;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Estoque.
 * Suporta as funcionalidades H1-H4 (Gerenciar Estoques) e suas regras de negócio.
 * Implementações (JPA/JDBC/etc.) ficam na camada de infraestrutura.
 */
public interface EstoqueRepositorio {
    
    /** Persiste ou atualiza um estoque (H1, H2, H3) */
    void salvar(Estoque estoque);
    
    /** Busca um estoque por seu ID */
    Optional<Estoque> buscarPorId(EstoqueId id);
    
    /** Lista todos os estoques de um cliente (H4, R1H4) */
    List<Estoque> buscarEstoquesPorClienteId(ClienteId clienteId);
    
    /** Verifica se já existe um estoque no endereço para o cliente (R2H1) */
    boolean existePorEndereco(String endereco, ClienteId clienteId);
    
    /** Verifica se já existe um estoque com o nome para o cliente (R3H1) */
    boolean existePorNome(String nome, ClienteId clienteId);
}
