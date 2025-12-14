package dev.gestock.sge.dominio.autenticacao.cliente;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class Senha {
	private final String valor;

	public Senha(String valor) {
		notNull(valor, "A senha não pode ser nula");
		notBlank(valor, "A senha não pode estar em branco");
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	public boolean validar(String senhaInformada) {
		return valor.equals(senhaInformada);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Senha) {
			var senha = (Senha) obj;
			return valor.equals(senha.valor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return "***"; // Não expor a senha
	}
}

