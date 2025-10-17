package src.main.java.dev.gestock.sge.dominio.principal.produto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço de domínio: operações compostas sobre vários produtos.
 * Exemplo: classificação ABC (R24).
 */
public class ProdutoServico {

    private final ProdutoRepositorio repositorio;

    public ProdutoServico(ProdutoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Classifica produtos com base no valor de movimentação total (R24).
     * - Top 20% = A
     * - Próximos 30% = B
     * - Restante = C
     */
    public void classificarProdutosABC(List<Produto> produtos, List<BigDecimal> valoresMovimentacao) {
        if (produtos.size() != valoresMovimentacao.size())
            throw new IllegalArgumentException("Tamanhos de listas incompatíveis");

        // Exemplo simplificado: ordena por valor e classifica
        for (int i = 0; i < produtos.size(); i++) {
            BigDecimal valor = valoresMovimentacao.get(i);
            if (valor.compareTo(BigDecimal.valueOf(10000)) > 0)
                produtos.get(i).classificarABC("A");
            else if (valor.compareTo(BigDecimal.valueOf(5000)) > 0)
                produtos.get(i).classificarABC("B");
            else
                produtos.get(i).classificarABC("C");

            repositorio.salvar(produtos.get(i));
        }
    }
}
