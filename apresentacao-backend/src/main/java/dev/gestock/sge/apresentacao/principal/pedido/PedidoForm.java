package dev.gestock.sge.apresentacao.principal.pedido;

import java.time.LocalDate;
import java.util.List;

public class PedidoForm {
	public PedidoDto pedido;

	public PedidoForm(PedidoDto pedido) {
		this.pedido = pedido;
	}

	public static class PedidoDto {
		public Long id;
		public Long clienteId;
		public Long fornecedorId;
		public Long estoqueId;
		public LocalDate dataCriacao;
		public LocalDate dataPrevistaEntrega;
		public String status;
		public List<ItemPedidoDto> itens;
	}

	public static class ItemPedidoDto {
		public Long produtoId;
		public int quantidade;
		public double precoUnitario;
	}

	public static class GerarPedidoDto {
		public Long clienteId;
		public Long fornecedorId;
		public Long produtoId;
		public int quantidade;
		public Long estoqueId; // Opcional - se fornecido, reserva estoque
	}
}
