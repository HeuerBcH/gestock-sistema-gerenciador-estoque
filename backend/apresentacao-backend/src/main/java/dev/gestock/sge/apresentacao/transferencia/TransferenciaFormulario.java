package dev.gestock.sge.apresentacao.transferencia;

public class TransferenciaFormulario {

	public static class TransferenciaDto {
		public Integer id;
		public Integer produtoId;
		public Integer estoqueOrigemId;
		public Integer estoqueDestinoId;
		public Integer quantidade;
		public String responsavel;
		public String motivo;
	}
}

