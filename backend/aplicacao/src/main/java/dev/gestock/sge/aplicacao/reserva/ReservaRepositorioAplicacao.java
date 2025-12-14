package dev.gestock.sge.aplicacao.reserva;

import java.util.List;

public interface ReservaRepositorioAplicacao {
	List<ReservaResumo> pesquisarResumos(String busca);

	ReservaTotais obterTotais();
}

