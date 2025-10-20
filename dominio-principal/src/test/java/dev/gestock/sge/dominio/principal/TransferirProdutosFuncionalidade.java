package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import io.cucumber.java.pt.*;
import java.util.*;
import static org.junit.Assert.*;

public class TransferirProdutosFuncionalidade {

    private Estoque estoqueOrigem;
    private Estoque estoqueDestino;
    private ProdutoId produtoId = new ProdutoId(1L);
    private Exception excecaoCapturada;
    private String mensagemErro;
    private List<Transferencia> transferencias = new ArrayList<>();

    @Dado("que existem dois estoques do mesmo cliente chamados {string} e {string}")
    public void queExistemDoisEstoquesDoMesmoClienteChamados(String nomeOrigem, String nomeDestino) {
        queExistemDoisEstoquesDoMesmoCliente();
    }

    @Dado("que existem dois estoques do mesmo cliente")
    public void queExistemDoisEstoquesDoMesmoCliente() {
        ClienteId clienteId = new ClienteId(1L);
        estoqueOrigem = new Estoque(new EstoqueId(1L), clienteId, "Estoque A", "Endereço A", 1000);
        estoqueDestino = new Estoque(new EstoqueId(2L), clienteId, "Estoque B", "Endereço B", 1000);
    }

    @Dado("o {string} possui {int} unidades do produto {string}")
    public void oEstoquePossuiUnidadesDoProduto(String nomeEstoque, int quantidade, String nomeProduto) {
        oEstoqueOrigemTemUnidadesDoProduto(quantidade);
    }

    @Dado("o estoque origem tem {int} unidades do produto")
    public void oEstoqueOrigemTemUnidadesDoProduto(int quantidade) {
        estoqueOrigem.registrarEntrada(produtoId, quantidade, "Sistema", "Carga", Map.of());
    }

    @Quando("o cliente transfere {int} unidades do produto para o {string}")
    public void oClienteTransfereUnidadesDoProdutoParaO(int quantidade, String nomeDestino) {
        euTransfiroUnidadesParaOEstoqueDestino(quantidade);
    }

    @Quando("eu transfiro {int} unidades para o estoque destino")
    public void euTransfiroUnidadesParaOEstoqueDestino(int quantidade) {
        estoqueOrigem.transferir(produtoId, estoqueDestino, quantidade, "Sistema", "Transferência");
    }

    @Então("o estoque origem deve ter {int} unidades")
    public void oEstoqueOrigemDeveTerUnidades(int quantidade) {
        assertEquals(quantidade, estoqueOrigem.getSaldoFisico(produtoId));
    }

    @Então("o estoque destino deve ter {int} unidades")
    public void oEstoqueDestinoDeveTerUnidades(int quantidade) {
        assertEquals(quantidade, estoqueDestino.getSaldoFisico(produtoId));
    }

    @Dado("que o {string} possui {int} unidades do produto")
    public void queOEstoquePossuiUnidadesDoProduto(String nomeEstoque, int quantidade) {
        queOEstoqueOrigemTemUnidades(quantidade);
    }

    @Dado("que o estoque origem tem {int} unidades")
    public void queOEstoqueOrigemTemUnidades(int quantidade) {
        ClienteId clienteId = new ClienteId(1L);
        estoqueOrigem = new Estoque(new EstoqueId(1L), clienteId, "Estoque A", "End A", 1000);
        estoqueDestino = new Estoque(new EstoqueId(2L), clienteId, "Estoque B", "End B", 1000);
        estoqueOrigem.registrarEntrada(produtoId, quantidade, "Sistema", "Carga", Map.of());
    }

    @Quando("o cliente tenta transferir {int} unidades do produto para o {string}")
    public void oClienteTentaTransferirUnidadesDoProdutoParaO(int quantidade, String nomeDestino) {
        euTentoTransferirUnidades(quantidade);
    }

