package dev.gestock.sge.aplicacao.fornecedor;

import java.math.BigDecimal;

public interface FornecedorResumo {
	int getId();

	String getNome();

	String getCnpj();

	String getContato();

	int getLeadTime();

	BigDecimal getCusto();

	String getStatus();
}

