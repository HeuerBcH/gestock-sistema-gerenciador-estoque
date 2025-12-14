package dev.gestock.sge.aplicacao.pontoresuprimento;

import java.util.List;

public interface PontoRessuprimentoRepositorioAplicacao {
	List<PontoRessuprimentoResumo> pesquisarResumos();

	List<PontoRessuprimentoResumo> pesquisarPorProdutoOuEstoque(String termo);

	List<PontoRessuprimentoResumo> pesquisarPorStatus(String status);

	PontoRessuprimentoResumo obterResumo(int id);

	PontoRessuprimentoTotais obterTotais();

	double calcularConsumoMedioDiario(int estoqueId, int produtoId, int dias);

	double calcularConsumoMaximoDiario(int estoqueId, int produtoId, int dias);

	int obterSaldoAtual(int estoqueId, int produtoId);

	int obterLeadTimeMedio(int produtoId);

	int obterLeadTimeMaximo(int produtoId);

	/**
	 * Sincroniza automaticamente os pontos de ressuprimento.
	 * Cria entradas para todas as combinações de estoque-produto que possuem
	 * quantidade > 0 na tabela ESTOQUE_PRODUTO.
	 * Fórmula: Estoque de Segurança = (Consumo Máximo × Lead Time Máximo) - (Consumo Médio × Lead Time Médio)
	 * @return Número de registros criados/atualizados
	 */
	int sincronizarPontosRessuprimento();
}

