package dev.gestock.sge.dominio.principal.cliente;

import java.util.Objects;

/* Value Object que encapsula a identidade do Cliente.

 - Garante que a identidade é imutável.
 - Evita "primitive obsession" (usar Long cru em todo o código).
 - Define igualdade por valor (do Long). */
public class ClienteId {
    private final Long id;

    public ClienteId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }
    
    /**
     * Cria um ClienteId temporário para novos clientes (ID será gerado pelo JPA).
     */
    public static ClienteId temporario() {
        return new ClienteId(null);
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ClienteId other)) {
            return false;
        }
        if (this.id == null && other.id == null) {
            return true;
        }
        if (this.id == null || other.id == null) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id != null ? id.toString() : "null";
    }
}
