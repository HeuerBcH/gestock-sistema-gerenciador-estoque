package dev.gestock.sge.dominio.pontoresuprimento;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class PontoRessuprimentoId {
	private final int id;

	public PontoRessuprimentoId(int id) {
		isTrue(id >= 0, "O id não pode ser negativo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof PontoRessuprimentoId) {
			var pontoRessuprimentoId = (PontoRessuprimentoId) obj;
			return id == pontoRessuprimentoId.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}
}

