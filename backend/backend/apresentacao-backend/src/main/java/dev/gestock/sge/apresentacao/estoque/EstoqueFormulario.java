package dev.gestock.sge.apresentacao.estoque;

public class EstoqueFormulario {
	public EstoqueDto estoque;
	
	public EstoqueFormulario(EstoqueDto estoque) {
		this.estoque = estoque;
	}

	public static class EstoqueDto {
		public Integer id;
		public String nome;
		public String endereco;
		public Integer capacidade;
		public String status;
	}
}

