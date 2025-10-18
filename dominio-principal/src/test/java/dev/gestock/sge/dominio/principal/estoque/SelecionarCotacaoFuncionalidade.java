package dev.gestock.sge.dominio.principal.estoque;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.gestock.sge.dominio.principal.AcervoFuncionalidade;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import io.cucumber.java.en.*;

public class SelecionarCotacaoFuncionalidade extends AcervoFuncionalidade {

    private Produto produto;
    private List<Cotacao> cotacoes = new ArrayList<>();
    private Cotacao cotacaoSelecionada;
    private RuntimeException excecao;

    // ============================================================
    // H18 – Seleção Automática
    // ============================================================

    @Given("que há cotações válidas para um produto em reposição")
    public void cotacoes_validas_para_produto() {
        produto = new Produto(new ProdutoId(1), "P001", "Produto Teste", "Unidade", null);
        produtoServico.salvar(produto);

        Fornecedor f1 = new Fornecedor(new FornecedorId(1), "Fornecedor A", 5);
        Fornecedor f2 = new Fornecedor(new FornecedorId(2), "Fornecedor B", 3);
        fornecedorServico.salvar(f1);
        fornecedorServico.salvar(f2);

        // Cria cotações válidas
        cotacoes.add(new Cotacao(produto, f1, 50.0, 5));
        cotacoes.add(new Cotacao(produto, f2, 50.0, 3)); // empate em preço, menor Lead Time
    }

    @When("o sistema executa a seleção automática")
    public void sistema_executa_selecao_automatica() {
        try {
            cotacaoSelecionada = cotacoes.stream()
                    .filter(c -> fornecedorServico.ativo(c.getFornecedor().getId()))
                    .min(Comparator.comparingDouble(Cotacao::getPreco)
                            .thenComparingInt(Cotacao::getLeadTime))
                    .orElseThrow(() -> new RuntimeException("Nenhuma cotação válida disponível"));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("ele deve escolher a cotação de menor preço válido, priorizando menor Lead Time em caso de empate")
    public void sistema_escolhe_menor_preco_menor_leadtime() {
        assertNotNull(cotacaoSelecionada);
        assertEquals(2, cotacaoSelecionada.getFornecedor().getId().getValor(), 
                     "Deve selecionar fornecedor com menor Lead Time em caso de empate");

        // Regras:
        // R1H18: Apenas cotações válidas e fornecedores ativos
        // R2H18: Empate → menor Lead Time
    }

    // ============================================================
    // H19 – Aprovar Cotação
    // ============================================================

    @Given("que uma cotação foi selecionada")
    public void cotacao_foi_selecionada() {
        if (cotacaoSelecionada == null) {
            // Seleção automática caso não tenha sido feita ainda
            sistema_executa_selecao_automatica();
        }
    }

    @When("o cliente aprova a cotação")
    public void cliente_aprova_cotacao() {
        try {
            cotacaoServico.aprovar(cotacaoSelecionada);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema registra a cotação como selecionada e associa ao pedido gerado")
    public void sistema_registra_cotacao_selecionada() {
        Cotacao c = cotacaoServico.obter(cotacaoSelecionada.getId());
        assertTrue(c.isSelecionada(), "Cotação deve estar marcada como selecionada");
        assertNotNull(pedidoServico.obterPorCotacao(c.getId()), "Pedido deve ser associado à cotação");

        // Regras:
        // R1H19: Cotação aprovada é vinculada ao pedido
    }
}
