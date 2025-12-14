package dev.gestock.sge.aplicacao.cotacao;

import java.util.List;

public interface CotacaoPorProdutoResumo {
	int getProdutoId();

	String getProdutoNome();

	List<CotacaoResumo> getCotacoes();

	int getTotalCotacoes();

	int getTotalAprovadas();
}

