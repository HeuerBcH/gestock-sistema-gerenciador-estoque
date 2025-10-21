package dev.gestock.sge.dominio.principal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.*;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;

public class CalcularROPFeature {

    // ===== estado por cenário =====
    private Map<String, Produto> produtos;
    private Produto produtoAtual;
    private Exception lastError;

    // defaults usados quando cenário não especifica
    private static final int DEFAULT_LEAD_TIME = 7;
    private static final int DEFAULT_ESTOQUE_SEGURANCA = 20;
    private static final int ROP_PADRAO_SEM_HISTORICO = 1;

    @Before
    public void reset() {
        produtos = new HashMap<>();
        produtoAtual = null;
        lastError = null;
    }

    // ===== entidades simples =====
    static class Produto {
        String nome;
        Integer consumoMedioDiario; // pode ser calculado ou informado
        Integer leadTime; // dias
        Integer estoqueSeguranca;
        Integer rop;
        List<Integer> historicoConsumo = new ArrayList<>();

        Produto(String nome) { this.nome = nome; }
    }

    // ===== GIVENS =====

    @Dado("que existe um produto chamado {string}")
    public void existe_produto_chamado(String nome) {
        produtoAtual = new Produto(nome);
        produtos.put(nome, produtoAtual);
    }

    @E("o consumo medio diario do produto e {int} unidades")
    public void consumo_medio_diario(int consumo) {
        produtoAtual.consumoMedioDiario = consumo;
    }

    @E("o lead time do fornecedor e {int} dias")
    public void lead_time_fornecedor(int dias) {
        produtoAtual.leadTime = dias;
    }

    @E("o estoque de seguranca e {int} unidades")
    public void estoque_seguranca(int qtd) {
        produtoAtual.estoqueSeguranca = qtd;
    }

    @E("o historico de consumo dos ultimos 90 dias")
    public void historico_90_dias() {
        // para facilitar, popular com valores variados (ex: 90 entradas de 10 => média 10)
        produtoAtual.historicoConsumo = IntStream.range(0, 90).map(i -> 10).boxed().collect(Collectors.toList());
    }

    @Dado("que existe um produto chamado {string} sem historico")
    public void produto_sem_historico(String nome) {
        existe_produto_chamado(nome);
        produtoAtual.historicoConsumo.clear();
    }

    @Dado("que existe um produto chamado {string} com ROP calculado")
    public void produto_com_rop_calculado(String nome) {
        existe_produto_chamado(nome);
        // aplicar valores que garantam cálculo
        produtoAtual.consumoMedioDiario = 10;
        produtoAtual.leadTime = DEFAULT_LEAD_TIME;
        produtoAtual.estoqueSeguranca = DEFAULT_ESTOQUE_SEGURANCA;
        calcularROPPara(produtoAtual);
    }

    // ===== WHENS =====

    @Quando("o ROP do produto for calculado")
    public void rop_do_produto_calculado() {
        lastError = null;
        try {
            calcularROPPara(produtoAtual);
        } catch (Exception e) { lastError = e; }
    }

    @Quando("o sistema calcula o consumo medio")
    public void sistema_calcula_consumo_medio() {
        lastError = null;
        try {
            if (produtoAtual.historicoConsumo == null || produtoAtual.historicoConsumo.isEmpty()) {
                produtoAtual.consumoMedioDiario = null;
            } else {
                double avg = produtoAtual.historicoConsumo.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                produtoAtual.consumoMedioDiario = (int)Math.round(avg);
            }
            // opcional: após calcular consumo, calcular ROP usando defaults se necessário
            calcularROPPara(produtoAtual);
        } catch (Exception e) { lastError = e; }
    }

    @Quando("O sistema tentar calcular o ROP do produto")
    public void sistema_tenta_calcular_rop() {
        // corresponde ao passo com "O" maiúsculo no feature
        rop_do_produto_calculado();
    }

    @Quando("o cliente clica para visualizar o ROP dos produtos")
    public void cliente_clica_visualizar_rop() {
        // ação de visualização; estado já preparado pelos Givens
    }

    // ===== THENS =====

    @Entao("o ROP do produto deve ser {int} unidades")
    public void rop_deve_ser(int esperado) {
        assertNull(lastError, "Erro inesperado durante cálculo: " + (lastError == null ? "" : lastError.getMessage()));
        assertNotNull(produtoAtual.rop, "ROP não foi calculado");
        assertEquals(esperado, produtoAtual.rop.intValue(), "ROP calculado incorreto");
    }

    @Entao("o ROP do produto e calculado com base nesse historico")
    public void rop_calculado_com_base_no_historico() {
        assertNull(lastError, "Erro inesperado durante cálculo: " + (lastError == null ? "" : lastError.getMessage()));
        assertNotNull(produtoAtual.consumoMedioDiario, "Consumo médio não foi calculado a partir do histórico");
        // verificar que ROP reflete a fórmula (ConsumoMedio * LeadTime) + EstoqueSegurança
        int lt = (produtoAtual.leadTime == null) ? DEFAULT_LEAD_TIME : produtoAtual.leadTime;
        int es = (produtoAtual.estoqueSeguranca == null) ? DEFAULT_ESTOQUE_SEGURANCA : produtoAtual.estoqueSeguranca;
        int esperado = produtoAtual.consumoMedioDiario * lt + es;
        assertNotNull(produtoAtual.rop, "ROP não foi calculado");
        assertEquals(esperado, produtoAtual.rop.intValue(), "ROP não foi calculado com base no histórico");
    }

    @Entao("o sistema deve exibir o valor do ROP")
    public void sistema_exibe_valor_rop() {
        assertNotNull(produtoAtual.rop, "ROP não disponível para exibição");
    }

    @E("o sistema deve exibir o consumo medio utilizado no calculo")
    public void sistema_exibe_consumo_medio() {
        assertNotNull(produtoAtual.consumoMedioDiario, "Consumo médio não disponível para exibição");
    }

    @Entao("o sistema deve usar um ROP padrao de {int} unidade")
    public void sistema_usa_rop_padrao(int padrao) {
        assertNull(lastError, "Erro inesperado durante cálculo: " + (lastError == null ? "" : lastError.getMessage()));
        assertNotNull(produtoAtual.rop, "ROP não calculado");
        assertEquals(padrao, produtoAtual.rop.intValue(), "ROP padrão não aplicado");
    }

    // ===== helpers =====

    private void calcularROPPara(Produto p) {
        // se não há histórico e consumo médio não informado => usar ROP padrão
        boolean temHistorico = p.historicoConsumo != null && !p.historicoConsumo.isEmpty();
        if ((p.consumoMedioDiario == null || p.consumoMedioDiario == 0) && !temHistorico) {
            p.rop = ROP_PADRAO_SEM_HISTORICO;
            return;
        }

        // se existe histórico mas consumo médio ainda não calculado, calcular
        if ((p.consumoMedioDiario == null || p.consumoMedioDiario == 0) && temHistorico) {
            double avg = p.historicoConsumo.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            p.consumoMedioDiario = (int)Math.round(avg);
        }

        int consumo = (p.consumoMedioDiario == null) ? 0 : p.consumoMedioDiario;
        int lt = (p.leadTime == null || p.leadTime == 0) ? DEFAULT_LEAD_TIME : p.leadTime;
        int es = (p.estoqueSeguranca == null) ? DEFAULT_ESTOQUE_SEGURANCA : p.estoqueSeguranca;

        p.rop = consumo * lt + es;
    }
}
