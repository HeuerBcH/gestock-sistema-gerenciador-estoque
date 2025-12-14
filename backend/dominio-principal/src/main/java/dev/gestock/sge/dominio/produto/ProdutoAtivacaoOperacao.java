package dev.gestock.sge.dominio.produto;

import dev.gestock.sge.dominio.comum.RegraVioladaException;

/**
 * Implementação concreta do Template Method para ativação de produto.
 * 
 * Pattern: Template Method
 * Funcionalidade: Gerenciar Produtos (RONALDO)
 */
public class ProdutoAtivacaoOperacao extends ProdutoOperacaoTemplate {

	public ProdutoAtivacaoOperacao(ProdutoRepositorio repositorio) {
		super(repositorio);
	}

	@Override
	protected void validarRegrasNegocio(Produto produto) {
		// R3H8: Todo produto deve estar vinculado a pelo menos um estoque ativo
		if (!repositorio.possuiEstoqueAtivo(produto.getId().getId())) {
			throw new RegraVioladaException("R3H8", 
				"O produto deve estar vinculado a pelo menos um estoque ativo para ser ativado");
		}
	}

	@Override
	protected void executarOperacao(Produto produto) {
		produto.ativar();
	}
}

