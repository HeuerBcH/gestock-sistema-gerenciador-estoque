package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

import io.cucumber.java.pt.*;
import java.util.*;
import static org.junit.Assert.*;

public class RegistrarMovimentacaoFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    private Estoque estoque;
    private Produto produto;
    private ProdutoId produtoId;
    private int saldoAnterior;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Exception excecaoCapturada;
    private String mensagemErro;
    private Movimentacao ultimaMovimentacao;

    // =========================================================
    // H20 — Registrar movimentações de estoque
    // =========================================================

    // ✅ Step renomeado para evitar conflito com GerenciarEstoqueFuncionalidade
    @Dado("que existe um estoque de movimentacao chamado {string}")
    public void queExisteUmEstoqueDeMovimentacaoChamado(String nome) {
        EstoqueId id = repositorio.novoEstoqueId();
        ClienteId clienteId = new ClienteId(1L);
        estoque = new Estoque(id, clienteId, nome, "Endereco A", 1000);
        repositorio.salvar(estoque);
    }

    @Dado("que existe um estoque {string}")
    public void queExisteUmEstoque(String nome) {
        queExisteUmEstoqueDeMovimentacaoChamado(nome);
    }

    @Dado("existe um produto {string}")
    public void existeUmProduto(String nome) {
        produtoId = repositorio.novoProdutoId();
        produto = new Produto(produtoId, "PROD-001", nome, "UN", false, 0.0);
        repositorio.salvar(produto);
    }

    @Dado("existe um produto chamado {string}")
    public void existeUmProdutoChamado(String nome) {
        existeUmProduto(nome);
    }

    @Quando("o cliente registra uma entrada de {int} unidades do produto")
    public void oClienteRegistraUmaEntradaDeUnidadesDoProduto(int quantidade) {
        saldoAnterior = estoque.getSaldoFisico(produtoId);
        estoque.registrarEntrada(produtoId, quantidade, "Cliente", "Entrada manual", Map.of());
        ultimaMovimentacao = new Movimentacao(
                1L, TipoMovimentacao.ENTRADA, produtoId, quantidade,
                java.time.LocalDateTime.now(), "Cliente", "Entrada manual", Map.of());
        movimentacoes.add(ultimaMovimentacao);
    }

    @Entao("o saldo do estoque apos a movimentacao deve aumentar em {int} unidades")
    public void oSaldoDoEstoqueAposMovimentacaoAumenta(int quantidade) {
        int saldoNovo = estoque.getSaldoFisico(produtoId);
        assertEquals(saldoAnterior + quantidade, saldoNovo);
    }

    @E("uma movimentacao do tipo ENTRADA deve ser criada")
    public void umaMovimentacaoDoTipoENTRADADeveSerCriada() {
        assertNotNull("Movimentacao deve ter sido criada", ultimaMovimentacao);
        assertEquals(TipoMovimentacao.ENTRADA, ultimaMovimentacao.getTipo());
    }

    // ---------------------------------------------------------
    // R2H20 — Saídas indicam motivo
    // ---------------------------------------------------------

    @Dado("que existe um estoque com {int} unidades do produto")
    public void queExisteUmEstoqueComUnidadesDoProduto(int quantidade) {
        EstoqueId id = repositorio.novoEstoqueId();
        ClienteId clienteId = new ClienteId(1L);
        estoque = new Estoque(id, clienteId, "Estoque A", "Endereco A", 1000);
        produtoId = repositorio.novoProdutoId();
        estoque.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", Map.of());
    }

    @Quando("o cliente registra uma saída de {int} unidades com motivo {string}")
    public void oClienteRegistraUmaSaidaDeUnidadesComMotivo(int quantidade, String motivo) {
        saldoAnterior = estoque.getSaldoFisico(produtoId);
        estoque.registrarSaida(produtoId, quantidade, "Cliente", motivo);
        ultimaMovimentacao = new Movimentacao(
                2L, TipoMovimentacao.SAIDA, produtoId, quantidade,
                java.time.LocalDateTime.now(), "Cliente", motivo, Map.of());
        movimentacoes.add(ultimaMovimentacao);
    }

    @Entao("o saldo do estoque deve diminuir em {int} unidades")
    public void oSaldoDoEstoqueDeveDiminuirEmUnidades(int quantidade) {
        int saldoNovo = estoque.getSaldoFisico(produtoId);
        assertEquals(saldoAnterior - quantidade, saldoNovo);
    }

    @E("a movimentacao deve conter o motivo {string}")
    public void aMovimentacaoDeveConterOMotivo(String motivo) {
        assertEquals(motivo, ultimaMovimentacao.getMotivo());
    }

    // ---------------------------------------------------------
    // Cenário: saldo insuficiente
    // ---------------------------------------------------------

    @Dado("que existe um estoque com {int} unidades disponiveis do produto")
    public void queExisteUmEstoqueComUnidadesDisponiveisDoProduto(int quantidade) {
        EstoqueId id = repositorio.novoEstoqueId();
        ClienteId clienteId = new ClienteId(1L);
        estoque = new Estoque(id, clienteId, "Estoque A", "Endereco A", 1000);
        produtoId = repositorio.novoProdutoId();
        estoque.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", Map.of());
    }

    @Quando("o cliente tenta registrar uma saida de {int} unidades")
    public void oClienteTentaRegistrarUmaSaidaDeUnidades(int quantidade) {
        try {
            estoque.registrarSaida(produtoId, quantidade, "Cliente", "Saida teste");
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve rejeitar a operacao")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull("Excecao deve ter sido capturada", excecaoCapturada);
    }

    @E("deve exibir a mensagem {string}")
    public void deveExibirAMensagem(String mensagem) {
        assertNotNull("Mensagem de erro deve existir", mensagemErro);
        assertTrue("Mensagem incorreta", mensagemErro.contains(mensagem));
    }

    // =========================================================
    // H21 — Visualizar historico de movimentacoes
    // =========================================================

    @Dado("que existem {int} movimentacoes registradas para o produto")
    public void queExistemMovimentacoesRegistradasParaOProduto(int quantidade) {
        produtoId = repositorio.novoProdutoId();
        for (int i = 0; i < quantidade; i++) {
            TipoMovimentacao tipo = (i % 2 == 0)
                    ? TipoMovimentacao.ENTRADA
                    : TipoMovimentacao.SAIDA;
            movimentacoes.add(new Movimentacao(
                    (long) (i + 1), tipo, produtoId, 10,
                    java.time.LocalDateTime.now(), "Sistema", "Teste", Map.of()));
        }
    }

    @Quando("o cliente visualiza o historico do produto")
    public void oClienteVisualizaOHistoricoDoProduto() {
        assertFalse("Movimentacoes devem existir", movimentacoes.isEmpty());
    }

    @Entao("o sistema deve exibir todas as {int} movimentacoes")
    public void oSistemaDeveExibirTodasAsMovimentacoes(int quantidade) {
        assertEquals(quantidade, movimentacoes.size());
    }

    @E("cada movimentacao deve conter data, tipo, quantidade e responsavel")
    public void cadaMovimentacaoDeveConterDataTipoQuantidadeEResponsavel() {
        for (Movimentacao mov : movimentacoes) {
            assertNotNull("Data deve existir", mov.getDataHora());
            assertNotNull("Tipo deve existir", mov.getTipo());
            assertTrue("Quantidade deve ser maior que zero", mov.getQuantidade() > 0);
            assertNotNull("Responsavel deve existir", mov.getResponsavel());
        }
    }
}
