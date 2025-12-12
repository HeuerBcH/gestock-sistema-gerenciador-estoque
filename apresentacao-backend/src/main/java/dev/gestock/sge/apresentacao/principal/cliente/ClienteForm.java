package dev.gestock.sge.apresentacao.principal.cliente;

public class ClienteForm {
	public ClienteDto cliente;

	public ClienteForm(ClienteDto cliente) {
		this.cliente = cliente;
	}

	public static class ClienteDto {
		public Long id;
		public String nome;
		public String documento;
		public String email;
	}
}
