package dev.gestock.sge.dominio.principal.estoque;

/**
 * Interface para observadores de eventos relacionados a Estoque.
 * 
 * Responsabilidade:
 * - Define o contrato para observadores que reagem a eventos de estoque
 * - Permite desacoplamento entre serviços de estoque e serviços que reagem a mudanças
 * 
 * Padrão: Observer
 * - Permite que componentes reajam a eventos de estoque de forma desacoplada
 * - Extensível: novos observers podem ser adicionados sem modificar o código existente
 * - Single Responsibility: cada observer tem UMA responsabilidade específica
 */
public interface EstoqueObserver {
    
    /**
     * Notifica que um estoque foi atualizado.
     * Permite que observers verifiquem se alertas devem ser removidos (R1H17).
     * 
     * @param estoqueId O ID do estoque que foi atualizado
     */
    void aoAtualizarEstoque(EstoqueId estoqueId);
}

