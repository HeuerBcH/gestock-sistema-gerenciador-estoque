package dev.gestock.sge.aplicacao.movimentacao;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDate;
import java.util.List;

public class MovimentacaoServicoAplicacao {
	private MovimentacaoRepositorioAplicacao repositorio;

	public MovimentacaoServicoAplicacao(MovimentacaoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<MovimentacaoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public List<MovimentacaoResumo> pesquisarPorPeriodo(LocalDate inicio, LocalDate fim) {
		return repositorio.pesquisarPorPeriodo(inicio, fim);
	}

	public List<MovimentacaoResumo> pesquisarPorTipo(String tipo) {
		if (tipo == null || tipo.isBlank()) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorTipo(tipo);
	}

	public MovimentacaoResumo obterResumo(int id) {
		return repositorio.obterResumo(id);
	}

	public MovimentacaoTotais obterTotais() {
		return repositorio.obterTotais();
	}

	public MovimentacaoTotais obterTotaisPorPeriodo(LocalDate inicio, LocalDate fim) {
		return repositorio.obterTotaisPorPeriodo(inicio, fim);
	}
}

