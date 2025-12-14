package dev.gestock.sge.dominio.reserva;

import java.util.List;
import dev.gestock.sge.dominio.pedido.PedidoId;

public interface ReservaRepositorio {
	void salvar(Reserva reserva);

	Reserva obter(ReservaId id);

	List<Reserva> obterPorPedido(PedidoId pedidoId);
}

