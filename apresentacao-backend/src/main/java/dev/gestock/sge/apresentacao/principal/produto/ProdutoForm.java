package dev.gestock.sge.apresentacao.principal.produto;

public class ProdutoForm {
	public ProdutoDto produto;

	public ProdutoForm(ProdutoDto produto) {
		this.produto = produto;
	}

	public static class ProdutoDto {
		public Long id;
		public String codigo;
		public String nome;
		public String unidadePeso;
		public double peso;
		public boolean perecivel;
		public boolean ativo;
	}
}
