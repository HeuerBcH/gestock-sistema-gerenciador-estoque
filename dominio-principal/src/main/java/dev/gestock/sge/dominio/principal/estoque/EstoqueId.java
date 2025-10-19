package dev.gestock.sge.dominio.principal.estoque;

import java.util.Objects;

/** Value Object: identidade do Estoque. */
public class EstoqueId {
	private final Long id;

	public EstoqueId(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID deve ser positivo");
		}
		this.id = id;
	}

	public Long getId() { return id; }

	@Override public boolean equals(Object o) { return o instanceof EstoqueId other && id.equals(other.id); }
	@Override public int hashCode() { return Objects.hash(id); }
	@Override public String toString() { return id.toString(); }
}
