package dev.gestock.sge.aplicacao.dominio.fornecedor;

public interface FornecedorResumo {
	Long getId();

	String getNome();

	String getCnpj();

	String getContato();

	Integer getLeadTimeMedio();

	boolean isAtivo();
}
