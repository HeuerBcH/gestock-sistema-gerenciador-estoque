package dev.gestock.sge.aplicacao.dominio.produto;

import java.util.List;
import java.util.Optional;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

public interface ProdutoRepositorioAplicacao {
	List<ProdutoResumo> pesquisarResumos();
	
	Optional<ProdutoResumo> buscarResumoPorId(ProdutoId id);
}
