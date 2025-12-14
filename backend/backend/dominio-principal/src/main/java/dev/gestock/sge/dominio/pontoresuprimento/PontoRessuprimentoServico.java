package dev.gestock.sge.dominio.pontoresuprimento;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;

public class PontoRessuprimentoServico {
	private final PontoRessuprimentoRepositorio repositorio;
	private final ProdutoRepositorio produtoRepositorio;
	private final EstoqueRepositorio estoqueRepositorio;

	public PontoRessuprimentoServico(PontoRessuprimentoRepositorio repositorio,
			ProdutoRepositorio produtoRepositorio, EstoqueRepositorio estoqueRepositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		notNull(produtoRepositorio, "O repositório de produtos não pode ser nulo");
		notNull(estoqueRepositorio, "O repositório de estoques não pode ser nulo");

		this.repositorio = repositorio;
		this.produtoRepositorio = produtoRepositorio;
		this.estoqueRepositorio = estoqueRepositorio;
	}

	public PontoRessuprimento registrar(PontoRessuprimento ponto) {
		notNull(ponto, "O ponto de ressuprimento não pode ser nulo");

		// Validar que produto existe
		var produto = produtoRepositorio.obter(ponto.getProdutoId());
		if (produto == null) {
			throw new IllegalArgumentException("Produto não encontrado");
		}

		// Validar que estoque existe
		var estoque = estoqueRepositorio.obter(ponto.getEstoqueId());
		if (estoque == null) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}

		// Validar que não existe duplicata
		var existente = repositorio.obterPorEstoqueEProduto(ponto.getEstoqueId(), ponto.getProdutoId());
		if (existente != null && !existente.getId().equals(ponto.getId())) {
			throw new IllegalArgumentException("Já existe um ponto de ressuprimento para este estoque e produto");
		}

		return repositorio.salvar(ponto);
	}

	public void atualizarEstoqueSeguranca(PontoRessuprimentoId id, EstoqueSeguranca estoqueSeguranca) {
		notNull(id, "O id não pode ser nulo");
		notNull(estoqueSeguranca, "O estoque de segurança não pode ser nulo");

		var ponto = repositorio.obter(id);
		if (ponto == null) {
			throw new IllegalArgumentException("Ponto de ressuprimento não encontrado");
		}

		ponto.setEstoqueSeguranca(estoqueSeguranca);
		repositorio.salvar(ponto);
	}

	public void remover(PontoRessuprimentoId id) {
		notNull(id, "O id não pode ser nulo");
		repositorio.remover(id);
	}
}

