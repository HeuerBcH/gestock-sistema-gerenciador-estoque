package dev.gestock.sge.apresentacao.principal.fornecedor;

public class FornecedorForm {
	public FornecedorDto fornecedor;

	public FornecedorForm(FornecedorDto fornecedor) {
		this.fornecedor = fornecedor;
	}

	public static class FornecedorDto {
		public Long id;
		public String nome;
		public String cnpj;
		public String contato;
		public Integer leadTimeMedio;
		public boolean ativo;
	}
}
