package dev.gestock.sge.dominio.produto;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.comum.RegraVioladaException;

public class ProdutoServico {
	private final ProdutoRepositorio repositorio;

	public ProdutoServico(ProdutoRepositorio repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public Produto salvar(Produto produto) {
		notNull(produto, "O produto não pode ser nulo");

		int produtoId = produto.getId().getId();

		// R1H8: Cada produto deve possuir um código único dentro do catálogo do cliente
		if (repositorio.existePorCodigo(produto.getCodigo().getValor(), produtoId)) {
			throw new RegraVioladaException("R1H8", 
				"Já existe um produto cadastrado com este código: " + produto.getCodigo().getValor());
		}

		return repositorio.salvar(produto);
	}

	public void remover(ProdutoId id) {
		notNull(id, "O id não pode ser nulo");
		repositorio.remover(id);
	}

	public void ativar(ProdutoId id) {
		notNull(id, "O id não pode ser nulo");
		var produto = repositorio.obter(id);
		if (produto == null) {
			throw new IllegalArgumentException("Produto não encontrado");
		}
		
		// R3H8: Todo produto deve estar vinculado a pelo menos um estoque ativo
		if (!repositorio.possuiEstoqueAtivo(id.getId())) {
			throw new RegraVioladaException("R3H8", 
				"O produto deve estar vinculado a pelo menos um estoque ativo para ser ativado");
		}
		
		produto.ativar();
		repositorio.salvar(produto);
	}

	public void inativar(ProdutoId id) {
		notNull(id, "O id não pode ser nulo");
		var produto = repositorio.obter(id);
		if (produto == null) {
			throw new IllegalArgumentException("Produto não encontrado");
		}
		
		// R1H10: Um produto não pode ser inativado se houver saldo positivo em qualquer estoque
		if (repositorio.possuiSaldoEmEstoque(id.getId())) {
			throw new RegraVioladaException("R1H10", 
				"Não é possível inativar o produto pois ele possui saldo em estoque");
		}
		
		// R1H10: Um produto não pode ser inativado se houver pedidos em andamento associados
		if (repositorio.possuiPedidosEmAndamento(id.getId())) {
			throw new RegraVioladaException("R1H10", 
				"Não é possível inativar o produto pois existem pedidos em andamento");
		}
		
		produto.inativar();
		repositorio.salvar(produto);
	}
}

