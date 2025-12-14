package dev.gestock.sge.aplicacao.autenticacao.cliente;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;

public class ClienteServicoAplicacao {
	private ClienteRepositorioAplicacao repositorio;

	public ClienteServicoAplicacao(ClienteRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<ClienteResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public ClienteResumo obterResumo(int id) {
		return repositorio.obterResumo(id);
	}
}

