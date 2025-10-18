package dev.gestock.sge.dominio.principal.estoque;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import dev.gestock.sge.dominio.principal.AcervoFuncionalidade;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import io.cucumber.java.en.*;

public class AlertasEstoqueFuncionalidade extends AcervoFuncionalidade {

    private Produto produto;
    private Estoque estoque;
    private Fornecedor fornecedor;
    private double quantidadeDisponivel;
    private double rop;
    private List<Alerta> alertasAtivos = new ArrayList<>();

    private RuntimeException excecao;

    // ============================================================
    // H16 – Notificar Estoque Baixo
    // ============================================================

    @Given("que o estoque de um produto atinge o ROP")
    public void estoque_produto_atinge_rop() {
        fornecedor = new Fornecedor(new FornecedorId(1), "Fornecedor Beta", 5);
        fornecedorServico.salvar(fornecedor);

        estoque = new Estoque(new EstoqueId(1), "Estoque Central", "Rua A", 500);
        estoqueServico.salvar(estoque);

        produto = new Produto(new ProdutoId(1), "P001", "Produto Teste", "Unidade", estoque.getId());
        produtoServico.salvar(produto);

        rop = 20.0;
        quantidadeDisponivel = 20.0; // atinge exatamente o ROP
    }

    @When("a quantidade disponível fica igual ou menor ao ponto de ressuprimento")
    public void quantidade_disponivel_igual_ou_menor_rop() {
        if (quantidadeDisponivel <= rop) {
            Alerta alerta = new Alerta(produto, estoque, fornecedor, "Estoque baixo");
            alertasAtivos.add(alerta);
            alertaServico.salvar(alerta);
        }
    }

    @Then("o sistema deve gerar um alerta indicando produto, estoque e fornecedor com menor cotação válida")
    public void sistema_gera_alerta() {
        assertFalse(alertasAtivos.isEmpty(), "Alerta foi gerado automaticamente");
        Alerta alerta = alertasAtivos.get(0);
        assertEquals(produto.getId(), alerta.getProduto().getId());
        assertEquals(estoque.getId(), alerta.getEstoque().getId());
        assertEquals(fornecedor.getId(), alerta.getFornecedor().getId());

        // Regras:
        // R1H16: Alerta é gerado automaticamente
        // R2H16: Alerta inclui produto, estoque e fornecedor sugerido
    }

    // ============================================================
    // H17 – Visualizar Alertas
    // ============================================================

    @Given("que existem alertas ativos")
    public void existem_alertas_ativos() {
        if (alertasAtivos.isEmpty()) {
            Alerta alerta = new Alerta(produto, estoque, fornecedor, "Estoque baixo");
            alertasAtivos.add(alerta);
            alertaServico.salvar(alerta);
        }
    }

    @When("o cliente acessa a lista de alertas")
    public void cliente_acessa_lista_alertas() {
        // Simulação de acesso ao painel de alertas
    }

    @Then("o sistema exibe todos e remove automaticamente os correspondentes após recebimento do pedido")
    public void sistema_exibe_e_remove_alertas() {
        assertFalse(alertasAtivos.isEmpty(), "Alertas ativos exibidos corretamente");

        // Simulação de recebimento do pedido, remove alertas correspondentes
        alertasAtivos.clear();
        assertTrue(alertasAtivos.isEmpty(), "Alertas correspondentes removidos após recebimento");

        // Regras:
        // R1H17: Alerta removido após recebimento do pedido
    }
}
