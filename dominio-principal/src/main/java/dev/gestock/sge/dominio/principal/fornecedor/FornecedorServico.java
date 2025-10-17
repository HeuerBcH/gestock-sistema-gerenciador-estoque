package src.main.java.dev.gestock.sge.dominio.principal.fornecedor;

import src.main.java.dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de domínio para regras que envolvem múltiplos fornecedores.
 * Exemplo: seleção da cotação mais vantajosa (R5, R6).
 */
public class FornecedorServico {

    private final FornecedorRepositorio repositorio;

    public FornecedorServico(FornecedorRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Seleciona automaticamente a melhor cotação entre fornecedores.
     * Critérios:
     *  - Menor preço (R5)
     *  - Menor prazo em caso de empate (R6)
     */
    public Optional<Cotacao> selecionarMelhorCotacao(List<Fornecedor> fornecedores, ProdutoId produtoId) {
        return fornecedores.stream()
                .map(f -> f.obterCotacaoPorProduto(produtoId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min((c1, c2) -> {
                    int cmp = Double.compare(c1.getPreco(), c2.getPreco());
                    return (cmp != 0) ? cmp : Integer.compare(c1.getPrazoDias(), c2.getPrazoDias());
                });
    }
}
