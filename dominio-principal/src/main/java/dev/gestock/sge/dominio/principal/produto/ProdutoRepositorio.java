package dev.gestock.sge.dominio.principal.produto;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Produto.
 * Responsável por buscar, salvar e validar unicidade do código (R1H8).
 */
public interface ProdutoRepositorio {
    void salvar(Produto produto);
    Optional<Produto> buscarPorId(ProdutoId id);
    Optional<Produto> buscarPorCodigo(CodigoProduto codigo);
    boolean codigoExiste(CodigoProduto codigo);
    List<Produto> buscarProdutosAtivos();
    List<Produto> buscarProdutosInativos();
    List<Produto> buscarProdutosPorCategoria(String categoria);
    List<Produto> buscarProdutosPereciveis();
    List<Produto> buscarProdutosPorClassificacaoABC(String classificacao);
    void excluir(Produto produto);
}
