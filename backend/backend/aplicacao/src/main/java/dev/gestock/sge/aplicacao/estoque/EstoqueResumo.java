package dev.gestock.sge.aplicacao.estoque;

public interface EstoqueResumo {
	int getId();

	String getNome();

	String getEndereco();

	int getCapacidade();

	int getQuantidadeAtual();

	int getCapacidadeDisponivel();

	int getOcupacao();

	String getStatus();
}

