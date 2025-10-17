package dev.gestock.sge.dominio.principal.produto;

/**
 * Value Object: Identidade imutável do Produto
 * 
 * Características:
 * - Imutável
 * - Comparação por valor
 * - Validação de invariantes
 */
public class ProdutoId {

    private final int id;

    public ProdutoId() {
        this.id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public ProdutoId(int id) {
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
        ProdutoId other = (ProdutoId) obj;
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