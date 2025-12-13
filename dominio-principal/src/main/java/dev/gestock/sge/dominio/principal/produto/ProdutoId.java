package dev.gestock.sge.dominio.principal.produto;

import java.util.Objects;

/* Value Object: Identidade imutável do Produto */
public class ProdutoId {

    private final Long id;

    public ProdutoId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }
    
    public static ProdutoId temporario() {
        return new ProdutoId(null);
    }

    public Long getId() { return id; }

    @Override public boolean equals(Object o) { 
        if (this == o) return true;
        if (!(o instanceof ProdutoId other)) return false;
        return Objects.equals(id, other.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() { return id != null ? id.toString() : "temporario"; }
}
