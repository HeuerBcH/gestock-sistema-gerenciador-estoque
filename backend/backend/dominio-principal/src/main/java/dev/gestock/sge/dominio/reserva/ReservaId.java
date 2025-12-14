package dev.gestock.sge.dominio.reserva;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class ReservaId {
	private final int id;

	public ReservaId(int id) {
		isTrue(id >= 0, "O id não pode ser negativo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ReservaId) {
			var reservaId = (ReservaId) obj;
			return id == reservaId.id;
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

