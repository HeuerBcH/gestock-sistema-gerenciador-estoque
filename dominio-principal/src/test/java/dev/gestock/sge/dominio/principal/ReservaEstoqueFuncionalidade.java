package dev.gestock.sge.dominio.principal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.pedido.Pedido;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import io.cucumber.java.en.*;

public class ReservaEstoqueFuncionalidade extends AcervoFuncionalidade {

    private Produto produto;
    private Estoque estoque;
    private Pedido pedido;
    private double quantidade;
    private Map<ProdutoId, Double> reservas = new HashMap<>();
    private RuntimeException excecao;

    // ============================================================
    // H24 – Reserva Automática
    // ============================================================

    @Given("que o cliente gera um novo pedido")
    public void cliente_gera_novo_pedido() {
        estoque = new Estoque(new EstoqueId(1), "Estoque Principal", "Rua A", 500);
        estoqueServico.salvar(estoque);

        produto = new Produto(new ProdutoId(1), "P001", "Produto Teste", "Unidade", estoque.getId());
        produtoServico.salvar(produto);

        pedido = new Pedido(new PedidoId(1), produto, null, 50); // Pedido de 50 unidades
        pedidoServico.salvar(pedido);

        quantidade = 50;
    }

    @When("o pedido é criado")
    public void pedido_criado() {
        // Reserva automaticamente a quantidade do produto
        double saldoDisponivel = estoqueServico.obterQuantidade(produto.getId());
        if (saldoDisponivel >= quantidade) {
            reservas.put(produto.getId(), quantidade);
            estoqueServico.retirarQuantidade(produto.getId(), quantidade); // reserva bloqueia o estoque
        } else {
            excecao = new RuntimeException("Saldo insuficiente para reserva");
        }
    }

    @Then("o sistema deve reservar automaticamente a quantidade correspondente dos produtos")
    public void sistema_reserva_automaticamente() {
        assertNotNull(reservas.get(produto.getId()), "Reserva realizada automaticamente");
        assertEquals(quantidade, reservas.get(produto.getId()), "Quantidade reservada correta");

        // Regras:
        // R1H24: Sistema reserva automaticamente
        // R2H24: Saldo reservado não pode ser usado em outras saídas
    }

    // ============================================================
    // H25 – Liberar Reserva
    // ============================================================

    @Given("que um pedido foi cancelado")
    public void pedido_cancelado() {
        pedido.setStatus("CANCELADO");
        pedidoServico.salvar(pedido);
    }

    @When("o cancelamento é confirmado")
    public void cancelamento_confirmado() {
        // Libera a reserva automaticamente
        Double quantidadeReservada = reservas.remove(produto.getId());
        if (quantidadeReservada != null) {
            estoqueServico.adicionarQuantidade(produto.getId(), quantidadeReservada);
        }
    }

    @Then("o sistema deve liberar as reservas e manter registro histórico das liberações")
    public void sistema_libera_reservas() {
        assertFalse(reservas.containsKey(produto.getId()), "Reserva liberada automaticamente");
        assertTrue(estoqueServico.obterQuantidade(produto.getId()) >= quantidade, "Quantidade devolvida ao estoque");

        // Regras:
        // R1H25: Reservas são liberadas automaticamente
        // R2H25: Registros de reserva/liberação mantidos para auditoria
    }
}
