package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

// Value Object: Ponto de Ressuprimento (ROP)

public class ROP {

    private final double consumoMedio;     // unidades por dia
    private final int leadTimeDias;        // tempo médio de entrega (em dias)
    private final int estoqueSeguranca;    // reserva mínima para evitar rupturas
    private final int valorROP;            // resultado final do cálculo

    public ROP(double consumoMedio, int leadTimeDias, int estoqueSeguranca) {
        isTrue(consumoMedio >= 0, "Consumo médio deve ser >= 0");
        isTrue(leadTimeDias >= 0, "Lead time deve ser >= 0");
        isTrue(estoqueSeguranca >= 0, "Estoque de segurança deve ser >= 0");

        this.consumoMedio = consumoMedio;
        this.leadTimeDias = leadTimeDias;
        this.estoqueSeguranca = estoqueSeguranca;

        // R3: cálculo do ponto de ressuprimento
        this.valorROP = (int) Math.ceil(consumoMedio * leadTimeDias + estoqueSeguranca);
    }

    public int getValorROP() { return valorROP; }
    public double getConsumoMedio() { return consumoMedio; }
    public int getLeadTimeDias() { return leadTimeDias; }
    public int getEstoqueSeguranca() { return estoqueSeguranca; }

    @Override
    public String toString() {
        return "ROP = " + valorROP + " unidades (CM=" + consumoMedio + ", LT=" + leadTimeDias + ", ES=" + estoqueSeguranca + ")";
    }
}
