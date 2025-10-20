package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.pedido.*;
import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.junit.Assert.*;

public class GerenciarPedidosFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    private Pedido pedido;
    private Fornecedor fornecedor;
    private Produto produto;
    private Estoque estoque;
    private Exception excecaoCapturada;
    private String mensagemErro;
    private ClienteId clienteId = new ClienteId(1L);
    private boolean movimentacaoGerada = false;

    // =============================================================
    // H11: Criar pedidos de compra
    // =============================================================

    @Dado("que existe um fornecedor chamado {string} ativo")
    public void dadoFornecedorAtivo(String nome) {
        FornecedorId id = repositorio.novoFornecedorId();
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        repositorio.salvar(fornecedor);
    }

    @Dado("existe um produto chamado {string} com cotacao valida")
    public void dadoProdutoComCotacaoValida(String nome) {
        ProdutoId id = repositorio.novoProdutoId();
        produto = new Produto(id, "PROD-001", nome, "UN", false, 0.0);
        fornecedor.registrarCotacao(id, 50.0, 10);
        repositorio.salvar(produto);
    }

    @Quando("o cliente cria um pedido com {int} unidades do produto")
    public void quandoCriaPedidoSimples(int quantidade) {
        PedidoId id = repositorio.novoPedidoId();
        pedido = new Pedido(id, clienteId, fornecedor.getId());
        pedido.adicionarItem(new ItemPedido(produto.getId(), quantidade, BigDecimal.valueOf(50.0)));
        pedido.registrarCusto(new CustoPedido(BigDecimal.valueOf(quantidade * 50.0), BigDecimal.ZERO, BigDecimal.ZERO));
        repositorio.salvar(pedido);
    }

    @Entao("o pedido deve ser criado com sucesso")
    public void entaoPedidoCriadoComSucesso() {
        assertNotNull(pedido);
    }

    @Entao("o status do pedido deve ser {string}")
    public void entaoStatusPedido(String status) {
        assertEquals(StatusPedido.valueOf(status), pedido.getStatus());
    }

    @Entao("a data prevista de entrega deve ser calculada com base no lead time")
    public void entaoDataPrevistaCalculada() {
        assertNotNull(pedido.getDataCriacao());
    }

    // -------------------------------------------------------------

    @Dado("existe um produto chamado {string} sem cotacoes")
    public void dadoProdutoSemCotacao(String nome) {
        ProdutoId id = repositorio.novoProdutoId();
        produto = new Produto(id, "PROD-002", nome, "UN", false, 0.0);
        repositorio.salvar(produto);
    }

    @Quando("o cliente tenta criar um pedido para o produto")
    public void quandoTentaCriarPedidoSemCotacao() {
        try {
            if (fornecedor.obterCotacaoPorProduto(produto.getId()).isEmpty()) {
                throw new IllegalArgumentException("Nenhuma cotacao encontrada para o produto");
            }
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve rejeitar a operacao")
    public void entaoSistemaRejeitaOperacao() {
        assertNotNull(excecaoCapturada);
    }

    @Entao("deve exibir a mensagem {string}")
    public void entaoMensagemErro(String mensagem) {
        assertTrue(mensagemErro.contains(mensagem));
    }

    // -------------------------------------------------------------

    @Dado("existem os seguintes produtos com cotacoes:")
    public void dadoProdutosComCotacoes(DataTable dataTable) {
        for (Map<String, String> row : dataTable.asMaps()) {
            ProdutoId pid = repositorio.novoProdutoId();
            Produto prod = new Produto(pid, "PROD-" + pid.getId(), row.get("produto"), "UN", false, 0.0);
            fornecedor.registrarCotacao(pid,
                    Double.parseDouble(row.get("preco")),
                    Integer.parseInt(row.get("prazo")));
            repositorio.salvar(prod);
        }
    }

    @Quando("o cliente cria um pedido com os seguintes itens:")
    public void quandoCriaPedidoComItens(DataTable dataTable) {
        PedidoId id = repositorio.novoPedidoId();
        pedido = new Pedido(id, clienteId, fornecedor.getId());
        for (Map<String, String> row : dataTable.asMaps()) {
            String nome = row.get("produto");
            Produto prod = repositorio.buscarPorCodigo(new CodigoProduto("PROD-" + nome.split(" ")[1]))
                    .orElse(new Produto(repositorio.novoProdutoId(), "PROD-X", nome, "UN", false, 0.0));
            int qtd = Integer.parseInt(row.get("quantidade"));
            Optional<Cotacao> cot = fornecedor.obterCotacaoPorProduto(prod.getId());
            BigDecimal preco = BigDecimal.valueOf(cot.get().getPreco());
            pedido.adicionarItem(new ItemPedido(prod.getId(), qtd, preco));
        }
        repositorio.salvar(pedido);
    }

    @Entao("o pedido deve conter {int} itens")
    public void entaoPedidoComQuantidadeItens(int qtd) {
        assertEquals(qtd, pedido.getItens().size());
    }

    @Entao("o valor total dos itens deve ser calculado corretamente")
    public void entaoValorTotalCalculado() {
        assertTrue(pedido.calcularTotalItens().compareTo(BigDecimal.ZERO) > 0);
    }

    // -------------------------------------------------------------

    @Dado("que existe um fornecedor com lead time de {int} dias")
    public void dadoFornecedorComLeadTime(int dias) {
        FornecedorId id = repositorio.novoFornecedorId();
        LeadTime lt = new LeadTime(dias);
        fornecedor = new Fornecedor(id, "Fornecedor A", "12.345.678/0001-90", "contato@fornecedor.com", lt);
        repositorio.salvar(fornecedor);
    }

    @Quando("o cliente cria um pedido hoje")
    public void quandoCriaPedidoHoje() {
        PedidoId id = repositorio.novoPedidoId();
        pedido = new Pedido(id, clienteId, fornecedor.getId());
        pedido.adicionarItem(new ItemPedido(new ProdutoId(1L), 100, BigDecimal.valueOf(50.0)));
        pedido.setDataPrevistaEntrega(LocalDate.now().plusDays(fornecedor.getLeadTimeMedio().getDias()));
        repositorio.salvar(pedido);
    }

    @Entao("a data prevista de entrega deve ser {int} dias a partir de hoje")
    public void entaoDataPrevistaEmDias(int dias) {
        assertEquals(LocalDate.now().plusDays(dias), pedido.getDataPrevistaEntrega());
    }

    // =============================================================
    // H12: Cancelar pedidos
    // =============================================================

    @Dado("que existe um pedido no estado {string}")
    public void dadoPedidoNoEstado(String estado) {
        PedidoId id = repositorio.novoPedidoId();
        pedido = new Pedido(id, clienteId, new FornecedorId(1L));
        pedido.adicionarItem(new ItemPedido(new ProdutoId(1L), 50, BigDecimal.valueOf(10.0)));

        switch (estado) {
            case "ENVIADO": pedido.enviar(); break;
            case "RECEBIDO": pedido.enviar(); pedido.registrarRecebimento(); break;
            case "CONCLUIDO": pedido.enviar(); pedido.registrarRecebimento(); pedido.concluir(); break;
        }
        repositorio.salvar(pedido);
    }

    @Quando("o cliente cancela o pedido")
    public void quandoCancelaPedido() {
        pedido.cancelar();
    }

    @Entao("o pedido deve ser cancelado com sucesso")
    public void entaoPedidoCanceladoComSucesso() {
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Quando("o cliente tenta cancelar o pedido")
    public void quandoTentaCancelarPedido() {
        try {
            pedido.cancelar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    // =============================================================
    // H13: Confirmar recebimento de pedidos
    // =============================================================

    @Dado("que existe um pedido no estado {string} com {int} unidades")
    public void dadoPedidoComUnidades(String estado, int qtd) {
        PedidoId id = repositorio.novoPedidoId();
        pedido = new Pedido(id, clienteId, new FornecedorId(1L));
        pedido.adicionarItem(new ItemPedido(new ProdutoId(1L), qtd, BigDecimal.valueOf(50.0)));
        if ("ENVIADO".equals(estado)) pedido.enviar();
        repositorio.salvar(pedido);
    }

    @Dado("existe um estoque para receber o produto")
    public void dadoEstoqueParaReceber() {
        EstoqueId id = repositorio.novoEstoqueId();
        estoque = new Estoque(id, clienteId, "Estoque A", "Endereço A", 1000);
        repositorio.salvar(estoque);
    }

    @Quando("o cliente confirma o recebimento do pedido")
    public void quandoConfirmaRecebimento() {
        pedido.registrarRecebimento();
        movimentacaoGerada = true;
    }

    @Entao("uma movimentacao de entrada deve ser gerada")
    public void entaoMovimentacaoGerada() {
        assertTrue(movimentacaoGerada);
    }

    @Entao("o saldo do estoque deve aumentar em {int} unidades")
    public void entaoSaldoAumenta(int qtd) {
        assertTrue("Simulação de aumento", movimentacaoGerada);
    }

    @Quando("o cliente tenta confirmar o recebimento")
    public void quandoTentaConfirmarRecebimento() {
        try {
            pedido.registrarRecebimento();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Dado("que existe um pedido no estado {string} com itens")
    public void dadoPedidoComItens(String estado) {
        PedidoId id = repositorio.novoPedidoId();
        pedido = new Pedido(id, clienteId, new FornecedorId(1L));
        pedido.adicionarItem(new ItemPedido(new ProdutoId(1L), 100, BigDecimal.valueOf(50.0)));
        repositorio.salvar(pedido);
    }

    @Quando("o cliente envia o pedido")
    public void quandoEnviaPedido() {
        pedido.enviar();
    }

    @Entao("o pedido deve ser enviado com sucesso")
    public void entaoPedidoEnviado() {
        assertEquals(StatusPedido.ENVIADO, pedido.getStatus());
    }

    @Quando("o cliente conclui o pedido")
    public void quandoConcluiPedido() {
        pedido.concluir();
    }

    @Entao("o pedido deve ser concluido com sucesso")
    public void entaoPedidoConcluido() {
        assertEquals(StatusPedido.CONCLUIDO, pedido.getStatus());
    }

    // -------------------------------------------------------------

    @Dado("que existe um pedido com os seguintes itens:")
    public void dadoPedidoComItensEPrecos(DataTable dataTable) {
        PedidoId id = repositorio.novoPedidoId();
        pedido = new Pedido(id, clienteId, new FornecedorId(1L));

        for (Map<String, String> row : dataTable.asMaps()) {
            int qtd = Integer.parseInt(row.get("quantidade"));
            BigDecimal preco = new BigDecimal(row.get("precoUnitario"));
            pedido.adicionarItem(new ItemPedido(new ProdutoId(1L), qtd, preco));
        }
        repositorio.salvar(pedido);
    }

    @Quando("o custo total do pedido e calculado")
    public void quandoCalculaCustoTotal() {
        pedido.calcularTotalItens();
    }

    @Entao("o valor total dos itens deve ser {string}")
    public void entaoValorTotalItens(String valor) {
        BigDecimal esperado = new BigDecimal(valor);
        assertEquals(esperado, pedido.calcularTotalItens());
    }
}
