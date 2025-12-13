package dev.gestock.sge.aplicacao.dominio.produto;

public interface ProdutoResumo {
	Long getId();

	String getCodigo();

	String getNome();

	String getUnidadePeso();

	double getPeso();

	boolean isPerecivel();

	boolean isAtivo();
}
