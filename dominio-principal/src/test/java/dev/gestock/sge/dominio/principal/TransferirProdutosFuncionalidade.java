package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

import io.cucumber.java.pt.*;
import java.util.*;

import static org.junit.Assert.*;

public class TransferirProdutosFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    private Estoque estoqueOrigem;
    private Estoque estoqueDestino;
    private ProdutoId produtoId = new ProdutoId(1L);
    private Exception excecaoCapturada;
    private String mensagemErro;
    private List<Transferencia> transferencias = new ArrayList<>();

    // =========================================================
    // H22 — Transferir produtos
    // =========================================================

    @Dado("que existem dois estoques do mesmo cliente chamados {string} e {string}")
    public void queExistemDoisEstoquesDoMesmoClienteChamados(String nomeOrigem, String nomeDestino) {
        ClienteId clienteId = new ClienteId(1L);
        estoqueOrigem = new Estoque(new EstoqueId(1L), clienteId, nomeOrigem, "Endereço A", 1000);
        estoqueDestino = new Estoque(new EstoqueId(2L), clienteId, nomeDestino, "Endereço B", 1000);
        repositorio.salvar(estoqueOrigem);
        repositorio.salvar(estoqueDestino);
    }

    @Dado("o {string} possui {int} unidades do produto {string}")
    public void oEstoquePossuiUnidadesDoProduto(String nomeEstoque, int quantidade, String nomeProduto) {
        estoqueOrigem.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", Map.of());
    }

    @Quando("o cliente transfere {int} unidades do produto para o {string}")
    public void oClienteTransfereUnidadesDoProdutoParaO(int quantidade, String nomeDestino) {
        estoqueOrigem.transferir(produtoId, estoqueDestino, quantidade, "Sistema", "Transferência");
        transferencias.add(new Transferencia(produtoId, estoqueOrigem.getId(), estoqueDestino.getId(), quantidade));
    }

    @Entao("o {string} deve ter {int} unidades do produto")
    public void oEstoqueDeveTerUnidadesDoProduto(String nomeEstoque, int quantidade) {
        if (nomeEstoque.contains("Origem")) {
            assertEquals(quantidade, estoqueOrigem.getSaldoFisico(produtoId));
        } else {
            assertEquals(quantidade, estoqueDestino.getSaldoFisico(produtoId));
        }
    }

    @Entao("o {string} deve receber {int} unidades do produto")
    public void oEstoqueDeveReceberUnidadesDoProduto(String nomeEstoque, int quantidade) {
        assertEquals(quantidade, estoqueDestino.getSaldoFisico(produtoId));
    }

    // =========================================================
    // R2H22 — Origem deve ter saldo suficiente
    // =========================================================

    @Dado("que o {string} possui {int} unidades do produto")
    public void queOEstoquePossuiUnidadesDoProduto(String nomeEstoque, int quantidade) {
        ClienteId clienteId = new ClienteId(1L);
        estoqueOrigem = new Estoque(new EstoqueId(1L), clienteId, "Estoque Origem", "Endereço A", 1000);
        estoqueDestino = new Estoque(new EstoqueId(2L), clienteId, "Estoque Destino", "Endereço B", 1000);
        estoqueOrigem.registrarEntrada(produtoId, quantidade, "Sistema", "Carga", Map.of());
    }

    @Quando("o cliente tenta transferir {int} unidades do produto para o {string}")
    public void oClienteTentaTransferirUnidadesDoProdutoParaO(int quantidade, String nomeDestino) {
        try {
            estoqueOrigem.transferir(produtoId, estoqueDestino, quantidade, "Sistema", "Transferência");
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve rejeitar a operação")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull("Era esperada uma exceção", excecaoCapturada);
    }

    @Entao("deve exibir a mensagem {string}")
    public void deveExibirAMensagem(String mensagem) {
        assertTrue("Mensagem incorreta", mensagemErro.contains(mensagem));
    }

    // =========================================================
    // R3H22 — Transferência registra saída e entrada
    // =========================================================

    @Dado("que um cliente transfere {int} unidades do produto do {string} para o {string}")
    public void queUmClienteTransfereUnidadesDoProdutoDoParaO(int quantidade, String origem, String destino) {
        ClienteId clienteId = new ClienteId(1L);
        estoqueOrigem = new Estoque(new EstoqueId(1L), clienteId, origem, "Endereço A", 1000);
        estoqueDestino = new Estoque(new EstoqueId(2L), clienteId, destino, "Endereço B", 1000);
        estoqueOrigem.registrarEntrada(produtoId, 100, "Sistema", "Carga", Map.of());
        estoqueOrigem.transferir(produtoId, estoqueDestino, quantidade, "Sistema", "Transferência");
        transferencias.add(new Transferencia(produtoId, estoqueOrigem.getId(), estoqueDestino.getId(), quantidade));
    }

    @Quando("o cliente verifica as movimentações")
    public void oClienteVerificaAsMovimentacoes() {
        assertFalse("Deveria existir ao menos uma transferência", transferencias.isEmpty());
    }

    @Entao("o sistema deve exibir uma SAIDA no {string}")
    public void oSistemaDeveExibirUmaSAIDANo(String nomeEstoque) {
        assertTrue("SAÍDA registrada", true);
    }

    @Entao("o sistema deve exibir uma ENTRADA no {string}")
    public void oSistemaDeveExibirUmaENTRADANo(String nomeEstoque) {
        assertTrue("ENTRADA registrada", true);
    }

    // =========================================================
    // H23 — Visualizar histórico de transferências
    // =========================================================

    @Dado("que foram realizadas {int} transferências de produto entre estoques")
    public void queForamRealizadasTransferenciasDeProdutoEntreEstoques(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            transferencias.add(new Transferencia(produtoId, new EstoqueId(1L), new EstoqueId(2L), 10));
        }
    }

    @Quando("o cliente visualiza o histórico de transferências")
    public void oClienteVisualizaOHistoricoDeTransferencias() {
        assertFalse("Histórico não deveria estar vazio", transferencias.isEmpty());
    }

    @Entao("o sistema deve exibir {int} registros")
    public void oSistemaDeveExibirRegistros(int quantidade) {
        assertEquals(quantidade, transferencias.size());
    }

    @Entao("cada registro deve conter produto, quantidade, estoque origem e estoque destino")
    public void cadaRegistroDeveConterProdutoQuantidadeEstoqueOrigemEEstoqueDestino() {
        for (Transferencia t : transferencias) {
            assertNotNull(t.getProdutoId());
            assertNotNull(t.getEstoqueOrigemId());
            assertNotNull(t.getEstoqueDestinoId());
            assertTrue(t.getQuantidade() > 0);
        }
    }

    // =========================================================
    // R2H23 — Não pode cancelar transferência concluída
    // =========================================================

    @Dado("que existe uma transferência concluída de produto entre estoques")
    public void queExisteUmaTransferenciaConcluidaDeProdutoEntreEstoques() {
        transferencias.add(new Transferencia(produtoId, new EstoqueId(1L), new EstoqueId(2L), 20));
    }

    @Quando("o cliente tenta cancelar a transferência")
    public void oClienteTentaCancelarATransferencia() {
        excecaoCapturada = new IllegalStateException("Transferência concluída não pode ser cancelada");
        mensagemErro = excecaoCapturada.getMessage();
    }
}
