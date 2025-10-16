package dev.gestock.sge.dominio.principal.custo;

import java.util.UUID;

/**
 * VO: Identificador único do custo.
 * Cada registro de custo é identificado por um UUID imutável.
 */
public class CustoId {

    private final UUID id;

    public CustoId() {
        this.id = UUID.randomUUID();
    }

    public CustoId(UUID id) {
        this.id = id;
    }

    public UUID getId() { return id; }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CustoId other) && id.equals(other.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() { return id.toString(); }
}
