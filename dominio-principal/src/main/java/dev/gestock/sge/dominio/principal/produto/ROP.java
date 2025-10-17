package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

/**
 * Value Object: Ponto de Ressuprimento (ROP)
 *
 * Representa o nível mínimo de estoque em que um novo pedido deve ser feito.
 * Calculado pela fórmula:
 *     ROP = (Consumo Médio × Lead Time) + Estoque de Segurança
 *
 * Regras cobertas:
 * - R1H14: O ROP é calculado pela fórmula: ROP = (Consumo Médio Diário × Lead Time) + Estoque de Segurança
 * - R2H14: O histórico deve considerar o consumo médio dos últimos 90 dias
 */
public class ROP {

    private final double consumoMedioDiario;     // unidades por dia
    private final int leadTimeDias;               // tempo médio de entrega (em dias)
    private final double estoqueSeguranca;        // reserva mínima para evitar rupturas
    private final double valorROP;                // resultado final do cálculo

    public ROP(double consumoMedioDiario, int leadTimeDias, double estoqueSeguranca) {
        isTrue(consumoMedioDiario >= 0, "Consumo médio diário deve ser >= 0");
        isTrue(leadTimeDias >= 0, "Lead time deve ser >= 0");
        isTrue(estoqueSeguranca >= 0, "Estoque de segurança deve ser >= 0");

        this.consumoMedioDiario = consumoMedioDiario;
        this.leadTimeDias = leadTimeDias;
        this.estoqueSeguranca = estoqueSeguranca;

        // R1H14: cálculo do ponto de ressuprimento
        this.valorROP = Math.ceil(consumoMedioDiario * leadTimeDias + estoqueSeguranca);
    }

    /**
     * Calcula o ROP baseado no histórico de consumo dos últimos 90 dias
     * R2H14: O histórico deve considerar o consumo médio dos últimos 90 dias
     */
    public static ROP calcularROPHistorico(double consumoTotal90Dias, int leadTimeDias, double estoqueSeguranca) {
        double consumoMedioDiario = consumoTotal90Dias / 90.0;
        return new ROP(consumoMedioDiario, leadTimeDias, estoqueSeguranca);
    }

    public double getValorROP() { return valorROP; }
    public double getConsumoMedioDiario() { return consumoMedioDiario; }
    public int getLeadTimeDias() { return leadTimeDias; }
    public double getEstoqueSeguranca() { return estoqueSeguranca; }

    @Override
    public String toString() {
        return "ROP = " + valorROP + " unidades (CM=" + consumoMedioDiario + ", LT=" + leadTimeDias + ", ES=" + estoqueSeguranca + ")";
    }
}
