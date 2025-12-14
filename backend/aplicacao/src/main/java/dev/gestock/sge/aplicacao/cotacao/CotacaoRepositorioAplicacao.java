package dev.gestock.sge.aplicacao.cotacao;

import java.util.List;

public interface CotacaoRepositorioAplicacao {
	List<CotacaoPorProdutoResumo> pesquisarPorProduto();

	List<CotacaoResumo> pesquisarPorProduto(int produtoId);

	CotacaoResumo obterResumo(int id);

	int sincronizarCotacoes();
}

