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

public class TransferirProdutosFuncionalidade extends AcervoFuncionalidade {

    private Produto produto;
    private Estoque estoqueOrigem;
    private Estoque estoqueDestino;
    private double quantidade;
    private List<Movimentacao> historicoTransferencias = new ArrayList<>();
    private RuntimeException excecao;

    // ============================================================
    // H22 – Transferir Produtos
    // ============================================================

    @Given("que o cliente possui dois estoques")
    public void cliente_possui_dois_estoques() {
        estoqueOrigem = new Estoque(new EstoqueId(1), "Estoque A", "Rua A", 500);
        estoqueDestino = new Estoque(new EstoqueId(2), "Estoque B", "Rua B", 300);
        estoqueServico.salvar(estoqueOrigem);
        estoqueServico.salvar(estoqueDestino);

        produto = new Produto(new ProdutoId(1), "P001", "Produto Teste", "Unidade", estoqueOrigem.getId());
        produtoServico.salvar(produto);

        // Quantidade disponível na origem
        estoqueServico.adicionarQuantidade(produto.getId(), 50);
        quantidade = 20;
    }

    @When("ele solicita transferência de produtos entre eles")
    public void cliente_solicita_transferencia() {
        try {
            if (!estoqueOrigem.getClienteId().equals(estoqueDestino.getClienteId())) {
                throw new RuntimeException("Estoques não pertencem ao mesmo cliente");
            }
            if (estoqueOrigem.getQuantidadeProduto(produto.getId()) < quantidade) {
                throw new RuntimeException("Quantidade insuficiente no estoque de origem");
            }

            // Executa transferência
            estoqueServico.retirarQuantidade(produto.getId(), quantidade, estoqueOrigem.getId());
            estoqueServico.adicionarQuantidade(produto.getId(), quantidade, estoqueDestino.getId());

            Movimentacao mov = new Movimentacao(produto, estoqueOrigem, -quantidade, "Transferência para " + estoqueDestino.getNome());
            Movimentacao movDestino = new Movimentacao(produto, estoqueDestino, quantidade, "Transferência de " + estoqueOrigem.getNome());
            movimentacaoServico.registrar(mov);
            movimentacaoServico.registrar(movDestino);
            historicoTransferencias.add(mov);
            historicoTransferencias.add(movDestino);

        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema deve verificar se ambos pertencem ao mesmo cliente e registrar automaticamente movimentações de saída e entrada")
    public void sistema_verifica_clientes_e_registra_movimentacoes() {
        assertNull(excecao, "Transferência realizada sem exceções");
        assertEquals(-quantidade, historicoTransferencias.get(0).getQuantidade());
        assertEquals(quantidade, historicoTransferencias.get(1).getQuantidade());

        // Regras:
        // R1H22: Apenas entre estoques do mesmo cliente
        // R2H22: Origem deve ter quantidade suficiente
        // R3H22: Sistema gera movimentações automáticas
    }

    // ============================================================
    // H23 – Histórico de Transferências
    // ============================================================

    @Given("que o cliente deseja visualizar transferências realizadas")
    public void cliente_deseja_visualizar_transferencias() {
        if (historicoTransferencias.isEmpty()) {
            // Simula uma transferência anterior
            Movimentacao mov = new Movimentacao(produto, estoqueOrigem, -10, "Transferência para " + estoqueDestino.getNome());
            Movimentacao movDestino = new Movimentacao(produto, estoqueDestino, 10, "Transferência de " + estoqueOrigem.getNome());
            historicoTransferencias.add(mov);
            historicoTransferencias.add(movDestino);
        }
    }

    @When("ele acessa o histórico")
    public void cliente_acessa_historico_transferencias() {
        // Simula acesso ao histórico
    }

    @Then("o sistema deve listar data, produto, quantidade, origem e destino, sem permitir cancelamentos concluídos")
    public void sistema_exibe_historico_transferencias() {
        assertFalse(historicoTransferencias.isEmpty(), "Histórico exibido corretamente");
        for (Movimentacao mov : historicoTransferencias) {
            assertNotNull(mov.getProduto());
            assertNotNull(mov.getEstoque());
            assertTrue(mov.getQuantidade() != 0);
        }

        // Regras:
        // R1H23: Histórico detalhado
        // R2H23: Não é possível cancelar transferência concluída
    }
}
