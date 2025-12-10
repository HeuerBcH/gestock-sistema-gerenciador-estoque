package dev.gestock.sge.aplicacao.dominio.produto;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class ProdutoServicoAplicacao {
	private final ProdutoRepositorioAplicacao repositorio;

	public ProdutoServicoAplicacao(ProdutoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<ProdutoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
}
