package dev.gestock.sge.apresentacao.pedido;

import java.time.LocalDate;
import java.util.List;

public class PedidoFormulario {
	public static class PedidoDto {
		public Integer id;
		public Integer fornecedorId;
		public Integer estoqueId;
		public List<ItemPedidoDto> itens;
		public LocalDate dataPedido;
		public String status;
	}

	public static class ItemPedidoDto {
		public Integer produtoId;
		public Integer quantidade;
	}

	public static class PedidoAutomaticoDto {
		public Integer estoqueId;
		public List<ItemPedidoDto> itens;
	}
}

