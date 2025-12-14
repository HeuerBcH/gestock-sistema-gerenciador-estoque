package dev.gestock.sge.dominio.transferencia;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoId;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.Quantidade;
import dev.gestock.sge.dominio.movimentacao.Motivo;
import dev.gestock.sge.dominio.movimentacao.Responsavel;

public class Transferencia {
	private final TransferenciaId id;
	private final ProdutoId produtoId;
	private final Quantidade quantidade;
	private final EstoqueId estoqueOrigem;
	private final EstoqueId estoqueDestino;
	private final DataHoraTransferencia dataHora;
	private final Responsavel responsavel;
	private final Motivo motivo;
	private final MovimentacaoId movimentacaoSaidaId;
	private final MovimentacaoId movimentacaoEntradaId;

	public Transferencia(TransferenciaId id, ProdutoId produtoId, Quantidade quantidade, EstoqueId estoqueOrigem,
			EstoqueId estoqueDestino, DataHoraTransferencia dataHora, Responsavel responsavel, Motivo motivo,
			MovimentacaoId movimentacaoSaidaId, MovimentacaoId movimentacaoEntradaId) {
		notNull(id, "O id não pode ser nulo");
		notNull(produtoId, "O id do produto não pode ser nulo");
		notNull(quantidade, "A quantidade não pode ser nula");
		notNull(estoqueOrigem, "O id do estoque de origem não pode ser nulo");
		notNull(estoqueDestino, "O id do estoque de destino não pode ser nulo");
		isTrue(!estoqueOrigem.equals(estoqueDestino), "O estoque de origem deve ser diferente do estoque de destino");
		notNull(dataHora, "A data/hora não pode ser nula");
		notNull(responsavel, "O responsável não pode ser nulo");
		notNull(motivo, "O motivo não pode ser nulo");
		notNull(movimentacaoSaidaId, "O id da movimentação de saída não pode ser nulo");
		notNull(movimentacaoEntradaId, "O id da movimentação de entrada não pode ser nulo");

		this.id = id;
		this.produtoId = produtoId;
		this.quantidade = quantidade;
		this.estoqueOrigem = estoqueOrigem;
		this.estoqueDestino = estoqueDestino;
		this.dataHora = dataHora;
		this.responsavel = responsavel;
		this.motivo = motivo;
		this.movimentacaoSaidaId = movimentacaoSaidaId;
		this.movimentacaoEntradaId = movimentacaoEntradaId;
	}

	public TransferenciaId getId() {
		return id;
	}

	public ProdutoId getProdutoId() {
		return produtoId;
	}

	public Quantidade getQuantidade() {
		return quantidade;
	}

	public EstoqueId getEstoqueOrigem() {
		return estoqueOrigem;
	}

	public EstoqueId getEstoqueDestino() {
		return estoqueDestino;
	}

	public DataHoraTransferencia getDataHora() {
		return dataHora;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public Motivo getMotivo() {
		return motivo;
	}

	public MovimentacaoId getMovimentacaoSaidaId() {
		return movimentacaoSaidaId;
	}

	public MovimentacaoId getMovimentacaoEntradaId() {
		return movimentacaoEntradaId;
	}
}

