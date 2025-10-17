package dev.gestock.sge.dominio.principal.pedido;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

/**
 * Value Object: ItemPedido
 * 
 * Representa um item dentro de um pedido
 */
public class ItemPedido {

    private final ProdutoId produto;
    private final double quantidade;
    private final double precoUnitario;

    public ItemPedido(ProdutoId produto, double quantidade, double precoUnitario) {
        notNull(produto, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        isTrue(precoUnitario > 0, "Preço unitário deve ser positivo");

        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public ProdutoId getProduto() {
        return produto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    /**
     * Calcula o valor total do item
     */
    public double getValorTotal() {
        return quantidade * precoUnitario;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemPedido other = (ItemPedido) obj;
        return produto.equals(other.produto) && 
               Double.compare(quantidade, other.quantidade) == 0 &&
               Double.compare(precoUnitario, other.precoUnitario) == 0;
    }

    @Override
    public int hashCode() {
        return produto.hashCode() + Double.hashCode(quantidade) + Double.hashCode(precoUnitario);
    }

    @Override
    public String toString() {
        return String.format("ItemPedido[%s, %.2f x R$ %.2f = R$ %.2f]", 
                           produto, quantidade, precoUnitario, getValorTotal());
    }
}