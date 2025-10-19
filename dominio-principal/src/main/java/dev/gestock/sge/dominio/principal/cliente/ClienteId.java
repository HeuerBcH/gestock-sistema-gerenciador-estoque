package dev.gestock.sge.dominio.principal.cliente;

import java.util.Objects;

/**
 * Value Object que encapsula a identidade do Cliente.
 *
 * - Garante que a identidade é imutável.
 * - Evita "primitive obsession" (usar Long cru em todo o código).
 * - Define igualdade por valor (do Long).
 */
public class ClienteId {
	private final Long id;

	public ClienteId(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID deve ser positivo");
		}
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ClienteId other && id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
