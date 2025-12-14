package dev.gestock.sge.dominio.estoque;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class Endereco {
	private final String valor;

	public Endereco(String valor) {
		notNull(valor, "O endereço não pode ser nulo");
		notBlank(valor, "O endereço não pode estar em branco");
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Endereco) {
			var endereco = (Endereco) obj;
			return valor.equals(endereco.valor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return valor;
	}
}

