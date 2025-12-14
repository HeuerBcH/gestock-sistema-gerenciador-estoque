package dev.gestock.sge.aplicacao.fornecedor;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;

public class FornecedorServicoAplicacao {
	private FornecedorRepositorioAplicacao repositorio;

	public FornecedorServicoAplicacao(FornecedorRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<FornecedorResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public List<FornecedorResumo> pesquisarPorNomeOuCnpj(String termo) {
		if (termo == null || termo.isBlank()) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorNomeOuCnpj(termo);
	}

	public List<FornecedorResumo> pesquisarPorStatus(String status) {
		if (status == null || status.isBlank() || status.equalsIgnoreCase("todos")) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorStatus(status);
	}

	public FornecedorResumo obterResumo(int id) {
		return repositorio.obterResumo(id);
	}
}

