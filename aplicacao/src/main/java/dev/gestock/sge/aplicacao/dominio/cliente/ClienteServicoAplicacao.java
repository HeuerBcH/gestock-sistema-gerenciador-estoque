package dev.gestock.sge.aplicacao.dominio.cliente;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class ClienteServicoAplicacao {
	private final ClienteRepositorioAplicacao repositorio;

	public ClienteServicoAplicacao(ClienteRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositorio não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<ClienteResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
}
