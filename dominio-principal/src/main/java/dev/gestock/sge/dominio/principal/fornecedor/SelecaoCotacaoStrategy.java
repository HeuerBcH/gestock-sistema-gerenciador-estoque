package dev.gestock.sge.dominio.principal.fornecedor;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Interface para estratégias de seleção de melhor cotação.
 * 
 * Responsabilidade:
 * - Define o contrato para algoritmos de seleção de cotação
 * - Permite diferentes critérios de seleção (menor preço, menor prazo, melhor relação)
 * 
 * Padrão: Strategy
 * - Encapsula diferentes algoritmos de seleção de cotação
 * - Permite trocar o algoritmo sem modificar o código que o usa
 * - Facilita testabilidade e extensibilidade
 */
public interface SelecaoCotacaoStrategy {
    
    /**
     * Seleciona a melhor cotação entre fornecedores para um produto específico.
     * 
     * @param fornecedores Lista de fornecedores a considerar
     * @param produtoId O produto para o qual selecionar a cotação
     * @return A melhor cotação encontrada, ou Optional.empty() se nenhuma for encontrada
     */
    Optional<Cotacao> selecionar(List<Fornecedor> fornecedores, ProdutoId produtoId);
}

