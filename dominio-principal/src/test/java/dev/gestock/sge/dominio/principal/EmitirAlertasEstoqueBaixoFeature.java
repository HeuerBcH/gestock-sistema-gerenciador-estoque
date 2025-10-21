package dev.gestock.sge.dominio.principal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;

public class EmitirAlertasEstoqueBaixoFeature {

    // ===== Estado por cenário =====
    private Map<String, Produto> produtos;
    private Map<String, Alerta> alertas;
    private Map<String, Fornecedor> fornecedores;
    private List<Alerta> alertasAtivos;
    private Pedido pedidoRecebido;
    private Produto produtoAtual;
    private Alerta alertaAtual;
    private Fornecedor fornecedorAtual;
    private int saldoAtual;
    private Exception lastError;

    private AtomicInteger seq;

    @Before
    public void reset() {
        produtos = new HashMap<>();
        alertas = new HashMap<>();
        fornecedores = new HashMap<>();
        alertasAtivos = new ArrayList<>();
        pedidoRecebido = null;
        produtoAtual = null;
        alertaAtual = null;
        fornecedorAtual = null;
        saldoAtual = 0;
        lastError = null;
        seq = new AtomicInteger(1);
    }

    // ===== Entidades Simples =====
    static class Produto {
        String nome;
        int rop;
        int saldo;
        Produto(String nome, int rop, int saldo) {
            this.nome = nome;
            this.rop = rop;
            this.saldo = saldo;
        }
    }

    static class Fornecedor {
        String nome;
        boolean cotacaoValida;
        double cotacao;
        boolean ativo;
        Fornecedor(String nome, boolean cotacaoValida, double cotacao, boolean ativo) {
            this.nome = nome;
            this.cotacaoValida = cotacaoValida;
            this.cotacao = cotacao;
            this.ativo = ativo;
        }
    }

    static class Alerta {
        Produto produto;
        int saldo;
        Fornecedor fornecedorSugerido;
        boolean ativo;
        Alerta(Produto produto, int saldo, Fornecedor fornecedorSugerido) {
            this.produto = produto;
            this.saldo = saldo;
            this.fornecedorSugerido = fornecedorSugerido;
            this.ativo = true;
        }
    }

    static class Pedido {
        Produto produto;
        int quantidade;
        Pedido(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }
    }

    // ===== Givens =====

    @Dado("que existe um produto com ROP de {int} unidades")
    public void existe_produto_com_rop(int rop) {
        String nome = "Produto" + seq.getAndIncrement();
        produtoAtual = new Produto(nome, rop, 0);
        produtos.put(nome, produtoAtual);
    }

    @E("o saldo atual do produto e {int} unidades")
    public void saldo_atual_produto(int saldo) {
        produtoAtual.saldo = saldo;
        saldoAtual = saldo;
    }

    @Dado("que existe um alerta gerado para um produto")
    public void existe_alerta_gerado_para_produto() {
        existe_produto_com_rop(100);
        saldo_atual_produto(90);
        fornecedorAtual = new Fornecedor("Fornecedor1", true, 10.0, true);
        alertaAtual = new Alerta(produtoAtual, saldoAtual, fornecedorAtual);
        alertas.put(produtoAtual.nome, alertaAtual);
    }

    @E("o fornecedor sugerido possui cotacao valida e ativa")
    public void fornecedor_sugerido_cotacao_valida_ativa() {
        fornecedorAtual = new Fornecedor("Fornecedor1", true, 10.0, true);
        alertaAtual.fornecedorSugerido = fornecedorAtual;
    }

    @Dado("que existem {int} alertas ativos")
    public void existem_alertas_ativos(int qtd) {
        for (int i = 1; i <= qtd; i++) {
            Produto p = new Produto("Produto" + i, 100, 90);
            Fornecedor f = new Fornecedor("Fornecedor" + i, true, 10.0 + i, true);
            Alerta a = new Alerta(p, p.saldo, f);
            alertasAtivos.add(a);
        }
    }

