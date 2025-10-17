package dev.gestock.sge.dominio.principal.estoque;

/**
 * Value Object: Identidade imutável do Estoque
 * 
 * Características:
 * - Imutável
 * - Comparação por valor
 * - Validação de invariantes
 */
public class EstoqueId {

    private final int id;

    public EstoqueId() {
        this.id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public EstoqueId(int id) {
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
        EstoqueId other = (EstoqueId) obj;
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