package dev.gestock.sge.dominio.pedido;

import static org.apache.commons.lang3.Validate.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.LeadTime;

public class Pedido {
	private final PedidoId id;
	private final FornecedorId fornecedorId;
	private final EstoqueId estoqueId;
	private final List<ItemPedido> itens;
	ValorTotal valorTotal; // package-private para acesso do mapeador
	private final DataPedido dataPedido;
	DataPrevista dataPrevista; // package-private para acesso do mapeador
	private StatusPedido status;

	public Pedido(PedidoId id, FornecedorId fornecedorId, EstoqueId estoqueId, List<ItemPedido> itens, DataPedido dataPedido,
			StatusPedido status) {
		notNull(id, "O id não pode ser nulo");
		notNull(fornecedorId, "O id do fornecedor não pode ser nulo");
		notNull(estoqueId, "O id do estoque não pode ser nulo");
		notNull(itens, "A lista de itens não pode ser nula");
		notEmpty(itens, "O pedido deve ter pelo menos um item");
		notNull(dataPedido, "A data do pedido não pode ser nula");
		notNull(status, "O status não pode ser nulo");

		this.id = id;
		this.fornecedorId = fornecedorId;
		this.estoqueId = estoqueId;
		this.itens = new ArrayList<>(itens);
		this.dataPedido = dataPedido;
		this.status = status;
		this.valorTotal = calcularValorTotal();
	}

	public PedidoId getId() {
		return id;
	}

	public FornecedorId getFornecedorId() {
		return fornecedorId;
	}

	public EstoqueId getEstoqueId() {
		return estoqueId;
	}

	public Collection<ItemPedido> getItens() {
		var copia = new ArrayList<ItemPedido>();
		copia.addAll(itens);
		return copia;
	}

	public ValorTotal getValorTotal() {
		return valorTotal;
	}

	public DataPedido getDataPedido() {
		return dataPedido;
	}

	public DataPrevista getDataPrevista() {
		return dataPrevista;
	}

	public StatusPedido getStatus() {
		return status;
	}

	/**
	 * Calcula o valor total do pedido somando (preço unitário * quantidade) de cada item.
	 */
	public ValorTotal calcularValorTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (var item : itens) {
			var subtotal = item.getPrecoUnitario().getValor()
					.multiply(BigDecimal.valueOf(item.getQuantidade().getValor()));
			total = total.add(subtotal);
		}
		this.valorTotal = new ValorTotal(total);
		return this.valorTotal;
	}

	/**
	 * Calcula a data prevista de entrega baseada no lead time do fornecedor.
	 */
	public void calcularDataPrevista(LeadTime leadTime) {
		notNull(leadTime, "O lead time não pode ser nulo");
		var dataPrevista = dataPedido.getValor().plusDays(leadTime.getDias());
		this.dataPrevista = new DataPrevista(dataPrevista);
	}

	/**
	 * Confirma o recebimento do pedido, alterando o status para RECEBIDO.
	 */
	public PedidoRecebidoEvento confirmarRecebimento() {
		if (status == StatusPedido.CANCELADO) {
			throw new IllegalStateException("Não é possível confirmar recebimento de um pedido cancelado");
		}
		this.status = StatusPedido.RECEBIDO;
		return new PedidoRecebidoEvento(this);
	}

	/**
	 * Cancela o pedido, alterando o status para CANCELADO.
	 */
	public PedidoCanceladoEvento cancelar() {
		if (status == StatusPedido.RECEBIDO) {
			throw new IllegalStateException("Não é possível cancelar um pedido já recebido");
		}
		this.status = StatusPedido.CANCELADO;
		return new PedidoCanceladoEvento(this);
	}

	/**
	 * Cria um evento de pedido criado.
	 */
	public PedidoCriadoEvento criarEvento() {
		return new PedidoCriadoEvento(this);
	}

	// Classe base para eventos do Pedido
	public static abstract class PedidoEvento {
		private final Pedido pedido;

		PedidoEvento(Pedido pedido) {
			this.pedido = pedido;
		}

		public Pedido getPedido() {
			return pedido;
		}
	}

	// Evento específico: pedido criado
	public static class PedidoCriadoEvento extends PedidoEvento {
		private PedidoCriadoEvento(Pedido pedido) {
			super(pedido);
		}
	}

	// Evento específico: pedido cancelado
	public static class PedidoCanceladoEvento extends PedidoEvento {
		private PedidoCanceladoEvento(Pedido pedido) {
			super(pedido);
		}
	}

	// Evento específico: pedido recebido
	public static class PedidoRecebidoEvento extends PedidoEvento {
		private PedidoRecebidoEvento(Pedido pedido) {
			super(pedido);
		}
	}

	/**
	 * Altera o status do pedido.
	 */
	public void alterarStatus(StatusPedido novoStatus) {
		notNull(novoStatus, "O novo status não pode ser nulo");
		
		if (status == StatusPedido.CANCELADO || status == StatusPedido.RECEBIDO) {
			throw new IllegalStateException("Não é possível alterar o status de um pedido cancelado ou recebido");
		}
		
		this.status = novoStatus;
	}
}

