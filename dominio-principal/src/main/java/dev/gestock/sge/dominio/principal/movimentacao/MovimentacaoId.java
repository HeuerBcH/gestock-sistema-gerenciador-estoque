package dev.gestock.sge.dominio.principal.movimentacao;

/**
 * Value Object: Identidade imutável da Movimentacao
 * 
 * Características:
 * - Imutável
 * - Comparação por valor
 * - Validação de invariantes
 */
public class MovimentacaoId {

    private final int id;

    public MovimentacaoId() {
        this.id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public MovimentacaoId(int id) {
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
        MovimentacaoId other = (MovimentacaoId) obj;
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