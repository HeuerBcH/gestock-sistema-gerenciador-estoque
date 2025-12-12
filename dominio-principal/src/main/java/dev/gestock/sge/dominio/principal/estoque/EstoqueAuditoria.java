package dev.gestock.sge.dominio.principal.estoque;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;

/**
 * Interface para registrar eventos de auditoria relacionados a operações do EstoqueRepositorio.
 * 
 * Responsabilidade:
 * - Define o contrato para registrar eventos de auditoria (salvar, buscar, remover)
 * - Permite diferentes implementações (console, banco de dados, arquivo, serviço externo)
 * 
 * Padrão: Strategy - permite trocar a estratégia de auditoria sem modificar o decorator
 */
public interface EstoqueAuditoria {
    
    /**
     * Registra a operação de salvar um estoque.
     * @param estoque O estoque que foi salvo
     */
    void registrarSalvar(Estoque estoque);
    
    /**
     * Registra a operação de buscar um estoque por ID.
     * @param id O ID do estoque que foi buscado
     */
    void registrarLeitura(EstoqueId id);
    
    /**
     * Registra a operação de buscar estoques por cliente.
     * @param clienteId O ID do cliente usado na busca
     */
    void registrarBuscaPorCliente(ClienteId clienteId);
}

