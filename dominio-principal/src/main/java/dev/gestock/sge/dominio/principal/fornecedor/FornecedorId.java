package src.main.java.dev.gestock.sge.dominio.principal.fornecedor;

import java.util.UUID;

/** Value Object: Identidade imutável do Fornecedor */
public class FornecedorId {
    private final UUID id;

    public FornecedorId() { this.id = UUID.randomUUID(); }
    public FornecedorId(UUID id) { this.id = id; }

    public UUID getId() { return id; }

    @Override public boolean equals(Object o) { return o instanceof FornecedorId other && id.equals(other.id); }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() { return id.toString(); }
}
