package dev.gestock.sge.dominio.principal.fornecedor;

/**
 * Value Object: Identidade imutável do Fornecedor
 * 
 * Características:
 * - Imutável
 * - Comparação por valor
 * - Validação de invariantes
 */
public class FornecedorId {

    private final int id;

    public FornecedorId() {
        this.id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public FornecedorId(int id) {
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
        FornecedorId other = (FornecedorId) obj;
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