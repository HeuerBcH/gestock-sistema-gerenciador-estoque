package dev.gestock.sge.aplicacao.cotacao;

import java.math.BigDecimal;

public interface CotacaoResumo {
	int getId();

	int getProdutoId();

	String getProdutoNome();

	int getFornecedorId();

	String getFornecedorNome();

	BigDecimal getPreco();

	int getLeadTime();

	String getValidade();

	String getStatusAprovacao();

	boolean isMaisVantajosa();
}

