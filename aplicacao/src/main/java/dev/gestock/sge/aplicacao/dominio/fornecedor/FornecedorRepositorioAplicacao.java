package dev.gestock.sge.aplicacao.dominio.fornecedor;

import java.util.List;
import java.util.Optional;

import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;

public interface FornecedorRepositorioAplicacao {
	List<FornecedorResumo> pesquisarResumos();
	
	Optional<FornecedorResumo> buscarResumoPorId(FornecedorId id);
	
	List<FornecedorResumo> pesquisarComFiltros(String busca, Boolean ativo);
}
