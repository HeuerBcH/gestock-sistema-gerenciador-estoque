package dev.gestock.sge.dominio.reserva;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.pedido.PedidoId;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.Quantidade;

public class Reserva {
	private final ReservaId id;
	private final PedidoId pedidoId;
	private final ProdutoId produtoId;
	private final Quantidade quantidade;
	private final DataHoraReserva dataHoraReserva;
	private StatusReserva status;
	private TipoLiberacao tipoLiberacao;
	private DataHoraLiberacao dataHoraLiberacao;

	public Reserva(ReservaId id, PedidoId pedidoId, ProdutoId produtoId, Quantidade quantidade,
			DataHoraReserva dataHoraReserva, StatusReserva status) {
		notNull(id, "O id não pode ser nulo");
		notNull(pedidoId, "O id do pedido não pode ser nulo");
		notNull(produtoId, "O id do produto não pode ser nulo");
		notNull(quantidade, "A quantidade não pode ser nula");
		notNull(dataHoraReserva, "A data/hora da reserva não pode ser nula");
		notNull(status, "O status não pode ser nulo");

		this.id = id;
		this.pedidoId = pedidoId;
		this.produtoId = produtoId;
		this.quantidade = quantidade;
		this.dataHoraReserva = dataHoraReserva;
		this.status = status;
	}

	public ReservaId getId() {
		return id;
	}

	public PedidoId getPedidoId() {
		return pedidoId;
	}

	public ProdutoId getProdutoId() {
		return produtoId;
	}

	public Quantidade getQuantidade() {
		return quantidade;
	}

	public DataHoraReserva getDataHoraReserva() {
		return dataHoraReserva;
	}

	public StatusReserva getStatus() {
		return status;
	}

	public TipoLiberacao getTipoLiberacao() {
		return tipoLiberacao;
	}

	public DataHoraLiberacao getDataHoraLiberacao() {
		return dataHoraLiberacao;
	}

	/**
	 * Libera a reserva, alterando o status para LIBERADA e registrando o tipo de
	 * liberação e a data/hora.
	 */
	public void liberar(TipoLiberacao tipo) {
		notNull(tipo, "O tipo de liberação não pode ser nulo");
		if (status == StatusReserva.LIBERADA) {
			throw new IllegalStateException("A reserva já está liberada");
		}
		this.status = StatusReserva.LIBERADA;
		this.tipoLiberacao = tipo;
		this.dataHoraLiberacao = new DataHoraLiberacao(java.time.LocalDateTime.now());
	}
}

