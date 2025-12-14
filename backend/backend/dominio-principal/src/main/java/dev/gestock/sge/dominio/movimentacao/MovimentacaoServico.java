package dev.gestock.sge.dominio.movimentacao;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;

public class MovimentacaoServico {
	private final MovimentacaoRepositorio repositorio;
	private final ProdutoRepositorio produtoRepositorio;
	private final EstoqueRepositorio estoqueRepositorio;
	private final EventoBarramento barramento;

	public MovimentacaoServico(MovimentacaoRepositorio repositorio, ProdutoRepositorio produtoRepositorio,
			EstoqueRepositorio estoqueRepositorio, EventoBarramento barramento) {
		notNull(repositorio, "O repositório não pode ser nulo");
		notNull(produtoRepositorio, "O repositório de produtos não pode ser nulo");
		notNull(estoqueRepositorio, "O repositório de estoques não pode ser nulo");
		notNull(barramento, "O barramento de eventos não pode ser nulo");

		this.repositorio = repositorio;
		this.produtoRepositorio = produtoRepositorio;
		this.estoqueRepositorio = estoqueRepositorio;
		this.barramento = barramento;
	}

	public Movimentacao registrar(Movimentacao movimentacao) {
		notNull(movimentacao, "A movimentação não pode ser nula");

		// Validar que produto existe
		var produto = produtoRepositorio.obter(movimentacao.getProdutoId());
		if (produto == null) {
			throw new IllegalArgumentException("Produto não encontrado");
		}

		// Validar que estoque existe
		var estoque = estoqueRepositorio.obter(movimentacao.getEstoqueId());
		if (estoque == null) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}

		// Validações específicas por tipo
		if (movimentacao.getTipo() == TipoMovimentacao.SAIDA) {
			// Validar quantidade disponível será feito na implementação do repositório
			// que tem acesso direto à tabela ESTOQUE_PRODUTO
		}

		var movimentacaoSalva = repositorio.salvar(movimentacao);
		
		// Publicar evento de movimentação criada
		var evento = movimentacaoSalva.criarEvento();
		barramento.postar(evento);
		
		return movimentacaoSalva;
	}

	public void remover(MovimentacaoId id) {
		notNull(id, "O id não pode ser nulo");
		repositorio.remover(id);
	}
}

