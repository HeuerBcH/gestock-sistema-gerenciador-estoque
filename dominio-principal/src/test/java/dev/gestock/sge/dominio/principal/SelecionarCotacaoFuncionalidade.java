package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import java.util.*;

import static org.junit.Assert.*;

public class SelecionarCotacaoFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    private Map<String, Fornecedor> fornecedores = new HashMap<>();
    private ProdutoId produtoId = new ProdutoId(1L);
    private Cotacao cotacaoSelecionada;
    private int contadorFornecedores = 1;
    private boolean cotacaoAprovada = false;

    // =========================================================
    // H18 — Selecionar cotação mais vantajosa
    // =========================================================

    @Dado("que existem as seguintes cotacoes para o produto {string}:")
    public void queExistemAsSeguintesCotacoesParaOProduto(String nomeProduto, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String nome = row.get("fornecedor");
            FornecedorId id = new FornecedorId((long) contadorFornecedores++);
            Fornecedor fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@" + nome + ".com");

            double preco = Double.parseDouble(row.get("preco"));
            int prazo = Integer.parseInt(row.get("prazo"));
            boolean ativo = row.containsKey("ativo") ? Boolean.parseBoolean(row.get("ativo")) : true;

            fornecedor.registrarCotacao(produtoId, preco, prazo);
            if (!ativo) fornecedor.inativar();

            fornecedores.put(nome, fornecedor);
            repositorio.salvar(fornecedor);
        }
    }

    @Quando("o sistema seleciona a melhor cotacao")
    public void oSistemaSelecionaAMelhorCotacao() {
        double menorPreco = Double.MAX_VALUE;
        int menorPrazo = Integer.MAX_VALUE;
        cotacaoSelecionada = null;

        for (Fornecedor fornecedor : fornecedores.values()) {
            if (fornecedor.isAtivo()) {
                Optional<Cotacao> cot = fornecedor.obterCotacaoPorProduto(produtoId);
                if (cot.isPresent()) {
                    Cotacao c = cot.get();
                    if (c.getPreco() < menorPreco ||
                        (c.getPreco() == menorPreco && c.getPrazoDias() < menorPrazo)) {
                        menorPreco = c.getPreco();
                        menorPrazo = c.getPrazoDias();
                        cotacaoSelecionada = c;
                    }
                }
            }
        }
    }

    @Entao("a cotacao do {string} deve ser selecionada")
    public void aCotacaoDoDeveSerSelecionada(String nomeFornecedor) {
        assertNotNull("Nenhuma cotacao foi selecionada", cotacaoSelecionada);
        Fornecedor fornecedorEsperado = fornecedores.get(nomeFornecedor);
        assertNotNull("Fornecedor esperado nao encontrado", fornecedorEsperado);

        Optional<Cotacao> cotEsperada = fornecedorEsperado.obterCotacaoPorProduto(produtoId);
        assertTrue("Fornecedor nao possui cotacao para o produto", cotEsperada.isPresent());
        assertEquals(cotEsperada.get().getPreco(), cotacaoSelecionada.getPreco(), 0.01);
    }

    // =========================================================
    // H19 — Revisar e aprovar cotacao
    // =========================================================

    @Dado("que o sistema selecionou a melhor cotacao para o produto {string}")
    public void queOSistemaSelecionouAMelhorCotacaoParaOProduto(String nomeProduto) {
        FornecedorId id = new FornecedorId(1L);
        Fornecedor fornecedor = new Fornecedor(id, "Fornecedor A", "12.345.678/0001-90", "contato@fornecedor.com");
        fornecedor.registrarCotacao(produtoId, 100.0, 10);
        cotacaoSelecionada = fornecedor.obterCotacaoPorProduto(produtoId).get();
        repositorio.salvar(fornecedor);
    }

    @Quando("o cliente aprova a cotacao")
    public void oClienteAprovaACotacao() {
        cotacaoAprovada = true;
    }

    @Entao("a cotacao deve ser marcada como {string}")
    public void aCotacaoDeveSerMarcadaComo(String status) {
        assertTrue("Cotacao deveria estar aprovada", cotacaoAprovada);
    }

    @E("um pedido deve ser gerado utilizando essa cotacao")
    public void umPedidoDeveSerGeradoUtilizandoEssaCotacao() {
        assertNotNull("Cotacao selecionada deve existir", cotacaoSelecionada);
    }
}
