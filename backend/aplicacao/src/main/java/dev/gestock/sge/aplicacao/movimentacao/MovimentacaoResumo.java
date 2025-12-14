package dev.gestock.sge.aplicacao.movimentacao;

import java.time.LocalDateTime;

public interface MovimentacaoResumo {
	int getId();

	LocalDateTime getDataHora();

	String getTipo();

	int getProdutoId();

	String getProdutoNome();

	int getQuantidade();

	String getMotivo();

	int getEstoqueId();

	String getEstoqueNome();

	String getResponsavel();
}

