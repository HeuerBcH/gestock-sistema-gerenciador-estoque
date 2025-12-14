package dev.gestock.sge.aplicacao.produto;

import java.util.List;

public interface ProdutoResumo {
	int getId();

	String getCodigo();

	String getNome();

	int getPeso();

	String getPerecivel();

	String getStatus();

	List<String> getFornecedores();
}

