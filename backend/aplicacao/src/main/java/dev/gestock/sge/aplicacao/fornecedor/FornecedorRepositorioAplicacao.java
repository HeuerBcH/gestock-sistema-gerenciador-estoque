package dev.gestock.sge.aplicacao.fornecedor;

import java.util.List;

public interface FornecedorRepositorioAplicacao {
	List<FornecedorResumo> pesquisarResumos();

	List<FornecedorResumo> pesquisarPorNomeOuCnpj(String termo);

	List<FornecedorResumo> pesquisarPorStatus(String status);

	FornecedorResumo obterResumo(int id);
}

