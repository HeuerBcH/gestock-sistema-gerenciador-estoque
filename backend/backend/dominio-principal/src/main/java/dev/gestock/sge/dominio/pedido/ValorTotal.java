package dev.gestock.sge.dominio.pedido;

import static org.apache.commons.lang3.Validate.*;
import java.math.BigDecimal;
import java.util.Objects;

public class ValorTotal {
	private final BigDecimal valor;

	public ValorTotal(BigDecimal valor) {
		notNull(valor, "O valor total não pode ser nulo");
		isTrue(valor.compareTo(BigDecimal.ZERO) >= 0, "O valor total não pode ser negativo");
		this.valor = valor;
	}

	public BigDecimal getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ValorTotal) {
			var valorTotal = (ValorTotal) obj;
			return valor.compareTo(valorTotal.valor) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return valor.toString();
	}
}

