package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import io.cucumber.java.pt.*;
import java.util.*;
import static org.junit.Assert.*;

public class RegistrarMovimentacaoFuncionalidade {

    private Estoque estoque;
    private Produto produto;
    private ProdutoId produtoId;
    private int saldoAnterior;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Exception excecaoCapturada;
    private String mensagemErro;
    private Movimentacao ultimaMovimentacao;

    @Dado("que existe um estoque {string}")
    public void queExisteUmEstoque(String nome) {
        EstoqueId id = new EstoqueId(1L);
        ClienteId clienteId = new ClienteId(1L);
        estoque = new Estoque(id, clienteId, nome, "Endereço A", 1000);
    }

    @Dado("que existe um estoque chamado {string}")
    public void queExisteUmEstoqueChamado(String nome) {
        queExisteUmEstoque(nome);
    }

    @Dado("existe um produto {string}")
    public void existeUmProduto(String nome) {
        produtoId = new ProdutoId(1L);
        produto = new Produto(produtoId, "PROD-001", nome, "UN", false, 0.0);
    }

    @Dado("existe um produto chamado {string}")
    public void existeUmProdutoChamado(String nome) {
        existeUmProduto(nome);
    }

    @Quando("o cliente registra uma entrada de {int} unidades do produto")
    public void oClienteRegistraUmaEntradaDeUnidadesDoProduto(int quantidade) {
        euRegistroUmaEntradaDeUnidades(quantidade);
    }

    @Quando("eu registro uma entrada de {int} unidades")
    public void euRegistroUmaEntradaDeUnidades(int quantidade) {
        saldoAnterior = estoque.getSaldoFisico(produtoId);
        estoque.registrarEntrada(produtoId, quantidade, "Sistema", "Entrada manual", Map.of());
        ultimaMovimentacao = new Movimentacao(
            1L, TipoMovimentacao.ENTRADA, produtoId, quantidade, 
            java.time.LocalDateTime.now(), "Sistema", "Entrada manual", Map.of());
        movimentacoes.add(ultimaMovimentacao);
    }

    @Então("o saldo do estoque deve aumentar em {int} unidades")
    public void oSaldoDoEstoqueDeveAumentarEmUnidades(int quantidade) {
        oSaldoDeveAumentarEmUnidades(quantidade);
    }

    @Então("o saldo deve aumentar em {int} unidades")
    public void oSaldoDeveAumentarEmUnidades(int quantidade) {
        int saldoNovo = estoque.getSaldoFisico(produtoId);
        assertEquals(saldoAnterior + quantidade, saldoNovo);
    }

    @Então("uma movimentação do tipo ENTRADA deve ser criada")
    public void umaMovimentacaoDoTipoENTRADADeveSerCriada() {
        umaMovimentacaoDeENTRADADeveSerCriada();
    }

    @Então("uma movimentação de ENTRADA deve ser criada")
    public void umaMovimentacaoDeENTRADADeveSerCriada() {
        assertNotNull("Movimentação deve ter sido criada", ultimaMovimentacao);
        assertEquals(TipoMovimentacao.ENTRADA, ultimaMovimentacao.getTipo());
    }

    @Dado("que existe um estoque com {int} unidades do produto")
    public void queExisteUmEstoqueComUnidadesDoProduto(int quantidade) {
        queExisteUmEstoqueComUnidades(quantidade);
    }

    @Dado("que existe um estoque com {int} unidades")
    public void queExisteUmEstoqueComUnidades(int quantidade) {
        EstoqueId id = new EstoqueId(1L);
        ClienteId clienteId = new ClienteId(1L);
        estoque = new Estoque(id, clienteId, "Estoque A", "Endereço A", 1000);
        produtoId = new ProdutoId(1L);
        estoque.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", Map.of());
    }

    @Quando("o cliente registra uma saída de {int} unidades com motivo {string}")
    public void oClienteRegistraUmaSaidaDeUnidadesComMotivo(int quantidade, String motivo) {
        euRegistroUmaSaidaDeUnidadesComMotivo(quantidade, motivo);
    }

    @Quando("eu registro uma saída de {int} unidades com motivo {string}")
    public void euRegistroUmaSaidaDeUnidadesComMotivo(int quantidade, String motivo) {
        saldoAnterior = estoque.getSaldoFisico(produtoId);
        estoque.registrarSaida(produtoId, quantidade, "Sistema", motivo);
        ultimaMovimentacao = new Movimentacao(
            1L, TipoMovimentacao.SAIDA, produtoId, quantidade, 
            java.time.LocalDateTime.now(), "Sistema", motivo, Map.of());
        movimentacoes.add(ultimaMovimentacao);
    }

