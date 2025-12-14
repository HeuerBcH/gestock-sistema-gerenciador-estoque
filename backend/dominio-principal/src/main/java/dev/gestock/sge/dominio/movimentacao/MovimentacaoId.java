package dev.gestock.sge.dominio.movimentacao;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class MovimentacaoId {
	private final int id;

	public MovimentacaoId(int id) {
		isTrue(id >= 0, "O id não pode ser negativo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof MovimentacaoId) {
			var movimentacaoId = (MovimentacaoId) obj;
			return id == movimentacaoId.id;
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

