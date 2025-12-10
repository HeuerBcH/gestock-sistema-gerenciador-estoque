package dev.gestock.sge.aplicacao.dominio.produto;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

public interface ProdutoResumo {
	ProdutoId getId();

	String getCodigo();

	String getNome();

	String getUnidadePeso();

	double getPeso();

	boolean isPerecivel();

	boolean isAtivo();
}
