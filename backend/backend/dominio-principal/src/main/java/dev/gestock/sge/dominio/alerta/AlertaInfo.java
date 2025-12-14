package dev.gestock.sge.dominio.alerta;

/**
 * Classe que contém informações sobre um alerta gerado.
 * 
 * Pattern: Observer
 * Funcionalidade: Emitir Alertas de Estoque Baixo (RODRIGO)
 */
public class AlertaInfo {
	private final int produtoId;
	private final String produtoNome;
	private final int estoqueId;
	private final String estoqueNome;
	private final int quantidadeAtual;
	private final int rop;
	private final double percentualAbaixoRop;
	private final NivelAlerta nivel;

	public AlertaInfo(int produtoId, String produtoNome, int estoqueId, String estoqueNome,
			int quantidadeAtual, int rop, double percentualAbaixoRop, NivelAlerta nivel) {
		this.produtoId = produtoId;
		this.produtoNome = produtoNome;
		this.estoqueId = estoqueId;
		this.estoqueNome = estoqueNome;
		this.quantidadeAtual = quantidadeAtual;
		this.rop = rop;
		this.percentualAbaixoRop = percentualAbaixoRop;
		this.nivel = nivel;
	}

	public int getProdutoId() {
		return produtoId;
	}

	public String getProdutoNome() {
		return produtoNome;
	}

	public int getEstoqueId() {
		return estoqueId;
	}

	public String getEstoqueNome() {
		return estoqueNome;
	}

	public int getQuantidadeAtual() {
		return quantidadeAtual;
	}

	public int getRop() {
		return rop;
	}

	public double getPercentualAbaixoRop() {
		return percentualAbaixoRop;
	}

	public NivelAlerta getNivel() {
		return nivel;
	}
}

