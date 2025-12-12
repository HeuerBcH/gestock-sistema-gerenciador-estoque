package dev.gestock.sge.aplicacao.dominio.estoque;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Optional;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;

public class EstoqueServicoAplicacao {
	private final EstoqueRepositorioAplicacao repositorio;

	public EstoqueServicoAplicacao(EstoqueRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}
	
	public Optional<EstoqueResumo> buscarPorId(EstoqueId id) {
		notNull(id, "ID do estoque é obrigatório");
		return repositorio.buscarResumoPorId(id);
	}
}
