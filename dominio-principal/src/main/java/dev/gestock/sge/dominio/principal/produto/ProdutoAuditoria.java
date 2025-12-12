package dev.gestock.sge.dominio.principal.produto;

/**
 * Interface para registrar eventos de auditoria relacionados a operações do ProdutoRepositorio.
 * 
 * Responsabilidade:
 * - Define o contrato para registrar eventos de auditoria (salvar, buscar, inativar)
 * - Permite diferentes implementações (console, banco de dados, arquivo, serviço externo)
 * 
 * Padrão: Strategy - permite trocar a estratégia de auditoria sem modificar o decorator
 */
public interface ProdutoAuditoria {
    
    /**
     * Registra a operação de salvar um produto.
     * @param produto O produto que foi salvo
     */
    void registrarSalvar(Produto produto);
    
    /**
     * Registra a operação de buscar um produto por ID.
     * @param id O ID do produto que foi buscado
     */
    void registrarLeitura(ProdutoId id);
    
    /**
     * Registra a operação de buscar um produto por código.
     * @param codigo O código do produto usado na busca
     */
    void registrarBuscaPorCodigo(CodigoProduto codigo);
    
    /**
     * Registra a operação de inativar um produto.
     * @param produto O produto que foi inativado
     */
    void registrarInativacao(Produto produto);
}

