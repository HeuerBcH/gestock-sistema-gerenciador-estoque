package dev.gestock.sge.dominio.principal.fornecedor;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.*;

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
     * Salva um fornecedor no repositório.
     * @param fornecedor o fornecedor a ser salvo
     */
    public void salvar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        repositorio.salvar(fornecedor);
    }

    /**
     * Seleciona automaticamente a melhor cotação entre fornecedores.
     * Critérios:
     *  - Menor preço (R5)
     *  - Menor prazo em caso de empate (R6)
     */
    public Optional<Cotacao> selecionarMelhorCotacao(List<Fornecedor> fornecedores, ProdutoId produtoId) {
        return fornecedores.stream()
                .filter(Fornecedor::isAtivo)
                .map(f -> f.obterCotacaoPorProduto(produtoId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Cotacao::isValidadeAtiva)
                .min((c1, c2) -> {
                    int cmp = Double.compare(c1.getPreco(), c2.getPreco());
                    return (cmp != 0) ? cmp : Integer.compare(c1.getPrazoDias(), c2.getPrazoDias());
                });
    }
}
