package dev.gestock.sge.aplicacao.pontoresuprimento;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;

public class PontoRessuprimentoServicoAplicacao {
	private PontoRessuprimentoRepositorioAplicacao repositorio;

	public PontoRessuprimentoServicoAplicacao(PontoRessuprimentoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<PontoRessuprimentoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public List<PontoRessuprimentoResumo> pesquisarPorProdutoOuEstoque(String termo) {
		if (termo == null || termo.isBlank()) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorProdutoOuEstoque(termo);
	}

	public List<PontoRessuprimentoResumo> pesquisarPorStatus(String status) {
		if (status == null || status.isBlank()) {
			return pesquisarResumos();
		}
		return repositorio.pesquisarPorStatus(status);
	}

	public PontoRessuprimentoResumo obterResumo(int id) {
		return repositorio.obterResumo(id);
	}

	public PontoRessuprimentoTotais obterTotais() {
		return repositorio.obterTotais();
	}

	public int sincronizarPontosRessuprimento() {
		return repositorio.sincronizarPontosRessuprimento();
	}
}

