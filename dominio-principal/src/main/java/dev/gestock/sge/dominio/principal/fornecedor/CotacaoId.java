package dev.gestock.sge.dominio.principal.fornecedor;

import java.util.UUID;

/** Value Object: Identificador de cotação */
public class CotacaoId {
    private final UUID id;

    public CotacaoId() { this.id = UUID.randomUUID(); }
    public CotacaoId(UUID id) { this.id = id; }

    public UUID getId() { return id; }

    @Override public boolean equals(Object o) { return o instanceof CotacaoId other && id.equals(other.id); }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() { return id.toString(); }
}
