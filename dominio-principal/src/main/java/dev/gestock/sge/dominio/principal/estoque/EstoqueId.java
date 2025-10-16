package dev.gestock.sge.dominio.principal.estoque;

import java.util.UUID;

/** Value Object: identidade do Estoque. */
public class EstoqueId {
	private final UUID id;

	public EstoqueId() {
		this.id = UUID.randomUUID();
	}
	public EstoqueId(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof EstoqueId other && id.equals(other.id);
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
