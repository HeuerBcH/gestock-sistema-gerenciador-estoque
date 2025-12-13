package dev.gestock.sge.dominio.principal.fornecedor;

import java.util.Objects;

/* Value Object: Identidade imutável do Fornecedor */
public class FornecedorId {
    private final Long id;

    public FornecedorId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }

    public static FornecedorId temporario() {
        return new FornecedorId(null);
    }

    public Long getId() { return id; }

    @Override public boolean equals(Object o) { 
        if (this == o) return true;
        if (!(o instanceof FornecedorId other)) return false;
        return Objects.equals(id, other.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() { return id != null ? id.toString() : "temporario"; }
}
