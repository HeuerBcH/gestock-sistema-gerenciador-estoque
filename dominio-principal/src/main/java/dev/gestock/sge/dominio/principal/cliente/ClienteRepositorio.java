package dev.gestock.sge.dominio.principal.cliente;

import java.util.Optional;

/**
 * Repositório do agregado Cliente.
 * 
 * Função:
 * - Abstrai o acesso a dados, isolando o domínio de tecnologias (JPA, JDBC, etc).
 * - No DDD, o domínio nunca deve saber "como" os dados são persistidos.
 * 
 * Suporta:
 * - R1H1: Cada estoque deve pertencer a um único cliente
 * - Todas as funcionalidades que dependem da identificação do cliente
 */
public interface ClienteRepositorio {

    /** Persiste o agregado Cliente (criação ou atualização) */
    void salvar(Cliente cliente);

    /** Recupera um Cliente pelo seu identificador */
    Optional<Cliente> buscarPorId(ClienteId id);
}
