package dev.gestock.sge.aplicacao.pedido;

import java.math.BigDecimal;

public interface ItemPedidoResumo {
	int getProdutoId();

	String getProdutoNome();

	int getQuantidade();

	BigDecimal getPrecoUnitario();

	BigDecimal getSubtotal();
}

