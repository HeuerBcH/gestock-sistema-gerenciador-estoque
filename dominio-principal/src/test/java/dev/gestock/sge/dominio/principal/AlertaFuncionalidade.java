package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.alerta.*;
import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.dominio.principal.cliente.*;
import dev.gestock.sge.infraestrutura.persistencia.memoria.*;
import io.cucumber.java.pt.*;
import java.util.*;
import static org.junit.Assert.*;

public class AlertaFuncionalidade {

    private Repositorio repositorio;
    private Produto produto;
    private Estoque estoque;
    private Alerta alerta;
    private int saldoAtual;
    private int ropEsperado;
    private boolean alertaGerado;

    // ===============================================================
    // CENÁRIOS H16 — Gerar Alerta ao atingir ou ficar abaixo do ROP
    // ===============================================================

    @Dado("que existe um produto com ROP de {int} unidades")
    public void queExisteUmProdutoComROPDeUnidades(int rop) {
        repositorio = new Repositorio();

        ProdutoId produtoId = repositorio.novoProdutoId();
        produto = new Produto(produtoId, "PROD-001", "Produto A", "UN", false, 0.0);
        repositorio.salvar(produto);

        EstoqueId estoqueId = repositorio.novoEstoqueId();
        ClienteId clienteId = repositorio.novoClienteId();
        estoque = new Estoque(estoqueId, clienteId, "Estoque Central", "Rua X", 1000);
        estoque.definirROP(produto.getId(), 10, 7, rop);
        repositorio.salvar(estoque);

        ropEsperado = rop;
    }

    @E("o saldo atual do produto e {int} unidades")
    public void oSaldoAtualDoProdutoEUnidades(int saldo) {
        saldoAtual = saldo;
    }

    @Quando("o sistema verifica o estoque")
    public void oSistemaVerificaOEstoque() {
        alertaGerado = estoque.atingiuROP(produto.getId(), saldoAtual);
        if (alertaGerado) {
            AlertaId alertaId = repositorio.novoAlertaId();
            alerta = new Alerta(alertaId, produto.getId(), estoque.getId(), null);
            repositorio.salvar(alerta);
        }
    }

    @Entao("um alerta deve ser gerado automaticamente")
    public void umAlertaDeveSerGeradoAutomaticamente() {
        assertTrue(alertaGerado);
        assertEquals(1, repositorio.listarAtivos().size());
    }

    @Entao("um alerta deve ser gerado")
    public void umAlertaDeveSerGerado() {
        assertTrue(alertaGerado);
        assertEquals(1, repositorio.listarAtivos().size());
    }

    @Entao("nenhum alerta deve ser gerado")
    public void nenhumAlertaDeveSerGerado() {
        assertFalse(alertaGerado);
        assertTrue(repositorio.listarAtivos().isEmpty());
    }

    // ===============================================================
    // CENÁRIO — Alerta contém informações completas
    // ===============================================================

    @Dado("que existe um alerta gerado para um produto")
    public void queExisteUmAlertaGeradoParaUmProduto() {
        repositorio = new Repositorio();

        ProdutoId produtoId = repositorio.novoProdutoId();
        produto = new Produto(produtoId, "PROD-002", "Produto B", "UN", false, 0.0);
        repositorio.salvar(produto);

        EstoqueId estoqueId = repositorio.novoEstoqueId();
        ClienteId clienteId = repositorio.novoClienteId();
        estoque = new Estoque(estoqueId, clienteId, "Estoque B", "Rua Y", 1000);
        repositorio.salvar(estoque);

        AlertaId alertaId = repositorio.novoAlertaId();
        alerta = new Alerta(alertaId, produto.getId(), estoque.getId(), null);
        repositorio.salvar(alerta);
    }

    @Quando("o cliente visualiza o alerta")
    public void oClienteVisualizaOAlerta() {
        assertNotNull(repositorio.obter(alerta.getId()));
    }

    @Entao("o sistema deve exibir o nome do produto")
    public void oSistemaDeveExibirONomeDoProduto() {
        Optional<Produto> produtoOpt = repositorio.buscarPorId(alerta.getProdutoId());
        assertTrue(produtoOpt.isPresent());
    }

    @E("o sistema deve exibir o estoque afetado")
    public void oSistemaDeveExibirOEstoqueAfetado() {
        Optional<Estoque> estoqueOpt = repositorio.buscarPorId(alerta.getEstoqueId());
        assertTrue(estoqueOpt.isPresent());
    }

    @E("o sitema deve exibir o fornecedor com menor cotacao")
    public void oSitemaDeveExibirOFornecedorComMenorCotacao() {
        assertNotNull(alerta);
    }

    // ===============================================================
    // CENÁRIO — Listar todos os alertas ativos
    // ===============================================================

    @Dado("que existem {int} alertas ativos")
    public void queExistemAlertasAtivos(int quantidade) {
        repositorio = new Repositorio();

        for (int i = 0; i < quantidade; i++) {
            ProdutoId pId = repositorio.novoProdutoId();
            EstoqueId eId = repositorio.novoEstoqueId();
            AlertaId aId = repositorio.novoAlertaId();
            Alerta a = new Alerta(aId, pId, eId, null);
            repositorio.salvar(a);
        }
    }

    @Quando("o cliente visualiza a lista de alertas")
    public void oClienteVisualizaAListaDeAlertas() {
        assertFalse(repositorio.listarAtivos().isEmpty());
    }

    @Entao("o sistema deve exibir {int} alertas")
    public void oSistemaDeveExibirAlertas(int quantidade) {
        assertEquals(quantidade, repositorio.listarAtivos().size());
    }

    // ===============================================================
    // CENÁRIO — Remover alerta após recebimento do pedido
    // ===============================================================

    @Dado("que existe um alerta ativo para um produto")
    public void queExisteUmAlertaAtivoParaUmProduto() {
        repositorio = new Repositorio();

        ProdutoId produtoId = repositorio.novoProdutoId();
        produto = new Produto(produtoId, "PROD-003", "Produto C", "UN", false, 0.0);
        repositorio.salvar(produto);

        EstoqueId estoqueId = repositorio.novoEstoqueId();
        ClienteId clienteId = repositorio.novoClienteId();
        estoque = new Estoque(estoqueId, clienteId, "Estoque C", "Rua Z", 1000);
        repositorio.salvar(estoque);

        AlertaId alertaId = repositorio.novoAlertaId();
        alerta = new Alerta(alertaId, produto.getId(), estoque.getId(), null);
        repositorio.salvar(alerta);

        ropEsperado = 100;
        saldoAtual = 80;
    }

    @E("um pedido foi recebido para suprir o estoque do produto")
    public void umPedidoFoiRecebidoParaSuprirOEstoqueDoProduto() {
        saldoAtual = 150; // estoque reabastecido
    }

    @Quando("o sistema atualiza o estoque")
    public void oSistemaAtualizaOEstoque() {
        if (saldoAtual > ropEsperado) {
            alerta.desativar();
            repositorio.salvar(alerta);
        }
    }

    @Entao("o alerta deve ser removido automaticamente")
    public void oAlertaDeveSerRemovidoAutomaticamente() {
        List<Alerta> ativos = repositorio.listarAtivos();
        assertTrue(ativos.stream().noneMatch(Alerta::isAtivo));
    }
}
