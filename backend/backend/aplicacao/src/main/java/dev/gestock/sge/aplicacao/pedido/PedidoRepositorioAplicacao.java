package dev.gestock.sge.aplicacao.pedido;

import java.util.List;

public interface PedidoRepositorioAplicacao {
	List<PedidoResumo> pesquisarResumos();

	PedidoResumo obterResumo(int id);
}

