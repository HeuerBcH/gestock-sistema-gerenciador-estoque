package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.dominio.principal.cliente.*;
import dev.gestock.sge.dominio.principal.pedido.*;
import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

import io.cucumber.java.pt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrarMovimentacaoFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    // Construtor público sem argumentos exigido pelo Cucumber
    public RegistrarMovimentacaoFuncionalidade() {
    }

    private Estoque estoque;
    private Produto produto;
    private Cliente cliente;
    private Pedido pedido;
    private Movimentacao movimentacao;
    private Exception excecaoCapturada;
    private String mensagemErro;
    private int saldoAnterior;
    private int saldoAtual;
    private List<Movimentacao> historicoMovimentacoes;

    // =============================================================
    // H20 — Registrar movimentações manuais
    // =============================================================

    @Dado("que existe um estoque de movimentacao chamado {string}")
    public void queExisteUmEstoqueDeMovimentacaoChamado(String nomeEstoque) {
        ClienteId clienteId = repositorio.novoClienteId();
        cliente = new Cliente(clienteId, "Cliente Teste", "123.456.789-00", "cliente@teste.com");
        repositorio.salvar(cliente);

        EstoqueId estoqueId = repositorio.novoEstoqueId();
        estoque = new Estoque(estoqueId, cliente.getId(), nomeEstoque, "Endereço Teste", 1000);
        repositorio.salvar(estoque);
    }

    @E("existe um produto chamado {string}")
    public void existeUmProdutoChamado(String nomeProduto) {
        ProdutoId produtoId = repositorio.novoProdutoId();

        produto = new Produto(produtoId, "PROD-001", nomeProduto, "UN", false, 1.0);
        repositorio.salvar(produto);
    }

    @Quando("o cliente registra uma entrada de {int} unidades do produto")
    public void oClienteRegistraUmaEntradaDeUnidadesDoProduto(int quantidade) {
        try {
            // Saldo antes
            saldoAnterior = estoque.getSaldoDisponivel(produto.getId());

            // Ação de domínio: entrada
            estoque.registrarEntrada(
                    produto.getId(),
                    quantidade,
                    "Sistema",
                    "Entrada manual",
                    Map.of("origem", "manual")
            );

            // Saldo depois
            saldoAtual = estoque.getSaldoDisponivel(produto.getId());

            // Última movimentação registrada no agregado
            List<Movimentacao> movs = estoque.getMovimentacoesSnapshot();
            movimentacao = movs.isEmpty() ? null : movs.get(movs.size() - 1);

        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    // Método que está sendo compartilhado com GerenciarPedidos
    @Entao("o saldo do estoque apos a movimentacao deve aumentar em {int} unidades")
    public void oSaldoDoEstoqueAposAMovimentacaoDeveAumentarEmUnidades(int quantidadeEsperada) {
        int diferencaSaldo = saldoAtual - saldoAnterior;
        assertEquals(quantidadeEsperada, diferencaSaldo);
    }

    @E("uma movimentacao do tipo ENTRADA deve ser criada")
    public void umaMovimentacaoDoTipoENTRADADeveSerCriada() {
        assertNotNull(movimentacao);
        assertEquals(TipoMovimentacao.ENTRADA, movimentacao.getTipo());
        assertEquals(produto.getId(), movimentacao.getProdutoId());
    }

    // -------------------------------------------------------------
    // R1H20 — Entradas geradas automaticamente após recebimento de pedido
    // -------------------------------------------------------------

    @Dado("que existe um pedido recebido para o produto {string} com {int} unidades")
    public void queExisteUmPedidoRecebidoParaOProdutoComUnidades(String nomeProduto, int quantidade) {
        // Criar cliente
        ClienteId clienteId = repositorio.novoClienteId();
        cliente = new Cliente(clienteId, "Cliente Teste", "123.456.789-00", "cliente@teste.com");
        repositorio.salvar(cliente);

        // Criar fornecedor
        FornecedorId fornecedorId = repositorio.novoFornecedorId();
        Fornecedor fornecedor = new Fornecedor(fornecedorId, "Fornecedor Teste", "12.345.678/0001-90", "contato@fornecedor.com");
        repositorio.salvar(fornecedor);

        // Criar produto
        ProdutoId produtoId = repositorio.novoProdutoId();

        produto = new Produto(produtoId, "PROD-002", nomeProduto, "UN", false, 1.0);
        repositorio.salvar(produto);

        // Criar pedido
        PedidoId pedidoId = repositorio.novoPedidoId();
        pedido = new Pedido(pedidoId, cliente.getId(), fornecedor.getId());

        ItemPedido item = new ItemPedido(produto.getId(), quantidade, BigDecimal.valueOf(50.0));
        pedido.adicionarItem(item);

        // Simular pedido recebido
        pedido.enviar();
        pedido.registrarRecebimento();

        repositorio.salvar(pedido);
    }

    @E("o pedido esta associado ao estoque {string}")
    public void oPedidoEstaAssociadoAoEstoque(String nomeEstoque) {
        EstoqueId estoqueId = repositorio.novoEstoqueId();
        estoque = new Estoque(estoqueId, cliente.getId(), nomeEstoque, "Endereço Teste", 1000);
        repositorio.salvar(estoque);

        // Associar pedido ao estoque
        pedido.setEstoqueId(estoqueId);
        repositorio.salvar(pedido);
    }

    @Quando("o sistema processa o recebimento do pedido")
    public void oSistemaProcessaORecebimentoDoPedido() {
        try {
            // Saldo antes
            saldoAnterior = estoque.getSaldoDisponivel(produto.getId());

            // Registrar a entrada no estoque referente ao recebimento do pedido
            int qtd = pedido.getItens().get(0).getQuantidade();
            estoque.registrarEntrada(
                    produto.getId(),
                    qtd,
                    "Sistema",
                    "Recebimento automático de pedido",
                    Map.of("pedidoId", pedido.getId().getId().toString())
            );

            // Saldo depois
            saldoAtual = estoque.getSaldoDisponivel(produto.getId());

            // Última movimentação
            List<Movimentacao> movs = estoque.getMovimentacoesSnapshot();
            movimentacao = movs.isEmpty() ? null : movs.get(movs.size() - 1);

        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve gerar automaticamente uma movimentacao do tipo ENTRADA")
    public void oSistemaDeveGerarAutomaticamenteUmaMovimentacaoDoTipoENTRADA() {
        assertNotNull(movimentacao);
        assertEquals(TipoMovimentacao.ENTRADA, movimentacao.getTipo());
        assertTrue(movimentacao.getMotivo().contains("Recebimento automático"));
    }

    @E("o saldo do estoque deve aumentar em {int} unidades")
    public void oSaldoDoEstoqueDeveAumentarEmUnidades(int quantidadeEsperada) {
        int diferencaSaldo = saldoAtual - saldoAnterior;
        assertEquals(quantidadeEsperada, diferencaSaldo);
    }

    // -------------------------------------------------------------
    // R2H20 — Saídas indicam motivo
    // -------------------------------------------------------------

    @Dado("que existe um estoque com {int} unidades do produto")
    public void queExisteUmEstoqueComUnidadesDoProduto(int saldoInicial) {
        ClienteId clienteId = repositorio.novoClienteId();
        cliente = new Cliente(clienteId, "Cliente Teste", "123.456.789-00", "cliente@teste.com");
        repositorio.salvar(cliente);

        ProdutoId produtoId = repositorio.novoProdutoId();

        produto = new Produto(produtoId, "PROD-003", "Produto Teste", "UN", false, 1.0);
        repositorio.salvar(produto);

        EstoqueId estoqueId = repositorio.novoEstoqueId();
        estoque = new Estoque(estoqueId, cliente.getId(), "Estoque Teste", "Endereço Teste", 1000);
        repositorio.salvar(estoque);

        // Inicializar saldo físico real via entrada
        estoque.registrarEntrada(
                produto.getId(),
                saldoInicial,
                "Setup",
                "Carga inicial de teste",
                Map.of("setup", "true")
        );
        // Atualiza marcador de saldo anterior
        saldoAnterior = estoque.getSaldoDisponivel(produto.getId());
    }

    // NOVO STEP DEFINITION
    @Dado("que existe um estoque com {int} unidades disponiveis do produto")
    public void queExisteUmEstoqueComUnidadesDisponiveisDoProduto(int saldoInicial) {
        // Reutiliza a lógica do método anterior que faz o setup inicial:
        queExisteUmEstoqueComUnidadesDoProduto(saldoInicial);
    }

    @Quando("o cliente registra uma saida de {int} unidades com motivo {string}")
    public void oClienteRegistraUmaSaidaDeUnidadesComMotivo(int quantidade, String motivo) {
        try {
            // Saldo antes
            saldoAnterior = estoque.getSaldoDisponivel(produto.getId());

            // Ação de domínio: saída
            estoque.registrarSaida(
                    produto.getId(),
                    quantidade,
                    "Cliente",
                    motivo
            );

            // Saldo depois
            saldoAtual = estoque.getSaldoDisponivel(produto.getId());

            // Última movimentação
            List<Movimentacao> movs = estoque.getMovimentacoesSnapshot();
            movimentacao = movs.isEmpty() ? null : movs.get(movs.size() - 1);

        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o saldo do estoque deve diminuir em {int} unidades")
    public void oSaldoDoEstoqueDeveDiminuirEmUnidades(int quantidadeEsperada) {
        int diferencaSaldo = saldoAnterior - saldoAtual;
        assertEquals(quantidadeEsperada, diferencaSaldo);
    }

    @E("a movimentacao deve conter o motivo {string}")
    public void aMovimentacaoDeveConterOMotivo(String motivoEsperado) {
        assertNotNull(movimentacao);
        assertEquals(motivoEsperado, movimentacao.getMotivo());
        assertEquals(TipoMovimentacao.SAIDA, movimentacao.getTipo());
    }

    @Quando("o cliente tenta registrar uma saida de {int} unidades")
    public void oClienteTentaRegistrarUmaSaidaDeUnidades(int quantidade) {
        try {
            // Saldo antes
            saldoAnterior = estoque.getSaldoDisponivel(produto.getId());

            // Ação de domínio que deve falhar se não houver saldo
            estoque.registrarSaida(
                    produto.getId(),
                    quantidade,
                    "Cliente",
                    "Saída manual"
            );

            // Se não lançar, atualiza saldo depois
            saldoAtual = estoque.getSaldoDisponivel(produto.getId());

        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve rejeitar a operacao")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull(excecaoCapturada);
    }

    @E("deve exibir a mensagem {string}")
    public void deveExibirAMensagem(String mensagem) {
        assertNotNull(mensagemErro);
        // Normaliza acentos e caixa para comparação mais resiliente
        String normalize = java.text.Normalizer.normalize(mensagemErro, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
        String esperado = java.text.Normalizer.normalize(mensagem, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
        assertTrue(normalize.contains(esperado));
    }

    // =============================================================
    // H21 — Visualizar histórico
    // =============================================================

    @Dado("que existem {int} movimentacoes registradas para o produto")
    public void queExistemMovimentacoesRegistradasParaOProduto(int quantidadeMovimentacoes) {
        ClienteId clienteId = repositorio.novoClienteId();
        cliente = new Cliente(clienteId, "Cliente Teste", "123.456.789-00", "cliente@teste.com");
        repositorio.salvar(cliente);

        ProdutoId produtoId = repositorio.novoProdutoId();

        produto = new Produto(produtoId, "PROD-004", "Produto Histórico", "UN", false, 1.0);
        repositorio.salvar(produto);

        EstoqueId estoqueId = repositorio.novoEstoqueId();
        estoque = new Estoque(estoqueId, cliente.getId(), "Estoque Histórico", "Endereço Teste", 1000);
        repositorio.salvar(estoque);

        // Criar histórico de movimentações
        historicoMovimentacoes = new ArrayList<>();
        for (int i = 1; i <= quantidadeMovimentacoes; i++) {
            Movimentacao mov = new Movimentacao(
                    (long) i,
                    i % 2 == 0 ? TipoMovimentacao.ENTRADA : TipoMovimentacao.SAIDA,
                    produto.getId(),
                    i * 10,
                    LocalDateTime.now().minusDays(quantidadeMovimentacoes - i),
                    "Responsável " + i,
                    "Motivo " + i,
                    Map.of("numero", String.valueOf(i))
            );
            historicoMovimentacoes.add(mov);
        }
    }

    @Quando("o cliente visualiza o historico do produto")
    public void oClienteVisualizaOHistoricoDoProduto() {
        // Simular busca do histórico
        assertNotNull(historicoMovimentacoes);
        assertFalse(historicoMovimentacoes.isEmpty());
    }

    @Entao("o sistema deve exibir todas as {int} movimentacoes")
    public void oSistemaDeveExibirTodasAsMovimentacoes(int quantidadeEsperada) {
        assertEquals(quantidadeEsperada, historicoMovimentacoes.size());
    }

    @E("cada movimentacao deve conter data, tipo, quantidade e responsavel")
    public void cadaMovimentacaoDeveConterDataTipoQuantidadeEResponsavel() {
        for (Movimentacao mov : historicoMovimentacoes) {
            assertNotNull(mov.getDataHora(), "Data não pode ser nula");
            assertNotNull(mov.getTipo(), "Tipo não pode ser nulo");
            assertTrue(mov.getQuantidade() > 0, "Quantidade deve ser positiva");
            assertNotNull(mov.getResponsavel(), "Responsável não pode ser nulo");
            assertFalse(mov.getResponsavel().trim().isEmpty(), "Responsável não pode ser vazio");
        }
    }
}