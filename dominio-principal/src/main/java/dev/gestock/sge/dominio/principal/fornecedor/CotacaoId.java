package dev.gestock.sge.dominio.principal.fornecedor;

/**
 * Value Object: CotacaoId
 * 
 * Identificador único para uma cotação
 */
public class CotacaoId {
    
    private final int id;
    
    public CotacaoId() {
        this.id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
    
    public CotacaoId(int id) {
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
        CotacaoId other = (CotacaoId) obj;
        return id == other.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "CotacaoId[" + id + "]";
    }
}