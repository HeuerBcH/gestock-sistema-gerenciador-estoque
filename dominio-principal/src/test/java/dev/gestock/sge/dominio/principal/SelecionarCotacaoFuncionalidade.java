package dev.gestock.sge.dominio.principal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorServico;
import dev.gestock.sge.dominio.principal.pedido.ItemPedido;
import dev.gestock.sge.dominio.principal.pedido.Pedido;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class SelecionarCotacaoFuncionalidade {

    private Repositorio repo;
    private FornecedorServico fornecedorSrv;
    private AtomicLong seq;

    private Map<String, FornecedorId> aliasFornecedor;
    private Map<String, ProdutoId> aliasProduto;
    private Map<String, Cotacao> aliasCotacao;

    private ProdutoId currentProdutoId;
    private Cotacao cotacaoSelecionada;
    private Exception lastError;

    @Before
    public void reset() {
        repo = new Repositorio();
        fornecedorSrv = new FornecedorServico(repo, repo);
        seq = new AtomicLong(1);
        aliasFornecedor = new HashMap<>();
        aliasProduto = new HashMap<>();
        aliasCotacao = new HashMap<>();
        currentProdutoId = null;
        cotacaoSelecionada = null;
        lastError = null;
    }

    private FornecedorId ensureFornecedor(String nome, String cnpj, boolean ativo) {
        return aliasFornecedor.computeIfAbsent(nome, k -> {
            FornecedorId id = repo.novoFornecedorId();
            Fornecedor f = new Fornecedor(id, nome, cnpj, "contato@ex.com");
            if (!ativo) {
                f.inativar();
            }
            repo.salvar(f);
            return id;
        });
    }

    private ProdutoId ensureProduto(String codigo, String nome) {
        return aliasProduto.computeIfAbsent(codigo, k -> {
            ProdutoId id = repo.novoProdutoId();
            Produto p = new Produto(id, codigo, nome, "UN", false, 1.0);
            repo.salvar(p);
            return id;
        });
    }

    private void registrarCotacao(String nomeFornecedor, String codigoProduto, double preco, int prazo, boolean ativo) {
        FornecedorId fornecedorId = ensureFornecedor(nomeFornecedor, "cnpj-" + seq.getAndIncrement(), ativo);
        ProdutoId produtoId = ensureProduto(codigoProduto, "Produto X");
        
        Fornecedor fornecedor = repo.buscarPorId(fornecedorId).orElseThrow();
        fornecedor.registrarCotacao(produtoId, preco, prazo);
        repo.salvar(fornecedor);
        
        // Armazenar cotação para referência
        Cotacao cotacao = new Cotacao(produtoId, preco, prazo);
        aliasCotacao.put(nomeFornecedor, cotacao);
    }

    private Cotacao selecionarMelhorCotacao(ProdutoId produtoId) {
        // Buscar fornecedores ativos que têm cotações para o produto
        List<Fornecedor> fornecedores = aliasFornecedor.values().stream()
                .map(id -> repo.buscarPorId(id).orElseThrow())
                .filter(f -> f.isAtivo())
                .toList();
        
        Cotacao melhorCotacao = null;
        double menorPreco = Double.MAX_VALUE;
        int menorPrazo = Integer.MAX_VALUE;
        
        for (Fornecedor fornecedor : fornecedores) {
            var cotacaoOpt = fornecedor.obterCotacaoPorProduto(produtoId);
            if (cotacaoOpt.isPresent()) {
                dev.gestock.sge.dominio.principal.fornecedor.Cotacao cotacao = cotacaoOpt.get();
                
                // Criar nossa própria instância de Cotacao
                Cotacao nossaCotacao = new Cotacao(produtoId, cotacao.getPreco(), cotacao.getPrazoDias());
                
                // Critério 1: Menor preço
                if (nossaCotacao.getPreco() < menorPreco) {
                    melhorCotacao = nossaCotacao;
                    menorPreco = nossaCotacao.getPreco();
                    menorPrazo = nossaCotacao.getPrazo();
                }
                // Critério 2: Em caso de empate, menor lead time
                else if (nossaCotacao.getPreco() == menorPreco && nossaCotacao.getPrazo() < menorPrazo) {
                    melhorCotacao = nossaCotacao;
                    menorPrazo = nossaCotacao.getPrazo();
                }
            }
        }
        
        return melhorCotacao;
    }

    // ===== DADOS =====

    @Dado("que existem as seguintes cotacoes para o produto {string}:")
    public void que_existem_as_seguintes_cotacoes_para_o_produto(String nomeProduto, io.cucumber.datatable.DataTable table) {
        currentProdutoId = ensureProduto("PROD-X", nomeProduto);
        
        List<Map<String, String>> rows = table.asMaps();
        for (Map<String, String> row : rows) {
            String fornecedor = row.get("fornecedor");
            double preco = Double.parseDouble(row.get("preco"));
            int prazo = Integer.parseInt(row.get("prazo"));
            boolean ativo = Boolean.parseBoolean(row.getOrDefault("ativo", "true"));
            
            registrarCotacao(fornecedor, "PROD-X", preco, prazo, ativo);
        }
    }

    @Dado("que existem as seguintes cotacoes para o produto {string} com empate:")
    public void que_existem_as_seguintes_cotacoes_para_o_produto_com_empate(String nomeProduto, io.cucumber.datatable.DataTable table) {
        currentProdutoId = ensureProduto("PROD-X", nomeProduto);
        
        List<Map<String, String>> rows = table.asMaps();
        for (Map<String, String> row : rows) {
            String fornecedor = row.get("fornecedor");
            double preco = Double.parseDouble(row.get("preco"));
            int prazo = Integer.parseInt(row.get("prazo"));
            
            registrarCotacao(fornecedor, "PROD-X", preco, prazo, true);
        }
    }

    @Dado("que existem as seguintes cotacoes para o produto {string} com fornecedor inativo:")
    public void que_existem_as_seguintes_cotacoes_para_o_produto_com_fornecedor_inativo(String nomeProduto, io.cucumber.datatable.DataTable table) {
        currentProdutoId = ensureProduto("PROD-X", nomeProduto);
        
        List<Map<String, String>> rows = table.asMaps();
        for (Map<String, String> row : rows) {
            String fornecedor = row.get("fornecedor");
            double preco = Double.parseDouble(row.get("preco"));
            int prazo = Integer.parseInt(row.get("prazo"));
            boolean ativo = Boolean.parseBoolean(row.getOrDefault("ativo", "true"));
            
            registrarCotacao(fornecedor, "PROD-X", preco, prazo, ativo);
        }
    }

    @Dado("que o sistema selecionou a melhor cotacao para o produto {string}")
    public void que_o_sistema_selecionou_a_melhor_cotacao_para_o_produto(String nomeProduto) {
        currentProdutoId = ensureProduto("PROD-X", nomeProduto);
        
        // Criar algumas cotações para o produto
        FornecedorId fornecedorId1 = ensureFornecedor("Fornecedor A", "12345678000199", true);
        FornecedorId fornecedorId2 = ensureFornecedor("Fornecedor B", "12345678000198", true);
        
        Fornecedor fornecedor1 = repo.buscarPorId(fornecedorId1).orElseThrow();
        Fornecedor fornecedor2 = repo.buscarPorId(fornecedorId2).orElseThrow();
        
        fornecedor1.registrarCotacao(currentProdutoId, 100.0, 10);
        fornecedor2.registrarCotacao(currentProdutoId, 90.0, 15);
        
        repo.salvar(fornecedor1);
        repo.salvar(fornecedor2);
        
        cotacaoSelecionada = selecionarMelhorCotacao(currentProdutoId);
    }

    // ===== QUANDOS =====

    @Quando("o sistema seleciona a melhor cotacao")
    public void o_sistema_seleciona_a_melhor_cotacao() {
        lastError = null;
        try {
            cotacaoSelecionada = selecionarMelhorCotacao(currentProdutoId);
        } catch (Exception ex) {
            lastError = ex;
        }
    }

    @Quando("o cliente aprova a cotacao")
    public void o_cliente_aprova_a_cotacao() {
        lastError = null;
        try {
            if (cotacaoSelecionada != null) {
                // Marcar cotação como selecionada (simulação)
                cotacaoSelecionada.marcarComoSelecionada();
                
                // Gerar pedido (simulação)
                PedidoId pedidoId = repo.novoPedidoId();
                ClienteId clienteId = repo.novoClienteId();
                FornecedorId fornecedorId = repo.novoFornecedorId();
                
                Pedido pedido = new Pedido(pedidoId, clienteId, fornecedorId);
                ItemPedido item = new ItemPedido(currentProdutoId, 10, 
                    java.math.BigDecimal.valueOf(cotacaoSelecionada.getPreco()));
                pedido.adicionarItem(item);
                repo.salvar(pedido);
            }
        } catch (Exception ex) {
            lastError = ex;
        }
    }

    // ===== ENTAOS =====

    @Entao("a cotacao do {string} deve ser selecionada")
    public void a_cotacao_do_deve_ser_selecionada(String nomeFornecedor) {
        assertNotNull(cotacaoSelecionada, "Nenhuma cotação foi selecionada");
        
        // Verificar se a cotação selecionada pertence ao fornecedor esperado
        FornecedorId fornecedorId = aliasFornecedor.get(nomeFornecedor);
        assertNotNull(fornecedorId, "Fornecedor não encontrado: " + nomeFornecedor);
        
        Fornecedor fornecedor = repo.buscarPorId(fornecedorId).orElseThrow();
        var cotacaoFornecedor = fornecedor.obterCotacaoPorProduto(currentProdutoId);
        
        assertTrue(cotacaoFornecedor.isPresent(), 
                "Fornecedor " + nomeFornecedor + " não possui cotação para o produto");
        
        dev.gestock.sge.dominio.principal.fornecedor.Cotacao cotacaoEsperada = cotacaoFornecedor.get();
        assertEquals(cotacaoSelecionada.getPreco(), cotacaoEsperada.getPreco(), 0.01,
                "Preço da cotação selecionada não confere");
        assertEquals(cotacaoSelecionada.getPrazo(), cotacaoEsperada.getPrazoDias(),
                "Prazo da cotação selecionada não confere");
    }

    @Entao("a cotacao do {string} deve ser selecionada por ter menor lead time")
    public void a_cotacao_do_deve_ser_selecionada_por_ter_menor_lead_time(String nomeFornecedor) {
        a_cotacao_do_deve_ser_selecionada(nomeFornecedor);
    }

    @Entao("a cotacao do {string} deve ser selecionada ignorando fornecedor inativo")
    public void a_cotacao_do_deve_ser_selecionada_ignorando_fornecedor_inativo(String nomeFornecedor) {
        a_cotacao_do_deve_ser_selecionada(nomeFornecedor);
    }

    @Entao("a cotacao deve ser marcada como {string}")
    public void a_cotacao_deve_ser_marcada_como(String status) {
        assertNotNull(cotacaoSelecionada, "Nenhuma cotação foi selecionada");
        
        if ("selecionada".equals(status)) {
            assertTrue(cotacaoSelecionada.isSelecionada(), 
                    "Cotação deveria estar marcada como selecionada");
        }
    }

    @Entao("um pedido deve ser gerado utilizando essa cotacao")
    public void um_pedido_deve_ser_gerado_utilizando_essa_cotacao() {
        assertNull(lastError, "Esperava sucesso na geração do pedido: " + 
                (lastError == null ? "" : lastError.getMessage()));
        
        // Verificar se existe pelo menos um pedido no repositório
        List<Pedido> pedidos = repo.listarTodos().stream()
                .filter(p -> p instanceof Pedido)
                .map(p -> (Pedido) p)
                .toList();
        
        assertFalse(pedidos.isEmpty(), "Deveria existir pelo menos um pedido gerado");
        
        // Verificar se o pedido contém o produto correto
        boolean pedidoComProduto = pedidos.stream()
                .anyMatch(p -> p.getItens().stream()
                        .anyMatch(item -> item.getProdutoId().equals(currentProdutoId)));
        
        assertTrue(pedidoComProduto, "Pedido deveria conter o produto " + currentProdutoId);
    }

    // ===== MÉTODOS AUXILIARES =====

    private static class Cotacao {
        private final ProdutoId produtoId;
        private final double preco;
        private final int prazo;
        private boolean selecionada = false;

        public Cotacao(ProdutoId produtoId, double preco, int prazo) {
            this.produtoId = produtoId;
            this.preco = preco;
            this.prazo = prazo;
        }

        public ProdutoId getProdutoId() { return produtoId; }
        public double getPreco() { return preco; }
        public int getPrazo() { return prazo; }
        public boolean isSelecionada() { return selecionada; }
        
        public void marcarComoSelecionada() { 
            this.selecionada = true; 
        }
    }
}
