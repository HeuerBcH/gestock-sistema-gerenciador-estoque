package dev.gestock.sge.dominio.cotacao;

import static org.apache.commons.lang3.Validate.*;
import java.util.Comparator;
import java.util.List;
import dev.gestock.sge.dominio.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;

public class CotacaoServico {
	private final CotacaoRepositorio repositorio;
	private final ProdutoRepositorio produtoRepositorio;
	private final FornecedorRepositorio fornecedorRepositorio;
	private CotacaoSelecaoStrategy estrategiaSelecao;

	public CotacaoServico(CotacaoRepositorio repositorio, ProdutoRepositorio produtoRepositorio,
			FornecedorRepositorio fornecedorRepositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		notNull(produtoRepositorio, "O repositório de produtos não pode ser nulo");
		notNull(fornecedorRepositorio, "O repositório de fornecedores não pode ser nulo");

		this.repositorio = repositorio;
		this.produtoRepositorio = produtoRepositorio;
		this.fornecedorRepositorio = fornecedorRepositorio;
		// Estratégia padrão: seleção completa
		this.estrategiaSelecao = new CotacaoSelecaoCompletaStrategy();
	}

	/**
	 * Define a estratégia de seleção de cotação.
	 * 
	 * Pattern: Strategy
	 * Funcionalidade: Selecionar Cotação Mais Vantajosa (DUDU)
	 */
	public void definirEstrategiaSelecao(CotacaoSelecaoStrategy estrategia) {
		notNull(estrategia, "A estratégia não pode ser nula");
		this.estrategiaSelecao = estrategia;
	}

	public Cotacao salvar(Cotacao cotacao) {
		notNull(cotacao, "A cotação não pode ser nula");

		// Validar que produto existe
		var produto = produtoRepositorio.obter(cotacao.getProdutoId());
		if (produto == null) {
			throw new IllegalArgumentException("Produto não encontrado");
		}

		// Validar que fornecedor existe
		var fornecedor = fornecedorRepositorio.obter(cotacao.getFornecedorId());
		if (fornecedor == null) {
			throw new IllegalArgumentException("Fornecedor não encontrado");
		}

		return repositorio.salvar(cotacao);
	}

	public void remover(CotacaoId id) {
		notNull(id, "O id não pode ser nulo");
		repositorio.remover(id);
	}

	public void aprovar(CotacaoId id) {
		notNull(id, "O id não pode ser nulo");
		var cotacao = repositorio.obter(id);
		if (cotacao == null) {
			throw new IllegalArgumentException("Cotação não encontrada");
		}
		cotacao.aprovar();
		repositorio.salvar(cotacao);
	}

	public void desaprovar(CotacaoId id) {
		notNull(id, "O id não pode ser nulo");
		var cotacao = repositorio.obter(id);
		if (cotacao == null) {
			throw new IllegalArgumentException("Cotação não encontrada");
		}
		cotacao.desaprovar();
		repositorio.salvar(cotacao);
	}

	/**
	 * Obtém a cotação mais vantajosa usando a estratégia configurada.
	 * 
	 * Pattern: Strategy
	 * Funcionalidade: Selecionar Cotação Mais Vantajosa (DUDU)
	 */
	public Cotacao obterMaisVantajosa(ProdutoId produtoId) {
		notNull(produtoId, "O id do produto não pode ser nulo");
		var cotacoes = repositorio.obterPorProduto(produtoId);
		
		if (cotacoes.isEmpty()) {
			return null;
		}

		// Usar a estratégia configurada para selecionar a melhor cotação
		return estrategiaSelecao.selecionar(cotacoes);
	}

	/**
	 * Obtém a cotação mais vantajosa usando uma estratégia específica.
	 * 
	 * Pattern: Strategy
	 * Funcionalidade: Selecionar Cotação Mais Vantajosa (DUDU)
	 */
	public Cotacao obterMaisVantajosa(ProdutoId produtoId, CotacaoSelecaoStrategy estrategia) {
		notNull(produtoId, "O id do produto não pode ser nulo");
		notNull(estrategia, "A estratégia não pode ser nula");
		var cotacoes = repositorio.obterPorProduto(produtoId);
		
		if (cotacoes.isEmpty()) {
			return null;
		}

		return estrategia.selecionar(cotacoes);
	}
}

