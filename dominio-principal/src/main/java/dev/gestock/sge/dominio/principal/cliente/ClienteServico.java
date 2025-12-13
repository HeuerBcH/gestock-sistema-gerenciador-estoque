package dev.gestock.sge.dominio.principal.cliente;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

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

    /**
     * Busca um cliente pelo seu identificador.
     */
    public Cliente buscarPorId(ClienteId id) {
        notNull(id, "ID do cliente é obrigatório");
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
    }

    /**
     * Verifica se já existe um cliente com o email informado.
     */
    public boolean emailJaExiste(String email) {
        notNull(email, "Email é obrigatório");
        return repositorio.buscarPorEmail(email).isPresent();
    }

    /**
     * Verifica se já existe um cliente com o documento informado.
     */
    public boolean documentoJaExiste(String documento) {
        notNull(documento, "Documento é obrigatório");
        return repositorio.buscarPorDocumento(documento).isPresent();
    }

    /**
     * Busca um cliente pelo email.
     */
    public Cliente buscarPorEmail(String email) {
        notNull(email, "Email é obrigatório");
        return repositorio.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com email: " + email));
    }

    /**
     * Lista todos os clientes cadastrados.
     */
    public List<Cliente> listarTodos() {
        return repositorio.listarTodos();
    }
}
