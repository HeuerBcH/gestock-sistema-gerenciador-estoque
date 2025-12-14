package dev.gestock.sge.aplicacao.alerta;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;

public class AlertaServicoAplicacao {
	private AlertaRepositorioAplicacao repositorio;

	public AlertaServicoAplicacao(AlertaRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<AlertaResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public List<AlertaResumo> pesquisarPorNivel(String nivel) {
		if (nivel == null || nivel.isBlank()) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorNivel(nivel);
	}

	public AlertaTotais obterTotais() {
		return repositorio.obterTotais();
	}
}

