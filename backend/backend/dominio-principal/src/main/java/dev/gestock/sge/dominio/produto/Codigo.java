package dev.gestock.sge.dominio.produto;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class Codigo {
	private final String valor;

	public Codigo(String valor) {
		notNull(valor, "O código não pode ser nulo");
		notBlank(valor, "O código não pode estar em branco");
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Codigo) {
			var codigo = (Codigo) obj;
			return valor.equals(codigo.valor);
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

