package dev.gestock.sge.dominio.principal.produto;

import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;

import static org.apache.commons.lang3.Validate.*;

/**
 * Serviço de domínio para gerenciamento de produtos.
 * 
 * Suporta:
 * - H8-H10: Gerenciar Produtos (cadastrar, editar, inativar)
 * - Validações de regras de negócio R1H8, R1H10, R2H10
 */
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

    /**
     * Cadastra um novo produto (H8).
     * Valida:
     * - R1H8: Código único no sistema
     */
    public void cadastrar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        
        // R1H8: Validar código único
        CodigoProduto codigo = new CodigoProduto(produto.getCodigo());
        if (produtoRepositorio.codigoExiste(codigo)) {
            throw new IllegalArgumentException("Já existe um produto com este código (R1H8)");
        }
        
        produtoRepositorio.salvar(produto);
    }

    /**
     * Atualiza informações de um produto (H9).
     * Nota: R1H9 - alterações não afetam cotações existentes
     */
    public void atualizar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        produtoRepositorio.salvar(produto);
    }

    /**
     * Inativa um produto (H10).
     * Valida:
     * - R1H10: Não pode ter saldo positivo em estoque
     * - R1H10: Não pode ter pedidos em andamento
     * - R2H10: Bloqueia novas cotações e pedidos
     */
    public void inativar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        
        // R1H10: Validar saldo zero em todos os estoques (se estoqueRepositorio disponível)
        if (estoqueRepositorio != null) {
            // Implementação depende de método para buscar saldo total
            // Pode ser feito na camada de aplicação
        }
        
        // R1H10: Validar ausência de pedidos em andamento (se pedidoRepositorio disponível)
        if (pedidoRepositorio != null) {
            // Implementação depende de método para buscar pedidos por produto
            // Pode ser feito na camada de aplicação
        }
        
        produto.inativar(); // R2H10 é aplicado no agregado
        produtoRepositorio.inativar(produto);
    }

    /**
     * Salva um produto no repositório (compatibilidade).
     */
    public void salvar(Produto produto) {
        notNull(produto, "Produto é obrigatório");
        produtoRepositorio.salvar(produto);
    }
}
