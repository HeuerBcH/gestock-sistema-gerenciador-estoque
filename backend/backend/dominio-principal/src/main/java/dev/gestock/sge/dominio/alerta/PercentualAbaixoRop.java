package dev.gestock.sge.dominio.alerta;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class PercentualAbaixoRop {
	private final double valor;

	public PercentualAbaixoRop(double valor) {
		isTrue(valor <= 0, "O percentual abaixo do ROP deve ser negativo ou zero");
		this.valor = valor;
	}

	public double getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof PercentualAbaixoRop) {
			var percentual = (PercentualAbaixoRop) obj;
			return Double.compare(valor, percentual.valor) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return String.format("%.2f%%", valor);
	}
}

