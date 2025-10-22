package dev.gestock.sge.dominio.principal.cliente;

import java.util.Optional;

// Repositório do agregado Cliente.

public interface ClienteRepositorio {

    // Persiste o agregado Cliente (criação ou atualização)
    void salvar(Cliente cliente);

    // Recupera um Cliente pelo seu identificador
    Optional<Cliente> buscarPorId(ClienteId id);
}
