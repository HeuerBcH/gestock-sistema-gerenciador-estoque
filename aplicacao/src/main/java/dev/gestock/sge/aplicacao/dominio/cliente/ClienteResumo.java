package dev.gestock.sge.aplicacao.dominio.cliente;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;

public interface ClienteResumo {
	ClienteId getId();

	String getNome();

	String getDocumento();

	String getEmail();
}
