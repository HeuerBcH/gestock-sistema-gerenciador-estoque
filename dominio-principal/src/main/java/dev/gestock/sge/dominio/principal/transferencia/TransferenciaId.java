package dev.gestock.sge.dominio.principal.transferencia;

/**
 * Value Object: Identidade imutável da Transferencia
 * 
 * Características:
 * - Imutável
 * - Comparação por valor
 * - Validação de invariantes
 */
public class TransferenciaId {

    private final int id;

    public TransferenciaId() {
        this.id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public TransferenciaId(int id) {
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
        TransferenciaId other = (TransferenciaId) obj;
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