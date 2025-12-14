package dev.gestock.sge.aplicacao.pedido;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;

public class PedidoServicoAplicacao {
	private PedidoRepositorioAplicacao repositorio;

	public PedidoServicoAplicacao(PedidoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<PedidoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public PedidoResumo obterResumo(int id) {
		return repositorio.obterResumo(id);
	}
}

