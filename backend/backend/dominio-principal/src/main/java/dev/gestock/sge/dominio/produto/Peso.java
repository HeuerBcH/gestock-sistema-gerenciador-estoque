package dev.gestock.sge.dominio.produto;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class Peso {
	private final int gramas;

	public Peso(int gramas) {
		isTrue(gramas > 0, "O peso deve ser maior que zero");
		this.gramas = gramas;
	}

	public int getGramas() {
		return gramas;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Peso) {
			var peso = (Peso) obj;
			return gramas == peso.gramas;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(gramas);
	}

	@Override
	public String toString() {
		return Integer.toString(gramas);
	}
}

