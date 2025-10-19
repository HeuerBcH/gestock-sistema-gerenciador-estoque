package dev.gestock.sge.dominio.principal.alerta;

import java.util.Objects;

public class AlertaId {
    private final Long id;

    public AlertaId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertaId)) return false;
        AlertaId alertaId = (AlertaId) o;
        return Objects.equals(id, alertaId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
