package dev.gestock.sge.apresentacao.pontoresuprimento;

public class PontoRessuprimentoFormulario {
	public PontoRessuprimentoDto pontoRessuprimento;
	
	public PontoRessuprimentoFormulario(PontoRessuprimentoDto pontoRessuprimento) {
		this.pontoRessuprimento = pontoRessuprimento;
	}

	public static class PontoRessuprimentoDto {
		public Integer id;
		public Integer estoqueId;
		public Integer produtoId;
		public Integer estoqueSeguranca;
	}
}