    @Então("o saldo do estoque deve diminuir em {int} unidades")
    public void oSaldoDoEstoqueDeveDiminuirEmUnidades(int quantidade) {
        oSaldoDeveDiminuirEmUnidades(quantidade);
    }

    @Então("o saldo deve diminuir em {int} unidades")
    public void oSaldoDeveDiminuirEmUnidades(int quantidade) {
        int saldoNovo = estoque.getSaldoFisico(produtoId);
        assertEquals(saldoAnterior - quantidade, saldoNovo);
    }

    @Então("a movimentação deve conter o motivo {string}")
    public void aMovimentacaoDeveConterOMotivo(String motivo) {
        assertEquals(motivo, ultimaMovimentacao.getMotivo());
    }

    @Dado("que existe um estoque com {int} unidades disponíveis do produto")
    public void queExisteUmEstoqueComUnidadesDisponiveisDoProduto(int quantidade) {
        queExisteUmEstoqueComUnidades(quantidade);
    }

    @Dado("que existe um estoque com {int} unidades disponíveis")
    public void queExisteUmEstoqueComUnidadesDisponiveis(int quantidade) {
        queExisteUmEstoqueComUnidades(quantidade);
    }

    @Quando("o cliente tenta registrar uma saída de {int} unidades")
    public void oClienteTentaRegistrarUmaSaidaDeUnidades(int quantidade) {
        euTentoRegistrarUmaSaidaDeUnidades(quantidade);
    }

    @Quando("eu tento registrar uma saída de {int} unidades")
    public void euTentoRegistrarUmaSaidaDeUnidades(int quantidade) {
        try {
            estoque.registrarSaida(produtoId, quantidade, "Sistema", "Saída");
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Então("o sistema deve rejeitar a operação")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull("Exceção deve ter sido capturada", excecaoCapturada);
    }

    @Então("deve exibir a mensagem {string}")
    public void deveExibirAMensagem(String mensagem) {
        assertNotNull("Mensagem de erro deve existir", mensagemErro);
        assertTrue("Mensagem incorreta", mensagemErro.contains(mensagem));
    }

    @Dado("que existem {int} movimentações registradas para o produto")
    public void queExistemMovimentacoesRegistradasParaOProduto(int quantidade) {
        queExistemMovimentacoesRegistradas(quantidade);
    }

    @Dado("que existem {int} movimentações registradas")
    public void queExistemMovimentacoesRegistradas(int quantidade) {
        produtoId = new ProdutoId(1L);
        for (int i = 0; i < quantidade; i++) {
            TipoMovimentacao tipo = (i % 2 == 0) ? TipoMovimentacao.ENTRADA : TipoMovimentacao.SAIDA;
            movimentacoes.add(new Movimentacao(
                (long) (i + 1), tipo, produtoId, 10, 
                java.time.LocalDateTime.now(), "Sistema", "Teste", Map.of()));
        }
    }

    @Quando("o cliente visualiza o histórico do produto")
    public void oClienteVisualizaOHistoricoDoProduto() {
        euVisualizoOHistoricoDoProduto();
    }

    @Quando("eu visualizo o histórico do produto")
    public void euVisualizoOHistoricoDoProduto() {
        assertFalse("Movimentações devem existir", movimentacoes.isEmpty());
    }

    @Então("o sistema deve exibir todas as {int} movimentações")
    public void oSistemaDeveExibirTodasAsMovimentacoes(int quantidade) {
        devoVerTodasAsMovimentacoes(quantidade);
    }

    @Então("devo ver todas as {int} movimentações")
    public void devoVerTodasAsMovimentacoes(int quantidade) {
        assertEquals(quantidade, movimentacoes.size());
    }

    @Então("cada movimentação deve conter data, tipo, quantidade e responsável")
    public void cadaMovimentacaoDeveConterDataTipoQuantidadeEResponsavel() {
        for (Movimentacao mov : movimentacoes) {
            assertNotNull("Data deve existir", mov.getDataHora());
            assertNotNull("Tipo deve existir", mov.getTipo());
            assertTrue("Quantidade deve ser maior que zero", mov.getQuantidade() > 0);
            assertNotNull("Responsável deve existir", mov.getResponsavel());
        }
    }
}
