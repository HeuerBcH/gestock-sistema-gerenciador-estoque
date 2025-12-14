package dev.gestock.sge.aplicacao.produto;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;

public class ProdutoServicoAplicacao {
	private ProdutoRepositorioAplicacao repositorio;

	public ProdutoServicoAplicacao(ProdutoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<ProdutoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public List<ProdutoResumo> pesquisarPorNomeOuCodigo(String termo) {
		if (termo == null || termo.isBlank()) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorNomeOuCodigo(termo);
	}

	public List<ProdutoResumo> pesquisarPorStatus(String status) {
		if (status == null || status.isBlank() || status.equalsIgnoreCase("todos")) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorStatus(status);
	}

	public ProdutoResumo obterResumo(int id) {
		return repositorio.obterResumo(id);
	}
}

