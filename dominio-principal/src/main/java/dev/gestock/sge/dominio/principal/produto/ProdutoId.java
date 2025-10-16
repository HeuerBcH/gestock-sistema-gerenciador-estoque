package dev.gestock.sge.dominio.principal.produto;

import java.util.UUID;

/** Value Object: Identidade imutável do Produto */
public class ProdutoId {

    private final UUID id;

    public ProdutoId() { this.id = UUID.randomUUID(); }
    public ProdutoId(UUID id) { this.id = id; }

    public UUID getId() { return id; }

    @Override public boolean equals(Object o) { return o instanceof ProdutoId other && id.equals(other.id); }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() { return id.toString(); }
}