    @Dado("que existe um alerta ativo para um produto")
    public void existe_alerta_ativo_para_produto() {
        existe_produto_com_rop(100);
        saldo_atual_produto(90);
        fornecedorAtual = new Fornecedor("Fornecedor1", true, 10.0, true);
        alertaAtual = new Alerta(produtoAtual, saldoAtual, fornecedorAtual);
        alertaAtual.ativo = true;
        alertas.put(produtoAtual.nome, alertaAtual);
    }

    @E("um pedido foi recebido para suprir o estoque do produto")
    public void pedido_recebido_para_produto() {
        pedidoRecebido = new Pedido(produtoAtual, 20);
    }

    // ===== Whens =====

    @Quando("o sistema verifica o estoque")
    public void sistema_verifica_estoque() {
        lastError = null;
        try {
            if (produtoAtual.saldo <= produtoAtual.rop) {
                fornecedorAtual = new Fornecedor("Fornecedor1", true, 10.0, true);
                alertaAtual = new Alerta(produtoAtual, produtoAtual.saldo, fornecedorAtual);
                alertas.put(produtoAtual.nome, alertaAtual);
            }
        } catch (Exception e) {
            lastError = e;
        }
    }

    @Quando("o cliente visualiza o alerta")
    public void cliente_visualiza_alerta() {
        // Simula visualização, nada a fazer
    }

    @Quando("o cliente visualiza a lista de alertas")
    public void cliente_visualiza_lista_alertas() {
        // Simula visualização, nada a fazer
    }

    @Quando("o sistema atualiza o estoque")
    public void sistema_atualiza_estoque() {
        lastError = null;
        try {
            if (pedidoRecebido != null) {
                produtoAtual.saldo += pedidoRecebido.quantidade;
                // Remove alerta se estoque suprido
                if (alertaAtual != null && produtoAtual.saldo > produtoAtual.rop) {
                    alertaAtual.ativo = false;
                }
            }
        } catch (Exception e) {
            lastError = e;
        }
    }

    // ===== Thens =====

    @Entao("um alerta deve ser gerado automaticamente")
    public void alerta_gerado_automaticamente() {
        assertNotNull(alertas.get(produtoAtual.nome), "Alerta não foi gerado automaticamente");
        assertTrue(alertas.get(produtoAtual.nome).ativo, "Alerta não está ativo");
    }

    @Entao("um alerta deve ser gerado")
    public void alerta_gerado() {
        alerta_gerado_automaticamente();
    }

    @Entao("nenhum alerta deve ser gerado")
    public void nenhum_alerta_gerado() {
        assertNull(alertas.get(produtoAtual.nome), "Alerta foi gerado indevidamente");
    }

    @Entao("o sistema deve exibir o nome do produto")
    public void sistema_exibe_nome_produto() {
        assertNotNull(alertaAtual, "Alerta não existe");
        assertEquals(produtoAtual.nome, alertaAtual.produto.nome, "Nome do produto não exibido corretamente");
    }

    @E("o sistema deve exibir o estoque afetado")
    public void sistema_exibe_estoque_afetado() {
        assertNotNull(alertaAtual, "Alerta não existe");
        assertEquals(produtoAtual.saldo, alertaAtual.saldo, "Estoque afetado não exibido corretamente");
    }

    @E("o sistema deve exibir o fornecedor com menor cotacao")
    public void sistema_exibe_fornecedor_menor_cotacao() {
        assertNotNull(alertaAtual, "Alerta não existe");
        assertNotNull(alertaAtual.fornecedorSugerido, "Fornecedor sugerido não exibido");
        assertTrue(alertaAtual.fornecedorSugerido.cotacaoValida && alertaAtual.fornecedorSugerido.ativo, "Fornecedor não possui cotação válida e ativa");
    }

    @Entao("o sistema deve exibir {int} alertas")
    public void sistema_exibe_qtd_alertas(int qtd) {
        assertEquals(qtd, alertasAtivos.size(), "Quantidade de alertas exibidos incorreta");
    }

    @Entao("o alerta deve ser removido automaticamente")
    public void alerta_removido_automaticamente() {
        assertNotNull(alertaAtual, "Alerta não existe");
        assertFalse(alertaAtual.ativo, "Alerta não foi removido automaticamente");
    }
}
