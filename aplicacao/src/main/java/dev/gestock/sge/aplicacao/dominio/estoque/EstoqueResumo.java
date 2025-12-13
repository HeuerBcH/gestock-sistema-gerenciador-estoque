package dev.gestock.sge.aplicacao.dominio.estoque;

public interface EstoqueResumo {
	Long getId();

	Long getClienteId();

	String getNome();

	String getEndereco();

	int getCapacidade();

	boolean isAtivo();
}
