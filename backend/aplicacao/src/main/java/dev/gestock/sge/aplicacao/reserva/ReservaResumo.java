package dev.gestock.sge.aplicacao.reserva;

import java.time.LocalDateTime;

public interface ReservaResumo {
	int getId();

	int getPedidoId();

	String getPedidoCodigo();

	int getProdutoId();

	String getProdutoNome();

	int getQuantidade();

	LocalDateTime getDataHoraReserva();

	String getStatus();

	String getTipoLiberacao();

	LocalDateTime getDataHoraLiberacao();
}

