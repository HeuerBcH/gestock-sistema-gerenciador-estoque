package dev.gestock.sge.apresentacao.fornecedor;

import java.math.BigDecimal;

public class FornecedorFormulario {
	public FornecedorDto fornecedor;
	
	public FornecedorFormulario(FornecedorDto fornecedor) {
		this.fornecedor = fornecedor;
	}

	public static class FornecedorDto {
		public Integer id;
		public String nome;
		public String cnpj;
		public String contato;
		public Integer leadTime;
		public BigDecimal custo;
		public String status;
	}
}

