package dev.gestock.sge.dominio.principal.produto;

import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;

import static org.apache.commons.lang3.Validate.*;

// Serviço de domínio para gerenciamento de produtos.
public class ProdutoServico {

    private final ProdutoRepositorio produtoRepositorio;
    private final EstoqueRepositorio estoqueRepositorio;
    private final PedidoRepositorio pedidoRepositorio;

    public ProdutoServico(ProdutoRepositorio produtoRepositorio) {
        this(produtoRepositorio, null, null);
    }

    public ProdutoServico(ProdutoRepositorio produtoRepositorio, 
                          EstoqueRepositorio estoqueRepositorio,
                          PedidoRepositorio pedidoRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
        this.estoqueRepositorio = estoqueRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
    }

    // Cadastra um novo produto (H8)

    public void cadastrar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        
        // R1H8: Validar código único
        CodigoProduto codigo = new CodigoProduto(produto.getCodigo());
        if (produtoRepositorio.codigoExiste(codigo)) {
            throw new IllegalArgumentException("Já existe um produto com este código (R1H8)");
        }
        
        produtoRepositorio.salvar(produto);
    }

    // Atualiza informações de um produto (H9)

    public void atualizar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        produtoRepositorio.salvar(produto);
    }

    // Inativa um produto (H10)
    public void inativar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        
        if (estoqueRepositorio != null) {
        }
        
        if (pedidoRepositorio != null) {
        }
        
        produto.inativar(); 
        produtoRepositorio.inativar(produto);
    }

    // Salva um produto no repositório (compatibilidade).
    public void salvar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        produtoRepositorio.salvar(produto);
    }
}
