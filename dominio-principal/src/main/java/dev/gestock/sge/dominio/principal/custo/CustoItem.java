package dev.gestock.sge.dominio.principal.custo;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import java.math.BigDecimal;
import static org.apache.commons.lang3.Validate.*;

/**
 * Entidade: CustoItem
 * Representa o custo associado a um produto dentro de um pedido.
 *
 * Usado para calcular custo médio e valor total de estoque.
 */
public class CustoItem {

    private final ProdutoId produtoId;
    private final TipoCusto tipo;
    private final BigDecimal valor;

    public CustoItem(ProdutoId produtoId, TipoCusto tipo, BigDecimal valor) {
        notNull(produtoId, "Produto é obrigatório");
        notNull(tipo, "Tipo de custo é obrigatório");
        notNull(valor, "Valor é obrigatório");
        isTrue(valor.signum() >= 0, "Valor de custo deve ser positivo");

        this.produtoId = produtoId;
        this.tipo = tipo;
        this.valor = valor;
    }

    public ProdutoId getProdutoId() { return produtoId; }
    public TipoCusto getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }

    @Override
    public String toString() {
        return String.format("%s: R$%.2f", tipo, valor);
    }
}
