package dev.gestock.sge.aplicacao.estoque;

import java.util.List;
import dev.gestock.sge.aplicacao.produto.ProdutoResumo;

public interface EstoqueRepositorioAplicacao {
	List<EstoqueResumo> pesquisarResumos();

	List<EstoqueResumo> pesquisarPorNomeOuEndereco(String termo);

	List<EstoqueResumo> pesquisarPorStatus(String status);

	EstoqueResumo obterResumo(int id);

	List<ProdutoResumo> pesquisarProdutos(int estoqueId);
}

