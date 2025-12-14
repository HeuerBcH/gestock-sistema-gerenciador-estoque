package dev.gestock.sge.dominio.fornecedor;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class LeadTime {
	private final int dias;

	public LeadTime(int dias) {
		isTrue(dias >= 1, "O lead time deve ser de pelo menos 1 dia");
		this.dias = dias;
	}

	public int getDias() {
		return dias;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof LeadTime) {
			var leadTime = (LeadTime) obj;
			return dias == leadTime.dias;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dias);
	}

	@Override
	public String toString() {
		return Integer.toString(dias);
	}
}

