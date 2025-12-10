package dev.gestock.sge.aplicacao.dominio.estoque;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class EstoqueServicoAplicacao {
	private final EstoqueRepositorioAplicacao repositorio;

	public EstoqueServicoAplicacao(EstoqueRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<EstoqueResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
}
