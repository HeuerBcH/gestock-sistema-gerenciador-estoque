package dev.gestock.sge.dominio.principal.fornecedor;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Classe utilitária para seleção de cotação mais vantajosa
 * 
 * Regras implementadas:
 * - R1H18: Apenas cotações com validade ativa e fornecedor ativo podem ser consideradas
 * - R2H18: Em caso de empate de preços, o sistema prioriza o menor Lead Time
 */
public class SelecionadorCotacao {

    /**
     * Seleciona a cotação mais vantajosa para um produto
     * R1H18: Apenas cotações com validade ativa e fornecedor ativo podem ser consideradas
     * R2H18: Em caso de empate de preços, o sistema prioriza o menor Lead Time
     */
    public static Optional<Cotacao> selecionarCotacaoMaisVantajosa(ProdutoId produto, 
                                                                    List<Cotacao> cotacoes, 
                                                                    List<Fornecedor> fornecedores) {
        return cotacoes.stream()
                .filter(cotacao -> cotacao.getProduto().equals(produto))
                .filter(Cotacao::isValida) // R1H18: Apenas cotações válidas
                .filter(cotacao -> isFornecedorAtivo(cotacao, fornecedores)) // R1H18: Apenas fornecedores ativos
                .min((c1, c2) -> {
                    // R2H18: Em caso de empate de preços, prioriza o menor Lead Time
                    int comparacaoPreco = Double.compare(c1.getPreco(), c2.getPreco());
                    if (comparacaoPreco != 0) {
                        return comparacaoPreco; // Menor preço primeiro
                    }
                    
                    // Em caso de empate, compara Lead Time
                    int leadTime1 = getLeadTimeFornecedor(c1, fornecedores);
                    int leadTime2 = getLeadTimeFornecedor(c2, fornecedores);
                    return Integer.compare(leadTime1, leadTime2); // Menor Lead Time primeiro
                });
    }

    /**
     * Verifica se o fornecedor da cotação está ativo
     */
    private static boolean isFornecedorAtivo(Cotacao cotacao, List<Fornecedor> fornecedores) {
        return fornecedores.stream()
                .anyMatch(fornecedor -> fornecedor.isAtivo());
    }

    /**
     * Obtém o Lead Time do fornecedor da cotação
     */
    private static int getLeadTimeFornecedor(Cotacao cotacao, List<Fornecedor> fornecedores) {
        return fornecedores.stream()
                .filter(fornecedor -> fornecedor.isAtivo())
                .mapToInt(Fornecedor::getLeadTime)
                .findFirst()
                .orElse(Integer.MAX_VALUE); // Se não encontrar, retorna valor alto para despriorizar
    }
}
