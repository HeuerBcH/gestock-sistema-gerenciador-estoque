package dev.gestock.sge.dominio.pontoresuprimento;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class ConsumoMedioDiario {
	private final double valor;

	public ConsumoMedioDiario(double valor) {
		isTrue(valor >= 0, "O consumo médio diário não pode ser negativo");
		this.valor = valor;
	}

	public double getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ConsumoMedioDiario) {
			var consumo = (ConsumoMedioDiario) obj;
			return Double.compare(valor, consumo.valor) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return Double.toString(valor);
	}
}