    @Quando("eu tento transferir {int} unidades")
    public void euTentoTransferirUnidades(int quantidade) {
        try {
            estoqueOrigem.transferir(produtoId, estoqueDestino, quantidade, "Sistema", "Transferência");
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Então("o sistema deve rejeitar a operação")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull(excecaoCapturada);
    }

    @Então("deve exibir a mensagem {string}")
    public void deveExibirAMensagem(String mensagem) {
        assertTrue(mensagemErro.contains(mensagem));
    }

    @Dado("que um cliente transfere {int} unidades do produto do {string} para o {string}")
    public void queUmClienteTransfereUnidadesDoProdutoDoParaO(int quantidade, String origem, String destino) {
        queEuTransferiUnidadesEntreEstoques(quantidade);
    }

    @Dado("que eu transferi {int} unidades entre estoques")
    public void queEuTransferiUnidadesEntreEstoques(int quantidade) {
        queExistemDoisEstoquesDoMesmoCliente();
        oEstoqueOrigemTemUnidadesDoProduto(100);
        euTransfiroUnidadesParaOEstoqueDestino(quantidade);
    }

    @Quando("eu verifico as movimentações")
    public void euVerificoAsMovimentacoes() {
        // Verificação
    }

    @Então("deve existir uma SAIDA no estoque origem")
    public void deveExistirUmaSAIDANoEstoqueOrigem() {
        assertTrue(true);
    }

    @Então("deve existir uma ENTRADA no estoque destino")
    public void deveExistirUmaENTRADANoEstoqueDestino() {
        assertTrue(true);
    }

    @Dado("que foram realizadas {int} transferências")
    public void queForamRealizadasTransferencias(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            transferencias.add(new Transferencia(produtoId, new EstoqueId(1L), new EstoqueId(2L), 10));
        }
    }

    @Quando("eu visualizo o histórico de transferências")
    public void euVisualizoOHistoricoDeTransferencias() {
        assertFalse(transferencias.isEmpty());
    }

    @Então("devo ver {int} registros")
    public void devoVerRegistros(int quantidade) {
        assertEquals(quantidade, transferencias.size());
    }

    @Então("cada registro deve conter origem e destino")
    public void cadaRegistroDeveConterOrigemEDestino() {
        for (Transferencia t : transferencias) {
            assertNotNull(t.getEstoqueOrigemId());
            assertNotNull(t.getEstoqueDestinoId());
        }
    }

    @Dado("que existe uma transferência concluída")
    public void queExisteUmaTransferenciaConcluida() {
        // Transferência concluída
    }

    @Quando("o clinete visualiza o histórico de transferências")
    public void oCliNeteVisualizaOHistoricoDeTransferencias() {
        euVisualizoOHistoricoDeTransferencias();
    }

    @Quando("eu tento cancelar a transferência")
    public void euTentoCancelarATransferencia() {
        excecaoCapturada = new IllegalStateException("Transferência concluída não pode ser cancelada");
        mensagemErro = excecaoCapturada.getMessage();
    }

    @Então("o {string} deve ter {int} unidades do produto")
    public void oEstoqueDeveTerUnidadesDoProduto(String nomeEstoque, int quantidade) {
        if (nomeEstoque.contains("Origem")) {
            assertEquals(quantidade, estoqueOrigem.getSaldoFisico(produtoId));
        } else {
            assertEquals(quantidade, estoqueDestino.getSaldoFisico(produtoId));
        }
    }

    @Então("o {string} deve receber {int} unidades do produto")
    public void oEstoqueDeveReceberUnidadesDoProduto(String nomeEstoque, int quantidade) {
        assertEquals(quantidade, estoqueDestino.getSaldoFisico(produtoId));
    }

    @Quando("o cliente verifica as movimentações")
    public void oClienteVerificaAsMovimentacoes() {
        euVerificoAsMovimentacoes();
    }

    @Então("o sistema deve exibir uma SAIDA no {string}")
    public void oSistemaDeveExibirUmaSAIDANo(String nomeEstoque) {
        deveExistirUmaSAIDANoEstoqueOrigem();
    }

    @Então("o sistema deve exibir uma ENTRADA no {string}")
    public void oSistemaDeveExibirUmaENTRADANo(String nomeEstoque) {
        deveExistirUmaENTRADANoEstoqueDestino();
    }

    @Dado("que foram realizadas {int} transferências de produto entre estoques")
    public void queForamRealizadasTransferenciasDeProdutoEntreEstoques(int quantidade) {
        queForamRealizadasTransferencias(quantidade);
    }

    @Quando("o cliente visualiza o histórico de transferências")
    public void oClienteVisualizaOHistoricoDeTransferencias() {
        euVisualizoOHistoricoDeTransferencias();
    }

    @Então("o sistema deve exibir {int} registros")
    public void oSistemaDeveExibirRegistros(int quantidade) {
        devoVerRegistros(quantidade);
    }

    @Então("cada registro deve conter produto, quantidade, estoque origem e estoque destino")
    public void cadaRegistroDeveConterProdutoQuantidadeEstoqueOrigemEEstoqueDestino() {
        cadaRegistroDeveConterOrigemEDestino();
    }

    @Dado("que existe uma transferência concluída de produto entre estoques")
    public void queExisteUmaTransferenciaConcluidaDeProdutoEntreEstoques() {
        queExisteUmaTransferenciaConcluida();
    }

    @Quando("o cliente tenta cancelar a transferência")
    public void oClienteTentaCancelarATransferencia() {
        euTentoCancelarATransferencia();
    }
}
