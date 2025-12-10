package dev.gestock.sge.aplicacao.dominio.pedido;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class PedidoServicoAplicacao {
	private final PedidoRepositorioAplicacao repositorio;

	public PedidoServicoAplicacao(PedidoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<PedidoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
}
