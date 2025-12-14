package dev.gestock.sge.aplicacao.transferencia;

import java.util.List;

public interface TransferenciaRepositorioAplicacao {
	List<TransferenciaResumo> pesquisarResumos(String busca);

	TransferenciaTotais obterTotais();

	/**
	 * Obtém a quantidade atual de um produto em um estoque específico.
	 */
	int obterQuantidadeProdutoNoEstoque(int estoqueId, int produtoId);

	/**
	 * Obtém a capacidade disponível de um estoque.
	 */
	int obterCapacidadeDisponivel(int estoqueId);
}

