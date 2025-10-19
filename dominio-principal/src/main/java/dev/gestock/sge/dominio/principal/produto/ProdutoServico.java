package dev.gestock.sge.dominio.principal.produto;

/**
 * Serviço de domínio: operações compostas sobre vários produtos.
 */
public class ProdutoServico {

    private final ProdutoRepositorio repositorio;

    public ProdutoServico(ProdutoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    // Classificação ABC removida - não necessária para o sistema
    
    /*
    public void classificarProdutosABC(List<Produto> produtos, List<BigDecimal> valoresMovimentacao) {
        if (produtos.size() != valoresMovimentacao.size())
            throw new IllegalArgumentException("Tamanhos de listas incompatíveis");

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
    */
}
