package src.main.java.dev.gestock.sge.dominio.principal.pedido;

import static org.apache.commons.lang3.Validate.*;
import src.main.java.dev.gestock.sge.dominio.principal.produto.ProdutoId;
import java.math.BigDecimal;

/** Entidade: item do pedido (produto + quantidade + preço unitário). */
public class ItemPedido {
    private final ProdutoId produtoId;
    private final int quantidade;
    private final BigDecimal precoUnitario;

    public ItemPedido(ProdutoId produtoId, int quantidade, BigDecimal precoUnitario) {
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser > 0");
        notNull(precoUnitario, "Preço unitário é obrigatório");
        isTrue(precoUnitario.signum() >= 0, "Preço unitário inválido");

        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public ProdutoId getProdutoId(){ return produtoId; }
    public int getQuantidade(){ return quantidade; }
    public BigDecimal getPrecoUnitario(){ return precoUnitario; }
    public BigDecimal getSubtotal(){ return precoUnitario.multiply(BigDecimal.valueOf(quantidade)); }
}
