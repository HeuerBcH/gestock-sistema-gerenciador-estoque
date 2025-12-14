package dev.gestock.sge.dominio.produto;

import dev.gestock.sge.dominio.comum.RegraVioladaException;

/**
 * Implementação concreta do Template Method para inativação de produto.
 * 
 * Pattern: Template Method
 * Funcionalidade: Gerenciar Produtos (RONALDO)
 */
public class ProdutoInativacaoOperacao extends ProdutoOperacaoTemplate {

	public ProdutoInativacaoOperacao(ProdutoRepositorio repositorio) {
		super(repositorio);
	}

	@Override
	protected void validarRegrasNegocio(Produto produto) {
		// R1H10: Um produto não pode ser inativado se houver saldo positivo em qualquer estoque
		if (repositorio.possuiSaldoEmEstoque(produto.getId().getId())) {
			throw new RegraVioladaException("R1H10", 
				"Não é possível inativar o produto pois ele possui saldo em estoque");
		}
		
		// R1H10: Um produto não pode ser inativado se houver pedidos em andamento associados
		if (repositorio.possuiPedidosEmAndamento(produto.getId().getId())) {
			throw new RegraVioladaException("R1H10", 
				"Não é possível inativar o produto pois existem pedidos em andamento");
		}
	}

	@Override
	protected void executarOperacao(Produto produto) {
		produto.inativar();
	}
}

