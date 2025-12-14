package dev.gestock.sge.aplicacao.alerta;

import java.time.LocalDateTime;

public interface AlertaResumo {
	String getNivel();

	int getProdutoId();

	String getProdutoNome();

	int getEstoqueId();

	String getEstoqueNome();

	int getQuantidadeAtual();

	int getRop();

	double getPercentualAbaixoRop();

	LocalDateTime getData();
}

