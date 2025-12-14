package dev.gestock.sge.aplicacao.pontoresuprimento;

public interface PontoRessuprimentoResumo {
	int getId();

	int getEstoqueId();

	String getEstoqueNome();

	int getProdutoId();

	String getProdutoNome();

	double getConsumoMedioDiario();

	double getConsumoMaximoDiario();

	int getLeadTimeMedio();

	int getLeadTimeMaximo();

	int getEstoqueSeguranca();

	int getRopCalculado();

	int getSaldoAtual();

	String getStatus();
}

