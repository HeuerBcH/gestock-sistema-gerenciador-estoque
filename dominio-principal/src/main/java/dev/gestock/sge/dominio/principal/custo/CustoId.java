package dev.gestock.sge.dominio.principal.custo;

import java.util.Objects;

/**
 * VO: Identificador único do custo.
 * Cada registro de custo é identificado por um Long imutável.
 */
public class CustoId {

    private final Long id;

    public CustoId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }

    public Long getId() { return id; }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CustoId other) && id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return id.toString(); }
}
