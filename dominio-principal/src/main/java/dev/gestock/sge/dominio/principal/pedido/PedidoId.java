package dev.gestock.sge.dominio.principal.pedido;

import java.util.Objects;

/** VO: Identificador imutável do Pedido (Long). */
public class PedidoId {
    private final Long id;

    public PedidoId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }

    public Long getId() { return id; }

    @Override public String toString() { return id.toString(); }
    @Override public boolean equals(Object o){ return o instanceof PedidoId other && id.equals(other.id); }
    @Override public int hashCode(){ return Objects.hash(id); }
}
