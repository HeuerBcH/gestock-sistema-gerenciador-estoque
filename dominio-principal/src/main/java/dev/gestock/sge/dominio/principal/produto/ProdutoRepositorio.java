package dev.gestock.sge.dominio.principal.produto;

import java.util.Optional;

/* Repositório do agregado Produto. Responsável por buscar, salvar e validar unicidade do código (R29). */
public interface ProdutoRepositorio {
    void salvar(Produto produto);
    Optional<Produto> buscarPorCodigo(CodigoProduto codigo);
    boolean codigoExiste(CodigoProduto codigo);
    void excluir(Produto produto);
}
