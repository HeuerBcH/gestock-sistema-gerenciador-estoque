package dev.gestock.sge.aplicacao.dominio.estoque;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.notNull;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
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
	
	public List<EstoqueResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public List<EstoqueResumo> pesquisarComFiltros(String busca, ClienteId clienteId, String status) {
		// Converte status string para Boolean
		Boolean ativo = null;
		if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("todos")) {
			ativo = status.equalsIgnoreCase("ativo");
		}
		return repositorio.pesquisarComFiltros(busca, clienteId, ativo);
	}
}
