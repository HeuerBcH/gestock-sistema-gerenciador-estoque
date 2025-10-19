package dev.gestock.sge.dominio.principal.produto;

/* Serviço de domínio: operações compostas sobre vários produtos. */
public class ProdutoServico {

    private final ProdutoRepositorio repositorio;

    public ProdutoServico(ProdutoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

}
