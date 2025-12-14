package dev.gestock.sge.dominio.pedido;

import java.util.List;

public interface PedidoRepositorio {
	Pedido salvar(Pedido pedido);

	Pedido obter(PedidoId id);

	List<Pedido> obterTodos();

	void remover(PedidoId id);
}

