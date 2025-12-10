package dev.gestock.sge.aplicacao.dominio.alerta;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class AlertaServicoAplicacao {
	private final AlertaRepositorioAplicacao repositorio;

	public AlertaServicoAplicacao(AlertaRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<AlertaResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
}
