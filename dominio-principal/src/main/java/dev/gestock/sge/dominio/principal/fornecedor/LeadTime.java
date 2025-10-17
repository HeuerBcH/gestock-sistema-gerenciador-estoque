package src.main.java.dev.gestock.sge.dominio.principal.fornecedor;

/**
 * Value Object: Lead Time
 *
 * Representa o tempo médio de entrega de um fornecedor (R14).
 * Calculado automaticamente com base em histórico de entregas.
 */
public class LeadTime {

    private final int dias;

    public LeadTime(int dias) {
        if (dias < 0) throw new IllegalArgumentException("Lead time não pode ser negativo");
        this.dias = dias;
    }

    public int getDias() { return dias; }

    @Override
    public String toString() { return dias + " dias"; }
}
