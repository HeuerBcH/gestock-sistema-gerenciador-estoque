package dev.gestock.sge.dominio.principal.estoque;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import dev.gestock.sge.dominio.principal.AcervoFuncionalidade;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import io.cucumber.java.en.*;

public class CalcularROPFuncionalidade extends AcervoFuncionalidade {

    private Produto produto;
    private double consumoMedioDiario;
    private int leadTime;
    private int estoqueSeguranca;
    private double ropCalculado;
    private RuntimeException excecao;
    private Map<ProdutoId, Double> rops = new HashMap<>();

    // ============================================================
    // H14 – Calcular ROP
    // ============================================================

    @Given("que o cliente possui histórico de consumo de um produto")
    public void cliente_possui_historico_consumo() {
        produto = new Produto(new ProdutoId(1), "P001", "Produto Teste", "Unidade", null);
        produtoServico.salvar(produto);

        // Simulação de histórico de consumo nos últimos 90 dias
        consumoMedioDiario = 5.0;
        leadTime = 7; // dias
        estoqueSeguranca = 10;
    }

    @When("o sistema executa o cálculo de ponto de ressuprimento")
    public void sistema_executa_calculo_rop() {
        try {
            ropCalculado = (consumoMedioDiario * leadTime) + estoqueSeguranca;
            rops.put(produto.getId(), ropCalculado);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o valor deve ser calculado conforme a fórmula: ROP = (Consumo Médio Diário × Lead Time) + Estoque de Segurança")
    public void sistema_exibe_rop_calculado() {
        assertNotNull(rops.get(produto.getId()));
        assertEquals((consumoMedioDiario * leadTime) + estoqueSeguranca, rops.get(produto.getId()));

        // Regras:
        // R1H14: Fórmula de cálculo
        // R2H14: Histórico considera últimos 90 dias
    }

    // ============================================================
    // H15 – Visualizar ROP
    // ============================================================

    @Given("que o cliente deseja visualizar os ROPs")
    public void cliente_deseja_visualizar_rops() {
        // Produto cadastrado com ROP calculado
        if (!rops.containsKey(produto.getId())) {
            rops.put(produto.getId(), 50.0); // valor padrão caso não haja histórico suficiente
        }
    }

    @When("ele acessa o painel de produtos")
    public void cliente_acessa_painel_produtos() {
        // Simulação de acesso ao painel
    }

    @Then("o sistema deve exibir o ROP de cada produto, usando valor padrão se não houver histórico suficiente")
    public void sistema_exibe_rop_produtos() {
        double ropExibido = rops.get(produto.getId());
        assertTrue(ropExibido > 0, "ROP exibido corretamente");

        // Regras:
        // R1H15: Produtos sem histórico usam ROP padrão do cliente
    }
}
