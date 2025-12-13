package dev.gestock.sge.aplicacao.dominio.produto;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

public class ProdutoServicoAplicacao {
	private final ProdutoRepositorioAplicacao repositorio;

	public ProdutoServicoAplicacao(ProdutoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<ProdutoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public Optional<ProdutoResumo> buscarPorId(ProdutoId id) {
		notNull(id, "ID do produto é obrigatório");
		return repositorio.buscarResumoPorId(id);
	}
}
