package dev.gestock.sge.dominio.estoque;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class Capacidade {
	private final int valor;

	public Capacidade(int valor) {
		isTrue(valor > 0, "A capacidade deve ser maior que zero");
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Capacidade) {
			var capacidade = (Capacidade) obj;
			return valor == capacidade.valor;
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

