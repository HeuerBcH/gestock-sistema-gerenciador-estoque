package dev.gestock.sge.aplicacao.transferencia;

import java.time.LocalDateTime;

public interface TransferenciaResumo {
	int getId();

	LocalDateTime getDataHoraTransferencia();

	int getProdutoId();

	String getProdutoNome();

	int getQuantidade();

	int getEstoqueOrigemId();

	String getEstoqueOrigemNome();

	int getEstoqueDestinoId();

	String getEstoqueDestinoNome();

	String getResponsavel();

	String getMotivo();
}

