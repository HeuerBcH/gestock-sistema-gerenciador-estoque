package dev.gestock.sge.dominio.estoque;

public interface EstoqueRepositorio {
	Estoque salvar(Estoque estoque);

	Estoque obter(EstoqueId id);

	void remover(EstoqueId id);

	/**
	 * Verifica se existe outro estoque com o mesmo endereço.
	 * @param endereco Endereço a verificar
	 * @param excludeId ID do estoque a excluir da verificação (para edição)
	 * @return true se existir outro estoque com o mesmo endereço
	 */
	boolean existePorEndereco(String endereco, int excludeId);

	/**
	 * Verifica se existe outro estoque com o mesmo nome.
	 * @param nome Nome a verificar
	 * @param excludeId ID do estoque a excluir da verificação (para edição)
	 * @return true se existir outro estoque com o mesmo nome
	 */
	boolean existePorNome(String nome, int excludeId);

	/**
	 * Verifica se o estoque possui produtos (quantidade > 0).
	 * @param estoqueId ID do estoque
	 * @return true se o estoque possuir produtos
	 */
	boolean possuiProdutos(int estoqueId);

	/**
	 * Verifica se o estoque possui pedidos em andamento.
	 * @param estoqueId ID do estoque
	 * @return true se o estoque possuir pedidos em andamento
	 */
	boolean possuiPedidosEmAndamento(int estoqueId);

	/**
	 * Obtém a ocupação atual do estoque (soma das quantidades de produtos).
	 * @param estoqueId ID do estoque
	 * @return Quantidade total de produtos no estoque
	 */
	int obterOcupacaoAtual(int estoqueId);
}

