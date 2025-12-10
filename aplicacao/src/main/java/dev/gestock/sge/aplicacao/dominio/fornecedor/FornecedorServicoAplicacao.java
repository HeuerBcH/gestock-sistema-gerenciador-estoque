package dev.gestock.sge.aplicacao.dominio.fornecedor;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class FornecedorServicoAplicacao {
	private final FornecedorRepositorioAplicacao repositorio;

	public FornecedorServicoAplicacao(FornecedorRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<FornecedorResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
}
