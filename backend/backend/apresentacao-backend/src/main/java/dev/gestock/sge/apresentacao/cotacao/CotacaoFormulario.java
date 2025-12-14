package dev.gestock.sge.apresentacao.cotacao;

import java.math.BigDecimal;

public class CotacaoFormulario {
	public CotacaoDto cotacao;
	
	public CotacaoFormulario(CotacaoDto cotacao) {
		this.cotacao = cotacao;
	}

	public static class CotacaoDto {
		public Integer id;
		public Integer produtoId;
		public Integer fornecedorId;
		public BigDecimal preco;
		public Integer leadTime;
		public String validade;
		public String statusAprovacao;
	}
}

