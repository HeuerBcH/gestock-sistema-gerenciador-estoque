package dev.gestock.sge.dominio.pontoresuprimento;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class EstoqueSeguranca {
	private final int valor;

	public EstoqueSeguranca(int valor) {
		isTrue(valor >= 0, "O estoque de segurança não pode ser negativo");
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof EstoqueSeguranca) {
			var estoqueSeguranca = (EstoqueSeguranca) obj;
			return valor == estoqueSeguranca.valor;
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

