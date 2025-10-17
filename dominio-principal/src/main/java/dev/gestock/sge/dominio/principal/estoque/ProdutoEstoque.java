package dev.gestock.sge.dominio.principal.estoque;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

/**
 * Value Object: ProdutoEstoque
 * 
 * Representa a associação entre um produto e sua quantidade no estoque
 */
public class ProdutoEstoque {

    private final ProdutoId produto;
    private final double quantidade;

    public ProdutoEstoque(ProdutoId produto, double quantidade) {
        notNull(produto, "Produto é obrigatório");
        isTrue(quantidade >= 0, "Quantidade não pode ser negativa");

        this.produto = produto;
        this.quantidade = quantidade;
    }

    public ProdutoId getProduto() {
        return produto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProdutoEstoque other = (ProdutoEstoque) obj;
        return produto.equals(other.produto) && 
               Double.compare(quantidade, other.quantidade) == 0;
    }

    @Override
    public int hashCode() {
        return produto.hashCode() + Double.hashCode(quantidade);
    }

    @Override
    public String toString() {
        return String.format("ProdutoEstoque[%s, %.2f]", produto, quantidade);
    }
}
