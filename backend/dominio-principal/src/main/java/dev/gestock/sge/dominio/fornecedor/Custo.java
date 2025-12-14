package dev.gestock.sge.dominio.fornecedor;

import static org.apache.commons.lang3.Validate.*;
import java.math.BigDecimal;
import java.util.Objects;

public class Custo {
	private final BigDecimal valor;

	public Custo(BigDecimal valor) {
		notNull(valor, "O custo não pode ser nulo");
		isTrue(valor.compareTo(BigDecimal.ZERO) > 0, "O custo deve ser maior que zero");
		this.valor = valor;
	}

	public Custo(double valor) {
		this(BigDecimal.valueOf(valor));
	}

	public BigDecimal getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Custo) {
			var custo = (Custo) obj;
			return valor.compareTo(custo.valor) == 0;
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

