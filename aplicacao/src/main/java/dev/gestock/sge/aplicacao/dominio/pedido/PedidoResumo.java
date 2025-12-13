package dev.gestock.sge.aplicacao.dominio.pedido;

import java.time.LocalDate;

public interface PedidoResumo {
	Long getId();

	Long getClienteId();

	Long getFornecedorId();

	LocalDate getDataCriacao();

	LocalDate getDataPrevistaEntrega();

	Long getEstoqueId();

	String getStatus();
}
