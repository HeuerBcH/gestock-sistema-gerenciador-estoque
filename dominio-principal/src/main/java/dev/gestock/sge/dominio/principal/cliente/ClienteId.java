package dev.gestock.sge.dominio.principal.cliente;

import java.util.UUID;

/**
 * Value Object que encapsula a identidade do Cliente.
 *
 * - Garante que a identidade é imutável.
 * - Evita "primitive obsession" (usar String/UUID cru em todo o código).
 * - Define igualdade por valor (do UUID).
 */
public class ClienteId {
	private final UUID id;

	public ClienteId() {
		this.id = UUID.randomUUID();
	} // gera novo UUID
	public ClienteId(UUID id) {
		this.id = id;
	}         // usado em reidratação

	public UUID getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ClienteId other && id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
