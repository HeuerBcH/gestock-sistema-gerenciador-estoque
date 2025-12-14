package dev.gestock.sge.apresentacao.produto;

import java.util.List;

public class ProdutoFormulario {
	public ProdutoDto produto;
	
	public ProdutoFormulario(ProdutoDto produto) {
		this.produto = produto;
	}

	public static class ProdutoDto {
		public Integer id;
		public String codigo;
		public String nome;
		public Integer peso;
		public String perecivel;
		public String status;
		public List<Integer> fornecedores;
	}
}

