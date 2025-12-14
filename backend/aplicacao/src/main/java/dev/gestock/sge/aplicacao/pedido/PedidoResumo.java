package dev.gestock.sge.aplicacao.pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PedidoResumo {
	int getId();

	int getFornecedorId();

	String getFornecedorNome();

	int getEstoqueId();

	String getEstoqueNome();

	List<ItemPedidoResumo> getItens();

	BigDecimal getValorTotal();

	LocalDate getDataPedido();

	LocalDate getDataPrevista();

	String getStatus();
}

