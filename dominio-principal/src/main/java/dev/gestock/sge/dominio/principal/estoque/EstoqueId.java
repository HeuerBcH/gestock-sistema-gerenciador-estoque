package dev.gestock.sge.dominio.principal.estoque;

import java.util.Objects;

/* Value Object: identidade do Estoque. */
public class EstoqueId {
    private final Long id;

    public EstoqueId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }

    public static EstoqueId temporario() {
        return new EstoqueId(null);
    }

    public Long getId() { return id; }

    @Override public boolean equals(Object o) { 
        if (this == o) return true;
        if (!(o instanceof EstoqueId other)) return false;
        return Objects.equals(id, other.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() { return id != null ? id.toString() : "temporario"; }
}
