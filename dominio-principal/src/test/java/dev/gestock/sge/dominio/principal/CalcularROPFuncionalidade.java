package dev.gestock.sge.dominio.principal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.*;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;

import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

public class CalcularROPFuncionalidade {

    // ===== Estado do cenário =====
    private Map<String, Produto> produtos;
    private Produto produtoAtual;
    private Exception lastError;

    // Parâmetros de cálculo
    private Integer consumoMedio;
    private Integer leadTimeDias;
    private Integer estoqueSeguranca;
    private List<Integer> historicoConsumo;
    private Integer ropCalculado;

    // Defaults
    private static final int DEFAULT_LEAD_TIME = 7;
    private static final int DEFAULT_ESTOQUE_SEGURANCA = 20;

    private Repositorio repo;
    private RopServico ropServico;

    @Before
    public void reset() {
        produtos = new HashMap<>();
        produtoAtual = null;
        lastError = null;
        consumoMedio = null;
        leadTimeDias = null;
        estoqueSeguranca = null;
        historicoConsumo = null;
        ropCalculado = null;

        repo = new Repositorio();
        repo.limparTodos();
        ropServico = new RopServico();
    }

    // GIVENS

    @Dado("que existe um produto chamado {string}")
    public void existe_produto_chamado(String nome) {
        ProdutoId id = repo.novoProdutoId();
        produtoAtual = new Produto(id, "COD" + id.getId(), nome, "UN", false, 1.0);
        produtos.put(nome, produtoAtual);
        repo.salvar(produtoAtual);
    }

    @E("o consumo medio diario do produto e {int} unidades")
    public void consumo_medio_diario(int consumo) {
        consumoMedio = consumo;
    }

    @E("o lead time do fornecedor e {int} dias")
    public void lead_time_fornecedor(int dias) {
        leadTimeDias = dias;
    }

    @E("o estoque de seguranca e {int} unidades")
    public void estoque_seguranca(int qtd) {
        estoqueSeguranca = qtd;
    }

    @E("o historico de consumo dos ultimos 90 dias")
    public void historico_90_dias() {
        historicoConsumo = IntStream.range(0, 90)
                .map(i -> 10)
                .boxed()
                .collect(Collectors.toList());
    }

    @Dado("que existe um produto chamado {string} sem historico")
    public void produto_sem_historico(String nome) {
        existe_produto_chamado(nome);
        historicoConsumo = Collections.emptyList();
    }

    @Dado("que existe um produto chamado {string} com ROP calculado")
    public void produto_com_rop_calculado(String nome) {
        existe_produto_chamado(nome);
        consumoMedio = 10;
        leadTimeDias = DEFAULT_LEAD_TIME;
        estoqueSeguranca = DEFAULT_ESTOQUE_SEGURANCA;
        calcularROP();
    }

    // WHENS 

    @Quando("o ROP do produto for calculado")
    public void rop_do_produto_calculado() {
        lastError = null;
        try {
            ropCalculado = ropServico.calcular(consumoMedio, leadTimeDias, estoqueSeguranca, historicoConsumo);
        } catch (Exception e) {
            lastError = e;
        }
    }

    @Quando("o sistema calcula o consumo medio")
    public void sistema_calcula_consumo_medio() {
        lastError = null;
        try {
            // Delegar o cálculo do ROP ao serviço
            ropCalculado = ropServico.calcular(consumoMedio, leadTimeDias, estoqueSeguranca, historicoConsumo);
        } catch (Exception e) {
            lastError = e;
        }
    }

    @Quando("O sistema tentar calcular o ROP do produto")
    public void sistema_tenta_calcular_rop() {
        rop_do_produto_calculado();
    }

    @Quando("o cliente clica para visualizar o ROP dos produtos")
    public void cliente_clica_visualizar_rop() {
        // Recupera o último ROP calculado via serviço (consulta)
        ropServico.obterRopAtual().ifPresent(val -> ropCalculado = val);
    }

    // THENS 

    @Entao("o ROP do produto deve ser {int} unidades")
    public void rop_deve_ser(int esperado) {
        assertNull(lastError, "Erro inesperado: " + (lastError == null ? "" : lastError.getMessage()));
        assertNotNull(ropCalculado, "ROP não foi calculado");
        assertEquals(esperado, ropCalculado.intValue());
    }

    @Entao("o ROP do produto e calculado com base nesse historico")
    public void rop_calculado_com_base_no_historico() {
        assertNull(lastError, "Erro inesperado: " + (lastError == null ? "" : lastError.getMessage()));
        int lt = Optional.ofNullable(leadTimeDias).orElse(DEFAULT_LEAD_TIME);
        int es = Optional.ofNullable(estoqueSeguranca).orElse(DEFAULT_ESTOQUE_SEGURANCA);
        // Se consumoMedio não foi previamente definido, o serviço deverá ter usado média do histórico
        int consumoParaValidacao;
        if (consumoMedio == null || consumoMedio == 0) {
            double avg = historicoConsumo == null || historicoConsumo.isEmpty() ? 0.0
                    : historicoConsumo.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            consumoParaValidacao = (int) Math.round(avg);
        } else {
            consumoParaValidacao = consumoMedio;
        }
        ROP rop = new ROP(consumoParaValidacao, lt, es);
        assertNotNull(ropCalculado, "ROP não foi calculado");
        assertEquals(rop.getValorROP(), ropCalculado.intValue());
    }

    @Entao("o sistema deve exibir o valor do ROP")
    public void sistema_exibe_valor_rop() {
        assertNotNull(ropCalculado, "ROP não disponível para exibição");
    }

    @E("o sistema deve exibir o consumo medio utilizado no calculo")
    public void sistema_exibe_consumo_medio() {
        assertNotNull(consumoMedio, "Consumo médio não disponível para exibição");
    }

    @Entao("o sistema deve usar um ROP padrao de {int} unidade")
    public void sistema_usa_rop_padrao(int padrao) {
        assertNull(lastError, "Erro inesperado: " + (lastError == null ? "" : lastError.getMessage()));
        assertNotNull(ropCalculado, "ROP não calculado");
        assertEquals(padrao, ropCalculado.intValue(), "ROP padrão não aplicado");
    }

    // Helpers 

    private void calcularROP() {
        // Mantido por compatibilidade com steps existentes que chamam este helper
        ropCalculado = ropServico.calcular(consumoMedio, leadTimeDias, estoqueSeguranca, historicoConsumo);
    }
}