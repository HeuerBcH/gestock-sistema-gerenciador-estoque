package dev.gestock.sge.aplicacao.movimentacao;

import java.time.LocalDate;
import java.util.List;

public interface MovimentacaoRepositorioAplicacao {
	List<MovimentacaoResumo> pesquisarResumos();

	List<MovimentacaoResumo> pesquisarPorPeriodo(LocalDate inicio, LocalDate fim);

	List<MovimentacaoResumo> pesquisarPorTipo(String tipo);

	MovimentacaoResumo obterResumo(int id);

	MovimentacaoTotais obterTotais();

	MovimentacaoTotais obterTotaisPorPeriodo(LocalDate inicio, LocalDate fim);
}

