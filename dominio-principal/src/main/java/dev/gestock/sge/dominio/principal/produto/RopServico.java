package dev.gestock.sge.dominio.principal.produto;

import java.util.*;

public class RopServico {

    private Integer ultimoRopCalculado;

    public int calcular(Integer consumoMedio, Integer leadTimeDias, Integer estoqueSeguranca, List<Integer> historicoConsumo) {
        boolean temHistorico = historicoConsumo != null && !historicoConsumo.isEmpty();

        Integer consumo = consumoMedio;
        if ((consumo == null || consumo == 0) && temHistorico) {
            double avg = historicoConsumo.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            consumo = (int) Math.round(avg);
        }

        if ((consumo == null || consumo == 0) && !temHistorico) {
            ultimoRopCalculado = 1; // ROP padrão sem histórico
            return ultimoRopCalculado;
        }

        int lt = leadTimeDias != null ? leadTimeDias : 7;
        int es = estoqueSeguranca != null ? estoqueSeguranca : 20;
        ROP rop = new ROP(consumo, lt, es);
        ultimoRopCalculado = rop.getValorROP();
        return ultimoRopCalculado;
    }

    public OptionalInt obterRopAtual() {
        return ultimoRopCalculado == null ? OptionalInt.empty() : OptionalInt.of(ultimoRopCalculado);
    }
}
