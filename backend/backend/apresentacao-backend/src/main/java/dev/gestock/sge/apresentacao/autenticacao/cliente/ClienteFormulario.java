package dev.gestock.sge.apresentacao.autenticacao.cliente;

public class ClienteFormulario {
	public ClienteDto cliente;
	
	public ClienteFormulario(ClienteDto cliente) {
		this.cliente = cliente;
	}

	public static class ClienteDto {
		public String nome;
		public String email;
		public String documento;
		public String senha;
	}

	public static class LoginDto {
		public String email;
		public String senha;
	}
}

