package dev.gestock.sge.aplicacao.dominio.pedido;

import java.time.LocalDate;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.pedido.StatusPedido;

public interface PedidoResumo {
	PedidoId getId();

	ClienteId getClienteId();

	FornecedorId getFornecedorId();

	LocalDate getDataCriacao();

	LocalDate getDataPrevistaEntrega();

	EstoqueId getEstoqueId();

	StatusPedido getStatus();
}
