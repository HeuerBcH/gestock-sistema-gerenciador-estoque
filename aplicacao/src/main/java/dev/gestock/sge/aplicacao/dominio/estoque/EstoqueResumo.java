package dev.gestock.sge.aplicacao.dominio.estoque;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;

public interface EstoqueResumo {
	EstoqueId getId();

	ClienteId getClienteId();

	String getNome();

	String getEndereco();

	int getCapacidade();

	boolean isAtivo();
}
