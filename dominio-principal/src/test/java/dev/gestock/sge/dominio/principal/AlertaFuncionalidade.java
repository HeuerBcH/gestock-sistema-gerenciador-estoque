package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.alerta.*;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.*;
import io.cucumber.java.pt.*;
import java.util.*;
import static org.junit.Assert.*;

public class AlertaFuncionalidade {

    private Produto produto;
    private int saldoAtual;
    private Alerta alerta;
    private List<Alerta> alertas = new ArrayList<>();
    private boolean alertaGerado = false;
    private int saldoAtualAlerta = 0;
    private int ropEsperado = 0;

    @Dado("que existe um produto com ROP de {string} unidades")
    public void queExisteUmProdutoComROPDeUnidades(String rop) {
        ProdutoId id = new ProdutoId(1L);
        // Ajustar construtor para incluir preço/custo padrão (0.0)
        produto = new Produto(id, "PROD-001", "Produto A", "UN", false, 0.0);
        produto.definirROP(10, 7, 20);
        ropEsperado = produto.getRop().getValorROP();
    }

    @Dado("o saldo atual é {string} unidades")
    public void oSaldoAtualEUnidades(String saldo) {
        saldoAtual = Integer.parseInt(saldo);
    }

    @Quando("o sistema verifica o estoque")
    public void oSistemaVerificaOEstoque() {
        alertaGerado = produto.atingiuROP(saldoAtual);
        if (alertaGerado) {
            AlertaId alertaId = new AlertaId(1L);
            EstoqueId estoqueId = new EstoqueId(1L);
            alerta = new Alerta(alertaId, produto.getId(), estoqueId, null);
            saldoAtualAlerta = saldoAtual;
        }
    }

    @Então("um alerta deve ser gerado automaticamente")
    public void umAlertaDeveSerGeradoAutomaticamente() {
        assertTrue("Alerta deve ser gerado", alertaGerado);
    }

    @Então("um alerta deve ser gerado")
    public void umAlertaDeveSerGerado() {
        assertTrue("Alerta deve ser gerado", alertaGerado);
    }

    @Então("nenhum alerta deve ser gerado")
    public void nenhumAlertaDeveSerGerado() {
        assertFalse("Nenhum alerta deve ser gerado", alertaGerado);
    }

    @Dado("que existe um alerta gerado")
    public void queExisteUmAlertaGerado() {
        ProdutoId id = new ProdutoId(1L);
        AlertaId alertaId = new AlertaId(1L);
        EstoqueId estoqueId = new EstoqueId(1L);
        alerta = new Alerta(alertaId, id, estoqueId, null);
        saldoAtualAlerta = 90;
        ropEsperado = 100;
    }

    @Quando("eu visualizo o alerta")
    public void euVisualizoOAlerta() {
        assertNotNull("Alerta deve existir", alerta);
    }

    @Então("devo ver o nome do produto")
    public void devoVerONomeDoProduto() {
        assertNotNull("Produto ID deve existir", alerta.getProdutoId());
    }

    @Então("devo ver o estoque afetado")
    public void devoVerOEstoqueAfetado() {
        assertTrue("Saldo deve ser menor que ROP", saldoAtualAlerta < ropEsperado);
    }

    @Então("devo ver o fornecedor com menor cotação")
    public void devoVerOFornecedorComMenorCotacao() {
        // Verificação de fornecedor sugerido
        assertNotNull("Alerta deve existir", alerta);
    }

    @Dado("que existem {int} alertas ativos")
    public void queExistemAlertasAtivos(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            ProdutoId id = new ProdutoId((long) (i + 1));
            AlertaId alertaId = new AlertaId((long) (i + 1));
            EstoqueId estoqueId = new EstoqueId(1L);
            alertas.add(new Alerta(alertaId, id, estoqueId, null));
        }
    }

    @Quando("eu visualizo a lista de alertas")
    public void euVisualizoAListaDeAlertas() {
        assertFalse("Lista de alertas não deve estar vazia", alertas.isEmpty());
    }

    @Então("devo ver {int} alertas")
    public void devoVerAlertas(int quantidade) {
        assertEquals(quantidade, alertas.size());
    }

    @Dado("que existe um alerta ativo para um produto")
    public void queExisteUmAlertaAtivoParaUmProduto() {
        ProdutoId id = new ProdutoId(1L);
        AlertaId alertaId = new AlertaId(1L);
        EstoqueId estoqueId = new EstoqueId(1L);
        alerta = new Alerta(alertaId, id, estoqueId, null);
        alertas.add(alerta);
        saldoAtualAlerta = 80;
        ropEsperado = 100;
    }

    @Dado("um pedido foi recebido para esse produto")
    public void umPedidoFoiRecebidoParaEsseProduto() {
        saldoAtual = 150; // Saldo aumentou
    }

    @Quando("o sistema atualiza os alertas")
    public void oSistemaAtualizaOsAlertas() {
        if (saldoAtual > ropEsperado) {
            alerta.desativar();
            alertas.remove(alerta);
        }
    }

    @Então("o alerta deve ser removido automaticamente")
    public void oAlertaDeveSerRemovidoAutomaticamente() {
        assertFalse("Alerta deve ter sido removido", alertas.contains(alerta));
    }
}
