package dev.gestock.sge.dominio.principal.fornecedor;

import java.util.Objects;

/* Value Object: Identidade imutável do Fornecedor */
public class FornecedorId {
    private final Long id;

    public FornecedorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }

    public Long getId() { return id; }

    @Override public boolean equals(Object o) { return o instanceof FornecedorId other && id.equals(other.id); }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() { return id.toString(); }
}
