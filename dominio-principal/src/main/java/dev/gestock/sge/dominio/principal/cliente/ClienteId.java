package dev.gestock.sge.dominio.principal.cliente;

/**
 * Value Object: Identidade imutável do Cliente
 * 
 * Características:
 * - Imutável
 * - Comparação por valor
 * - Validação de invariantes
 */
public class ClienteId {

    private final int id;

    public ClienteId() {
        this.id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public ClienteId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClienteId other = (ClienteId) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}