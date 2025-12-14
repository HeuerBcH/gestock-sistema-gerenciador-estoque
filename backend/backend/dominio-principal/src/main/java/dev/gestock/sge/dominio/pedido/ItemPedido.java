package dev.gestock.sge.dominio.pedido;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;
import dev.gestock.sge.dominio.fornecedor.Custo;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.Quantidade;

public class ItemPedido {
	private final ProdutoId produtoId;
	private final Quantidade quantidade;
	private final Custo precoUnitario;

	public ItemPedido(ProdutoId produtoId, Quantidade quantidade, Custo precoUnitario) {
		notNull(produtoId, "O id do produto não pode ser nulo");
		notNull(quantidade, "A quantidade não pode ser nula");
		notNull(precoUnitario, "O preço unitário não pode ser nulo");

		this.produtoId = produtoId;
		this.quantidade = quantidade;
		this.precoUnitario = precoUnitario;
	}

	public ProdutoId getProdutoId() {
		return produtoId;
	}

	public Quantidade getQuantidade() {
		return quantidade;
	}

	public Custo getPrecoUnitario() {
		return precoUnitario;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ItemPedido) {
			var item = (ItemPedido) obj;
			return produtoId.equals(item.produtoId) && quantidade.equals(item.quantidade)
					&& precoUnitario.equals(item.precoUnitario);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(produtoId, quantidade, precoUnitario);
	}
}

