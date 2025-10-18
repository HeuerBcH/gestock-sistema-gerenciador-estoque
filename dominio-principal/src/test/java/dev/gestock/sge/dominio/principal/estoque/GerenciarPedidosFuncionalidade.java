package dev.gestock.sge.dominio.principal.estoque;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import dev.gestock.sge.dominio.principal.AcervoFuncionalidade;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import io.cucumber.java.en.*;

public class GerenciarPedidosFuncionalidade extends AcervoFuncionalidade {

    private Map<Integer, Pedido> pedidos = new HashMap<>();
    private Pedido pedido;
    private Produto produto;
    private Estoque estoque;
    private Fornecedor fornecedor;
    private RuntimeException excecao;

    // ============================================================
    // H11 – Criar Pedido
    // ============================================================

    @Given("que o cliente deseja reabastecer o estoque")
    public void cliente_deseja_reabastecer_estoque() {
        fornecedor = new Fornecedor(new FornecedorId(1), "Fornecedor Alpha", 5);
        fornecedorServico.salvar(fornecedor);

        estoque = new Estoque(new EstoqueId(1), "Estoque Central", "Rua A", 500);
        estoqueServico.salvar(estoque);

        produto = new Produto(new ProdutoId(1), "P001", "Produto Teste", "Unidade", estoque.getId());
        produtoServico.salvar(produto);
    }

    @When("ele cria um novo pedido com base em uma cotação válida")
    public void cliente_cria_pedido_com_cotacao_valida() {
        try {
            pedido = new Pedido(new PedidoId(1), produto, fornecedor, 10);
            pedidoServico.salvar(pedido);
            pedidos.put(pedido.getId().getValor(), pedido);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve registrar o pedido com data prevista de entrega baseada no Lead Time")
    public void sistema_registra_pedido_com_data_prevista() {
        assertNotNull(pedido);
        assertEquals(10, pedido.getQuantidade());
        assertTrue(pedido.getDataPrevista().isAfter(pedido.getDataCriacao()));

        // Regras:
        // R1H11: Pedido só pode ser criado com cotação válida
        // R2H11: Data prevista é calculada pelo Lead Time do fornecedor
    }

    // ============================================================
    // H12 – Cancelar Pedido
    // ============================================================

    @Given("que o cliente possui pedidos pendentes")
    public void cliente_possui_pedidos_pendentes() {
        pedido = new Pedido(new PedidoId(2), produto, fornecedor, 5);
        pedidoServico.salvar(pedido);
    }

    @When("ele solicita o cancelamento")
    public void cliente_solicita_cancelamento() {
        try {
            pedidoServico.cancelar(pedido.getId());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve permitir apenas se o pedido ainda não estiver Em transporte")
    public void sistema_valida_cancelamento() {
        if (pedido.getStatus() == StatusPedido.EM_TRANSPORTE) {
            assertNotNull(excecao, "Não deve permitir cancelar pedidos em transporte");
        } else {
            assertEquals(StatusPedido.CANCELADO, pedidoServico.obter(pedido.getId()).getStatus());
        }

        // Regras:
        // R1H12: Pedidos em transporte não podem ser cancelados
    }

    // ============================================================
    // H13 – Confirmar Recebimento
    // ============================================================

    @Given("que o pedido foi entregue")
    public void pedido_foi_entregue() {
        pedido = new Pedido(new PedidoId(3), produto, fornecedor, 15);
        pedido.setStatus(StatusPedido.ENTREGUE);
        pedidoServico.salvar(pedido);
    }

    @When("o cliente confirma o recebimento")
    public void cliente_confirma_recebimento() {
        pedidoServico.confirmarRecebimento(pedido.getId());
    }

    @Then("o sistema deve atualizar o estoque e registrar uma movimentação de entrada automaticamente")
    public void sistema_atualiza_estoque_e_registra_movimentacao() {
        var estoqueAtual = estoqueServico.obter(estoque.getId());
        assertEquals(15, estoqueAtual.getQuantidadeProduto(produto.getId()));
        assertTrue(movimentacaoServico.existeEntrada(pedido.getId()));

        // Regras:
        // R1H13: Recebimento gera movimentação de entrada automaticamente
    }
}
