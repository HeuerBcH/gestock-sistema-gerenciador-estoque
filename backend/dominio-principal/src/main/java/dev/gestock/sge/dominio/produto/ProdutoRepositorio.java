package dev.gestock.sge.dominio.produto;

public interface ProdutoRepositorio {
	Produto salvar(Produto produto);

	Produto obter(ProdutoId id);

	Produto obterPorCodigo(Codigo codigo);

	void remover(ProdutoId id);

	/**
	 * R1H8: Verifica se existe produto com o mesmo código (exceto o próprio).
	 * @param codigo Código a verificar
	 * @param excludeId ID do produto a excluir da verificação
	 * @return true se existir outro produto com o mesmo código
	 */
	boolean existePorCodigo(String codigo, int excludeId);

	/**
	 * R3H8: Verifica se o produto está vinculado a pelo menos um estoque ativo.
	 * @param produtoId ID do produto
	 * @return true se o produto está vinculado a algum estoque ativo
	 */
	boolean possuiEstoqueAtivo(int produtoId);

	/**
	 * R1H10: Verifica se o produto possui saldo positivo em algum estoque.
	 * @param produtoId ID do produto
	 * @return true se o produto possuir saldo em estoque
	 */
	boolean possuiSaldoEmEstoque(int produtoId);

	/**
	 * R1H10: Verifica se o produto possui pedidos em andamento.
	 * @param produtoId ID do produto
	 * @return true se o produto possuir pedidos em andamento
	 */
	boolean possuiPedidosEmAndamento(int produtoId);
}

