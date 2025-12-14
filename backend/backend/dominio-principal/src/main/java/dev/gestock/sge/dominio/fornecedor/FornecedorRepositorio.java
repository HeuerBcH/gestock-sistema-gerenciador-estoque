package dev.gestock.sge.dominio.fornecedor;

public interface FornecedorRepositorio {
	Fornecedor salvar(Fornecedor fornecedor);

	Fornecedor obter(FornecedorId id);

	Fornecedor obterPorCnpj(Cnpj cnpj);

	void remover(FornecedorId id);

	/**
	 * R1H7: Verifica se o fornecedor possui pedidos pendentes.
	 * @param fornecedorId ID do fornecedor
	 * @return true se possuir pedidos pendentes
	 */
	boolean possuiPedidosPendentes(int fornecedorId);
}

