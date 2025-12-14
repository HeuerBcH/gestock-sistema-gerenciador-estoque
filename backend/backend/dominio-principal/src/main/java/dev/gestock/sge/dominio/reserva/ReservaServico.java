package dev.gestock.sge.dominio.reserva;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import dev.gestock.sge.dominio.pedido.Pedido;
import dev.gestock.sge.dominio.pedido.PedidoId;
import dev.gestock.sge.dominio.pedido.ItemPedido;

public class ReservaServico {
	private final ReservaRepositorio repositorio;

	public ReservaServico(ReservaRepositorio repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	/**
	 * Cria reservas para todos os itens de um pedido.
	 */
	public void criarReservas(Pedido pedido) {
		notNull(pedido, "O pedido não pode ser nulo");
		
		var dataHoraReserva = new DataHoraReserva(LocalDateTime.now());
		var reservas = new ArrayList<Reserva>();

		for (var item : pedido.getItens()) {
			// Criar ID temporário (será gerado pelo banco)
			var reservaId = new ReservaId(0);
			var reserva = new Reserva(reservaId, pedido.getId(), item.getProdutoId(), item.getQuantidade(),
					dataHoraReserva, StatusReserva.ATIVA);
			reservas.add(reserva);
		}

		// Salvar todas as reservas
		for (var reserva : reservas) {
			repositorio.salvar(reserva);
		}
	}

	/**
	 * Libera todas as reservas de um pedido.
	 */
	public void liberarReservas(PedidoId pedidoId, TipoLiberacao tipoLiberacao) {
		notNull(pedidoId, "O id do pedido não pode ser nulo");
		notNull(tipoLiberacao, "O tipo de liberação não pode ser nulo");

		var reservas = repositorio.obterPorPedido(pedidoId);
		for (var reserva : reservas) {
			if (reserva.getStatus() == StatusReserva.ATIVA) {
				reserva.liberar(tipoLiberacao);
				repositorio.salvar(reserva);
			}
		}
	}
}

