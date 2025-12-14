package dev.gestock.sge.aplicacao.produto;

import java.util.List;

public interface ProdutoRepositorioAplicacao {
	List<ProdutoResumo> pesquisarResumos();

	List<ProdutoResumo> pesquisarPorNomeOuCodigo(String termo);

	List<ProdutoResumo> pesquisarPorStatus(String status);

	ProdutoResumo obterResumo(int id);
}

