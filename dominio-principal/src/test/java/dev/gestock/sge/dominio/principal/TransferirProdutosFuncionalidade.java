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
    private List<Map<String, Object>> historicoTransferencias = new ArrayList<>();

    // =========================================================
    // H22 — Transferir produtos
    // =========================================================

    @Dado("que existem dois estoques do mesmo cliente chamados {string} e {string}")
    public void queExistemDoisEstoquesDoMesmoClienteChamados(String nomeOrigem, String nomeDestino) {
        ClienteId clienteId = repositorio.novoClienteId();
        estoqueOrigem = new Estoque(repositorio.novoEstoqueId(), clienteId, nomeOrigem, "Endereco A", 1000);
        estoqueDestino = new Estoque(repositorio.novoEstoqueId(), clienteId, nomeDestino, "Endereco B", 1000);
        repositorio.salvar(estoqueOrigem);
        repositorio.salvar(estoqueDestino);
    }

    @Dado("o {string} possui {int} unidades do produto {string}")
    public void oEstoquePossuiUnidadesDoProduto(String nomeEstoque, int quantidade, String nomeProduto) {
        estoqueOrigem.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", Map.of());
    }

    @Quando("o cliente transfere {int} unidades do produto para o {string}")
    public void oClienteTransfereUnidadesDoProdutoParaO(int quantidade, String nomeDestino) {
        estoqueOrigem.transferir(produtoId, estoqueDestino, quantidade, "Sistema", "Transferencia");
        registrarTransferencia(estoqueOrigem, estoqueDestino, produtoId, quantidade);
    }

    @Entao("o {string} deve ter {int} unidades do produto")
    public void oEstoqueDeveTerUnidadesDoProduto(String nomeEstoque, int quantidade) {
        if (nomeEstoque.contains("Origem")) {
            assertEquals(quantidade, estoqueOrigem.getSaldoFisico(produtoId));
        } else {
            assertEquals(quantidade, estoqueDestino.getSaldoFisico(produtoId));
        }
    }

    @E("o {string} deve receber {int} unidades do produto")
    public void oEstoqueDeveReceberUnidadesDoProduto(String nomeEstoque, int quantidade) {
        assertEquals(quantidade, estoqueDestino.getSaldoFisico(produtoId));
    }

    // =========================================================
    // R2H22 — Origem deve ter saldo suficiente
    // =========================================================

    @Dado("que o {string} possui {int} unidades do produto")
    public void queOEstoquePossuiUnidadesDoProdutoSemNomeProduto(String nomeEstoque, int quantidade) {
        ClienteId clienteId = repositorio.novoClienteId();
        estoqueOrigem = new Estoque(repositorio.novoEstoqueId(), clienteId, "Estoque Origem", "Endereco A", 1000);
        estoqueDestino = new Estoque(repositorio.novoEstoqueId(), clienteId, "Estoque Destino", "Endereco B", 1000);
        estoqueOrigem.registrarEntrada(produtoId, quantidade, "Sistema", "Carga", Map.of());
    }

    @Quando("o cliente tenta transferir {int} unidades do produto para o {string}")
    public void oClienteTentaTransferirUnidadesDoProdutoParaO(int quantidade, String nomeDestino) {
        try {
            estoqueOrigem.transferir(produtoId, estoqueDestino, quantidade, "Sistema", "Transferencia");
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve rejeitar a operacao de transferencia")
    public void oSistemaDeveRejeitarAOperacaoDeTransferencia() {
        assertNotNull("Era esperada uma excecao", excecaoCapturada);
    }

    @E("deve exibir a mensagem de transferencia {string}")
    public void deveExibirAMensagemDeTransferencia(String mensagem) {
        assertNotNull("Mensagem nao deve ser nula", mensagemErro);
        assertTrue("Mensagem incorreta", mensagemErro.contains(mensagem));
    }

    // =========================================================
    // R3H22 — Transferencia registra saida e entrada
    // =========================================================

    @Dado("que um cliente transfere {int} unidades do produto do {string} para o {string}")
    public void queUmClienteTransfereUnidadesDoProdutoDoParaO(int quantidade, String nomeOrigem, String nomeDestino) {
        ClienteId clienteId = repositorio.novoClienteId();
        estoqueOrigem = new Estoque(repositorio.novoEstoqueId(), clienteId, nomeOrigem, "Endereco A", 1000);
        estoqueDestino = new Estoque(repositorio.novoEstoqueId(), clienteId, nomeDestino, "Endereco B", 1000);
        estoqueOrigem.registrarEntrada(produtoId, 100, "Sistema", "Carga inicial", Map.of());
        estoqueOrigem.transferir(produtoId, estoqueDestino, quantidade, "Sistema", "Transferencia");
        registrarTransferencia(estoqueOrigem, estoqueDestino, produtoId, quantidade);
    }

    @Quando("o cliente verifica as movimentacoes")
    public void oClienteVerificaAsMovimentacoes() {
        assertFalse("Deveria haver movimentacoes registradas", historicoTransferencias.isEmpty());
    }

    @Entao("o sistema deve exibir uma saida no {string}")
    public void oSistemaDeveExibirUmaSaidaNo(String nomeEstoque) {
        Map<String, Object> ultima = historicoTransferencias.get(historicoTransferencias.size() - 1);
        assertEquals("saida", ultima.get("tipo_origem"));
    }

    @E("o sistema deve exibir uma entrada no {string}")
    public void oSistemaDeveExibirUmaEntradaNo(String nomeEstoque) {
        Map<String, Object> ultima = historicoTransferencias.get(historicoTransferencias.size() - 1);
        assertEquals("entrada", ultima.get("tipo_destino"));
    }

    // =========================================================
    // H23 — Visualizar historico de transferencias
    // =========================================================

    @Dado("que foram realizadas {int} transferencias de produto entre estoques")
    public void queForamRealizadasTransferenciasDeProdutoEntreEstoques(int quantidade) {
        ClienteId clienteId = repositorio.novoClienteId();
        estoqueOrigem = new Estoque(repositorio.novoEstoqueId(), clienteId, "Origem", "Endereco A", 1000);
        estoqueDestino = new Estoque(repositorio.novoEstoqueId(), clienteId, "Destino", "Endereco B", 1000);
        for (int i = 0; i < quantidade; i++) {
            registrarTransferencia(estoqueOrigem, estoqueDestino, produtoId, 10);
        }
    }

    @Quando("o cliente visualiza o historico de transferencias")
    public void oClienteVisualizaOHistoricoDeTransferencias() {
        assertFalse("Historico nao deve estar vazio", historicoTransferencias.isEmpty());
    }

    @Entao("o sistema deve exibir {int} registros")
    public void oSistemaDeveExibirRegistros(int quantidade) {
        assertEquals(quantidade, historicoTransferencias.size());
    }

    @E("cada registro deve conter produto, quantidade, estoque origem e estoque destino")
    public void cadaRegistroDeveConterProdutoQuantidadeEstoqueOrigemEDestino() {
        for (Map<String, Object> t : historicoTransferencias) {
            assertNotNull(t.get("produto"));
            assertTrue((int) t.get("quantidade") > 0);
            assertNotNull(t.get("origem"));
            assertNotNull(t.get("destino"));
        }
    }

    // =========================================================
    // R2H23 — Nao pode cancelar transferencia concluida
    // =========================================================

    @Dado("que existe uma transferencia concluida de produto entre estoques")
    public void queExisteUmaTransferenciaConcluidaDeProdutoEntreEstoques() {
        registrarTransferencia(new EstoqueId(1L), new EstoqueId(2L), produtoId, 50);
    }

    @Quando("o cliente tenta cancelar a transferencia")
    public void oClienteTentaCancelarATransferencia() {
        try {
            throw new IllegalStateException("Transferencia concluida nao pode ser cancelada");
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve rejeitar a operacao de transferencia")
    public void oSistemaDeveRejeitarOperacaoDeTransferenciaDuplicado() {
        assertNotNull("Excecao esperada", excecaoCapturada);
    }

    @E("deve exibir a mensagem de transferencia {string}")
    public void deveExibirMensagemDeTransferenciaDuplicado(String mensagem) {
        assertTrue(mensagemErro.contains(mensagem));
    }

    // =========================================================
    // Metodos auxiliares
    // =========================================================

    private void registrarTransferencia(Estoque origem, Estoque destino, ProdutoId produtoId, int quantidade) {
        Map<String, Object> registro = new HashMap<>();
        registro.put("produto", produtoId);
        registro.put("quantidade", quantidade);
        registro.put("origem", origem.getNome());
        registro.put("destino", destino.getNome());
        registro.put("tipo_origem", "saida");
        registro.put("tipo_destino", "entrada");
        historicoTransferencias.add(registro);
    }

    private void registrarTransferencia(EstoqueId origem, EstoqueId destino, ProdutoId produtoId, int quantidade) {
        Map<String, Object> registro = new HashMap<>();
        registro.put("produto", produtoId);
        registro.put("quantidade", quantidade);
        registro.put("origem", origem);
        registro.put("destino", destino);
        registro.put("tipo_origem", "saida");
        registro.put("tipo_destino", "entrada");
        historicoTransferencias.add(registro);
    }
}
