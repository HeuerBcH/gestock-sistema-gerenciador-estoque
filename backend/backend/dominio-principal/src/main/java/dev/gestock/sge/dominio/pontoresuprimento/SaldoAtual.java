package dev.gestock.sge.dominio.pontoresuprimento;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class SaldoAtual {
	private final int valor;

	public SaldoAtual(int valor) {
		isTrue(valor >= 0, "O saldo atual não pode ser negativo");
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SaldoAtual) {
			var saldo = (SaldoAtual) obj;
			return valor == saldo.valor;
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

