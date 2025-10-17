package src.main.java.dev.gestock.sge.dominio.principal.pedido;

import java.util.UUID;

/** VO: Identificador imutável do Pedido (UUID). */
public class PedidoId {
    private final UUID id;

    public PedidoId() { this.id = UUID.randomUUID(); }
    public PedidoId(UUID id) { this.id = id; }

    public UUID getId() { return id; }

    @Override public String toString() { return id.toString(); }
    @Override public boolean equals(Object o){ return o instanceof PedidoId other && id.equals(other.id); }
    @Override public int hashCode(){ return id.hashCode(); }
}
