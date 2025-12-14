package dev.gestock.sge.dominio.produto;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class Quantidade {
	private final int valor;

	public Quantidade(int valor) {
		isTrue(valor >= 0, "A quantidade não pode ser negativa");
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Quantidade) {
			var quantidade = (Quantidade) obj;
			return valor == quantidade.valor;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return Integer.toString(valor);
	}
}

