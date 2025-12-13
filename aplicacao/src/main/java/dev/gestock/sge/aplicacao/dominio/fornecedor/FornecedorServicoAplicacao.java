package dev.gestock.sge.aplicacao.dominio.fornecedor;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;

public class FornecedorServicoAplicacao {
	private final FornecedorRepositorioAplicacao repositorio;

	public FornecedorServicoAplicacao(FornecedorRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<FornecedorResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public Optional<FornecedorResumo> buscarPorId(FornecedorId id) {
		notNull(id, "ID do fornecedor é obrigatório");
		return repositorio.buscarResumoPorId(id);
	}
	
	public List<FornecedorResumo> pesquisarComFiltros(String busca, Boolean ativo) {
		return repositorio.pesquisarComFiltros(busca, ativo);
	}
}
