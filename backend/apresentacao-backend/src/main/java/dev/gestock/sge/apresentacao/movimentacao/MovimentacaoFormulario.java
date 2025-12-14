package dev.gestock.sge.apresentacao.movimentacao;

import java.time.LocalDateTime;

public class MovimentacaoFormulario {
	public MovimentacaoDto movimentacao;
	
	public MovimentacaoFormulario(MovimentacaoDto movimentacao) {
		this.movimentacao = movimentacao;
	}

	public static class MovimentacaoDto {
		public Integer id;
		public LocalDateTime dataHora;
		public Integer produtoId;
		public Integer estoqueId;
		public Integer quantidade;
		public String tipo;
		public String motivo;
		public String responsavel;
	}
}

