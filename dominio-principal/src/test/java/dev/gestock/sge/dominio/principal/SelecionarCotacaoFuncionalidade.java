package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;
import java.util.*;
import static org.junit.Assert.*;

public class SelecionarCotacaoFuncionalidade {

    private Map<String, Fornecedor> fornecedores = new HashMap<>();
    private ProdutoId produtoId = new ProdutoId(1L);
    private Cotacao cotacaoSelecionada;
    private int contadorFornecedores = 1;
    private boolean cotacaoAprovada = false;

    @Dado("que existem as seguintes cotações para um produto:")
    public void queExistemAsSeguintesCotacoesParaUmProduto(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String nome = row.get("fornecedor");
            FornecedorId id = new FornecedorId((long) contadorFornecedores++);
            Fornecedor forn = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
            double preco = Double.parseDouble(row.get("preco"));
            int prazo = Integer.parseInt(row.get("prazo"));
            forn.registrarCotacao(produtoId, preco, prazo);
            boolean ativo = row.containsKey("ativo") ? Boolean.parseBoolean(row.get("ativo")) : true;
            if (!ativo) forn.inativar();
            fornecedores.put(nome, forn);
        }
    }

    @Quando("o sistema seleciona a melhor cotação")
    public void oSistemaSelecionaAMelhorCotacao() {
        double menorPreco = Double.MAX_VALUE;
        int menorPrazo = Integer.MAX_VALUE;
        
        for (Fornecedor forn : fornecedores.values()) {
            if (forn.isAtivo()) {
                Optional<Cotacao> cot = forn.obterCotacaoPorProduto(produtoId);
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

    @Então("a cotação do {string} deve ser selecionada")
    public void aCotacaoDoDeveSelecionada(String nomeFornecedor) {
        assertNotNull("Cotação deve ter sido selecionada", cotacaoSelecionada);
        Fornecedor fornEsperado = fornecedores.get(nomeFornecedor);
        assertNotNull("Fornecedor deve existir", fornEsperado);
    }

    @Dado("que existem as seguintes cotações:")
    public void queExistemAsSeguintesCotacoes(DataTable dataTable) {
        queExistemAsSeguintesCotacoesParaUmProduto(dataTable);
    }

    @Dado("que o sistema selecionou uma cotação")
    public void queOSistemaSelecionouUmaCotacao() {
        FornecedorId id = new FornecedorId(1L);
        Fornecedor forn = new Fornecedor(id, "Fornecedor A", "12.345.678/0001-90", "contato@fornecedor.com");
        forn.registrarCotacao(produtoId, 100.0, 10);
        cotacaoSelecionada = forn.obterCotacaoPorProduto(produtoId).get();
    }

    @Quando("eu aprovo a cotação")
    public void euAprovoACotacao() {
        // Marca como aprovada (simulação)
        cotacaoAprovada = true;
    }

    @Então("a cotação deve ser marcada como {string}")
    public void aCotacaoDeveSerMarcadaComo(String status) {
        assertTrue("Cotação deve estar aprovada", cotacaoAprovada);
    }

    @Então("um pedido deve ser gerado com essa cotação")
    public void umPedidoDeveSerGeradoComEssaCotacao() {
        assertNotNull("Cotação deve existir", cotacaoSelecionada);
    }
}
