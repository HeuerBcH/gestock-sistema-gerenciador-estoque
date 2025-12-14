package dev.gestock.sge.aplicacao.estoque;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;
import dev.gestock.sge.aplicacao.produto.ProdutoResumo;

public class EstoqueServicoAplicacao {
	private EstoqueRepositorioAplicacao repositorio;

	public EstoqueServicoAplicacao(EstoqueRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<EstoqueResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public List<EstoqueResumo> pesquisarPorNomeOuEndereco(String termo) {
		if (termo == null || termo.isBlank()) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorNomeOuEndereco(termo);
	}

	public List<EstoqueResumo> pesquisarPorStatus(String status) {
		if (status == null || status.isBlank() || status.equalsIgnoreCase("todos")) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorStatus(status);
	}

	public EstoqueResumo obterResumo(int id) {
		return repositorio.obterResumo(id);
	}

	public List<ProdutoResumo> pesquisarProdutos(int estoqueId) {
		return repositorio.pesquisarProdutos(estoqueId);
	}
}

