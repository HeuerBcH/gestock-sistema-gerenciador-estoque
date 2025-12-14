package dev.gestock.sge.aplicacao.alerta;

import java.util.List;

public interface AlertaRepositorioAplicacao {
	List<AlertaResumo> pesquisarResumos();

	List<AlertaResumo> pesquisarPorNivel(String nivel);

	AlertaTotais obterTotais();

	double calcularPercentualAbaixoRop(int saldoAtual, int rop);
}

