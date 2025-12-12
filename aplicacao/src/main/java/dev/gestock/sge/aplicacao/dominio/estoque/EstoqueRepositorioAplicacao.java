package dev.gestock.sge.aplicacao.dominio.estoque;

import java.util.List;
import java.util.Optional;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;

public interface EstoqueRepositorioAplicacao {
	Optional<EstoqueResumo> buscarResumoPorId(EstoqueId id);
	
	List<EstoqueResumo> pesquisarResumos();
	
	List<EstoqueResumo> pesquisarComFiltros(String busca, ClienteId clienteId, Boolean ativo);
}
