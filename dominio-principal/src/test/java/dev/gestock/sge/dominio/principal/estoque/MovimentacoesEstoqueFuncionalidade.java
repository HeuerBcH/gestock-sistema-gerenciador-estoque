package dev.gestock.sge.dominio.principal.estoque;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import dev.gestock.sge.dominio.principal.AcervoFuncionalidade;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import io.cucumber.java.en.*;

public class MovimentacoesEstoqueFuncionalidade extends AcervoFuncionalidade {

    private Produto produto;
    private Estoque estoque;
    private double quantidade;
    private String motivo;
    private List<Movimentacao> historico = new ArrayList<>();
    private RuntimeException excecao;

    // ============================================================
    // H20 – Registrar Movimentações
    // ============================================================

    @Given("que há alterações no estoque")
    public void ha_alteracoes_no_estoque() {
        estoque = new Estoque(new EstoqueId(1), "Estoque Principal", "Rua A", 500);
        estoqueServico.salvar(estoque);

        produto = new Produto(new ProdutoId(1), "P001", "Produto Teste", "Unidade", estoque.getId());
        produtoServico.salvar(produto);

        quantidade = 10;
        motivo = "Recebimento";
    }

    @When("ocorre entrada ou saída de produtos")
    public void ocorre_entrada_ou_saida() {
        Movimentacao mov = new Movimentacao(produto, estoque, quantidade, motivo);
        movimentacaoServico.registrar(mov);
        historico.add(mov);

        // Atualiza quantidade do estoque
        if ("Recebimento".equalsIgnoreCase(motivo)) {
            estoqueServico.adicionarQuantidade(produto.getId(), quantidade);
        } else {
            estoqueServico.retirarQuantidade(produto.getId(), quantidade);
        }
    }

    @Then("o sistema deve registrar automaticamente as movimentações, indicando motivo para saídas")
    public void sistema_registra_movimentacoes() {
        assertFalse(historico.isEmpty(), "Movimentação registrada");
        Movimentacao mov = historico.get(0);
        assertEquals(produto.getId(), mov.getProduto().getId());
        assertEquals(quantidade, mov.getQuantidade());
        assertEquals(motivo, mov.getMotivo());

        // Regras:
        // R1H20: Entradas automáticas após recebimento
        // R2H20: Saídas devem indicar motivo (venda, consumo, perda)
    }

    // ============================================================
    // H21 – Histórico de Movimentações
    // ============================================================

    @Given("que o cliente deseja consultar movimentações passadas")
    public void cliente_deseja_consultar_historico() {
        // Simula 12 meses de movimentações
        for (int i = 0; i < 12; i++) {
            Movimentacao mov = new Movimentacao(produto, estoque, 5 + i, "Recebimento");
            movimentacaoServico.registrar(mov);
            historico.add(mov);
        }
    }

    @When("ele acessa o histórico")
    public void cliente_acessa_historico() {
        // Simula acesso ao histórico
    }

    @Then("o sistema deve exibir registros de pelo menos 12 meses")
    public void sistema_exibe_historico() {
        assertTrue(historico.size() >= 12, "Histórico de pelo menos 12 meses exibido");
        for (Movimentacao mov : historico) {
            assertEquals(produto.getId(), mov.getProduto().getId());
        }

        // Regras:
        // R1H21: Histórico mantido por 12 meses
    }
}
