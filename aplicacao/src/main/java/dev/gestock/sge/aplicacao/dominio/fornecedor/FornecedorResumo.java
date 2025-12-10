package dev.gestock.sge.aplicacao.dominio.fornecedor;

import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.LeadTime;

public interface FornecedorResumo {
	FornecedorId getId();

	String getNome();

	String getCnpj();

	String getContato();

	LeadTime getLeadTimeMedio();

	boolean isAtivo();
}
