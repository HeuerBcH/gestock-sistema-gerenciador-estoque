package dev.gestock.sge.dominio.pontoresuprimento;

import java.util.List;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.produto.ProdutoId;

public interface PontoRessuprimentoRepositorio {
	PontoRessuprimento salvar(PontoRessuprimento ponto);

	PontoRessuprimento obter(PontoRessuprimentoId id);

	PontoRessuprimento obterPorEstoqueEProduto(EstoqueId estoqueId, ProdutoId produtoId);

	List<PontoRessuprimento> obterTodos();

	void remover(PontoRessuprimentoId id);
}

