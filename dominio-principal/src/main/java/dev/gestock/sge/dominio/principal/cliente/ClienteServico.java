package dev.gestock.sge.dominio.principal.cliente;

import static org.apache.commons.lang3.Validate.*;

/**
 * Serviço de domínio para gerenciamento de clientes.
 * 
 * Responsabilidade:
 * - Agrupar regras e operações que envolvem clientes
 * - Validar regras de negócio relacionadas a clientes
 * 
 * Suporta:
 * - R1H1: Todo estoque deve pertencer a um único cliente
 * - Validação de integridade do cliente
 */
public class ClienteServico {

    private final ClienteRepositorio repositorio;

    public ClienteServico(ClienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Cadastra um novo cliente no sistema.
     * Aplica validações antes de salvar.
     */
    public void registrarCliente(Cliente cliente) {
        notNull(cliente, "Cliente não pode ser nulo");
        repositorio.salvar(cliente);
    }

    /**
     * Valida se o cliente possui pelo menos um estoque cadastrado.
     */
    public boolean validarPossuiEstoques(Cliente cliente) {
        notNull(cliente, "Cliente é obrigatório");
        return cliente.possuiEstoques();
    }
}
