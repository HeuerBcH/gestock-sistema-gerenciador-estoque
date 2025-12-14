package dev.gestock.sge.dominio.cotacao;

import java.util.List;
import dev.gestock.sge.dominio.produto.ProdutoId;

public interface CotacaoRepositorio {
	Cotacao salvar(Cotacao cotacao);

	Cotacao obter(CotacaoId id);

	List<Cotacao> obterPorProduto(ProdutoId produtoId);

	void remover(CotacaoId id);
}

