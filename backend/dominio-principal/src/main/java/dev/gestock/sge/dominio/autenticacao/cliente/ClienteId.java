package dev.gestock.sge.dominio.autenticacao.cliente;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class ClienteId {
	private final int id;

	public ClienteId(int id) {
		isTrue(id >= 0, "O id não pode ser negativo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ClienteId) {
			var clienteId = (ClienteId) obj;
			return id == clienteId.id;
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

