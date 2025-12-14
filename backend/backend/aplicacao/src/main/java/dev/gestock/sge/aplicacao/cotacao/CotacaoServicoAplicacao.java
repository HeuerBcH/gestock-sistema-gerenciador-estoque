package dev.gestock.sge.aplicacao.cotacao;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;
import dev.gestock.sge.dominio.cotacao.CotacaoServico;

public class CotacaoServicoAplicacao {
	private CotacaoRepositorioAplicacao repositorio;
	private CotacaoServico cotacaoServico;

	public CotacaoServicoAplicacao(CotacaoRepositorioAplicacao repositorio, CotacaoServico cotacaoServico) {
		notNull(repositorio, "O repositório não pode ser nulo");
		notNull(cotacaoServico, "O serviço de cotação não pode ser nulo");
		this.repositorio = repositorio;
		this.cotacaoServico = cotacaoServico;
	}

	public List<CotacaoPorProdutoResumo> pesquisarPorProduto() {
		// A lógica de identificar a mais vantajosa já está implementada no repositório
		return repositorio.pesquisarPorProduto();
	}

	public List<CotacaoResumo> pesquisarPorProduto(int produtoId) {
		return repositorio.pesquisarPorProduto(produtoId);
	}

	public CotacaoResumo obterResumo(int id) {
		return repositorio.obterResumo(id);
	}
	
	public int sincronizarCotacoes() {
		return repositorio.sincronizarCotacoes();
	}
}

