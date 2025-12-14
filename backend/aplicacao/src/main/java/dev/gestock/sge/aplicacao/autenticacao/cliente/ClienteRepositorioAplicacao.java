package dev.gestock.sge.aplicacao.autenticacao.cliente;

import java.util.List;

public interface ClienteRepositorioAplicacao {
	List<ClienteResumo> pesquisarResumos();

	ClienteResumo obterResumo(int id);
}

