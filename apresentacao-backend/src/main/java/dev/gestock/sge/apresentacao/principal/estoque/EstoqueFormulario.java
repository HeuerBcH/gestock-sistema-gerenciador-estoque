package dev.gestock.sge.apresentacao.principal.estoque;

public class EstoqueFormulario {
	public EstoqueDto estoque;

	public EstoqueFormulario(EstoqueDto estoque) {
		this.estoque = estoque;
	}

	public static class EstoqueDto {
		public Long id;
		public Long clienteId;
		public String nome;
		public String endereco;
		public int capacidade;
		public boolean ativo;
	}
}
