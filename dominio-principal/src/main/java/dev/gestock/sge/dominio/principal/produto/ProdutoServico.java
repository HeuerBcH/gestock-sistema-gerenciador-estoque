package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

/* Serviço de domínio: operações compostas sobre vários produtos. */
public class ProdutoServico {

    private final ProdutoRepositorio repositorio;

    public ProdutoServico(ProdutoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Salva um produto no repositório.
     * @param produto o produto a ser salvo
     */
    public void salvar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        repositorio.salvar(produto);
    }

}
