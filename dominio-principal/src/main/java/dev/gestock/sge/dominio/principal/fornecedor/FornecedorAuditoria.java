package dev.gestock.sge.dominio.principal.fornecedor;

/**
 * Interface para registrar eventos de auditoria relacionados a operações do FornecedorRepositorio.
 * 
 * Responsabilidade:
 * - Define o contrato para registrar eventos de auditoria (salvar, buscar)
 * - Permite diferentes implementações (console, banco de dados, arquivo, serviço externo)
 * 
 * Padrão: Strategy - permite trocar a estratégia de auditoria sem modificar o decorator
 */
public interface FornecedorAuditoria {
    
    /**
     * Registra a operação de salvar um fornecedor.
     * @param fornecedor O fornecedor que foi salvo
     */
    void registrarSalvar(Fornecedor fornecedor);
    
    /**
     * Registra a operação de buscar um fornecedor por ID.
     * @param id O ID do fornecedor que foi buscado
     */
    void registrarLeitura(FornecedorId id);
    
    /**
     * Registra a operação de buscar um fornecedor por CNPJ.
     * @param cnpj O CNPJ usado na busca
     */
    void registrarBuscaPorCnpj(String cnpj);
}

