package dev.gestock.sge.dominio.movimentacao;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDateTime;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.Quantidade;

public class Movimentacao {
	private final MovimentacaoId id;
	private final LocalDateTime dataHora;
	private final ProdutoId produtoId;
	private final EstoqueId estoqueId;
	private final Quantidade quantidade;
	private final TipoMovimentacao tipo;
	private final Motivo motivo;
	private final Responsavel responsavel;

	public Movimentacao(MovimentacaoId id, LocalDateTime dataHora, ProdutoId produtoId, EstoqueId estoqueId,
			Quantidade quantidade, TipoMovimentacao tipo, Motivo motivo, Responsavel responsavel) {
		notNull(id, "O id não pode ser nulo");
		notNull(dataHora, "A data/hora não pode ser nula");
		notNull(produtoId, "O id do produto não pode ser nulo");
		notNull(estoqueId, "O id do estoque não pode ser nulo");
		notNull(quantidade, "A quantidade não pode ser nula");
		notNull(tipo, "O tipo não pode ser nulo");
		notNull(motivo, "O motivo não pode ser nulo");
		notNull(responsavel, "O responsável não pode ser nulo");

		this.id = id;
		this.dataHora = dataHora;
		this.produtoId = produtoId;
		this.estoqueId = estoqueId;
		this.quantidade = quantidade;
		this.tipo = tipo;
		this.motivo = motivo;
		this.responsavel = responsavel;
	}

	public MovimentacaoId getId() {
		return id;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public ProdutoId getProdutoId() {
		return produtoId;
	}

	public EstoqueId getEstoqueId() {
		return estoqueId;
	}

	public Quantidade getQuantidade() {
		return quantidade;
	}

	public TipoMovimentacao getTipo() {
		return tipo;
	}

	public Motivo getMotivo() {
		return motivo;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	/**
	 * Retorna a quantidade com sinal: positiva para ENTRADA, negativa para SAIDA.
	 */
	public int getQuantidadeComSinal() {
		return tipo == TipoMovimentacao.ENTRADA 
			? quantidade.getValor() 
			: -quantidade.getValor();
	}

	/**
	 * Cria um evento de movimentação criada.
	 */
	public MovimentacaoCriadaEvento criarEvento() {
		return new MovimentacaoCriadaEvento(this);
	}

	// Classe base para eventos do Movimentacao
	public static abstract class MovimentacaoEvento {
		private final Movimentacao movimentacao;

		MovimentacaoEvento(Movimentacao movimentacao) {
			this.movimentacao = movimentacao;
		}

		public Movimentacao getMovimentacao() {
			return movimentacao;
		}
	}

	// Evento específico: movimentação criada
	public static class MovimentacaoCriadaEvento extends MovimentacaoEvento {
		private MovimentacaoCriadaEvento(Movimentacao movimentacao) {
			super(movimentacao);
		}
	}
}

