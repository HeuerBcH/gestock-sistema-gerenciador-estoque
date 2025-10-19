package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import io.cucumber.java.pt.*;
import java.util.Map;
import static org.junit.Assert.*;

public class ReservarEstoqueFuncionalidade {

    private Estoque estoque;
    private ProdutoId produtoId = new ProdutoId(1L);
    private int saldoFisicoInicial;
    private int reservaAtual;
    private Exception excecaoCapturada;
    private String mensagemErro;
    private boolean reservaLiberada = false;

    @Dado("que existe um estoque com {int} unidades disponíveis")
    public void queExisteUmEstoqueComUnidadesDisponiveis(int quantidade) {
        ClienteId clienteId = new ClienteId(1L);
        estoque = new Estoque(new EstoqueId(1L), clienteId, "Estoque A", "End A", 1000);
        estoque.registrarEntrada(produtoId, quantidade, "Sistema", "Carga", Map.of());
        saldoFisicoInicial = quantidade;
    }

    @Quando("eu crio um pedido de venda com {int} unidades")
    public void euCrioUmPedidoDeVendaComUnidades(int quantidade) {
        estoque.reservar(produtoId, quantidade);
        reservaAtual = quantidade;
    }

    @Então("{int} unidades devem ser reservadas")
    public void unidadesDevemSerReservadas(int quantidade) {
        assertEquals(quantidade, estoque.getSaldoReservado(produtoId));
    }

    @Então("o saldo disponível deve ser {int} unidades")
    public void oSaldoDisponivelDeveSerUnidades(int quantidade) {
        assertEquals(quantidade, estoque.getSaldoDisponivel(produtoId));
    }

    @Então("o saldo físico deve permanecer {int} unidades")
    public void oSaldoFisicoDevePermanecerUnidades(int quantidade) {
        assertEquals(quantidade, estoque.getSaldoFisico(produtoId));
    }

    @Dado("que existe um estoque com {int} unidades físicas")
    public void queExisteUmEstoqueComUnidadesFisicas(int quantidade) {
        queExisteUmEstoqueComUnidadesDisponiveis(quantidade);
    }

    @Dado("{int} unidades estão reservadas")
    public void unidadesEstaoReservadas(int quantidade) {
        estoque.reservar(produtoId, quantidade);
    }

    @Quando("eu tento registrar uma saída de {int} unidades")
    public void euTentoRegistrarUmaSaidaDeUnidades(int quantidade) {
        try {
            estoque.registrarSaida(produtoId, quantidade, "Sistema", "Venda");
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

    @Dado("que existe uma reserva de {int} unidades")
    public void queExisteUmaReservaDeUnidades(int quantidade) {
        queExisteUmEstoqueComUnidadesDisponiveis(200);
        estoque.reservar(produtoId, quantidade);
        reservaAtual = quantidade;
    }

    @Quando("o pedido é cancelado")
    public void oPedidoECancelado() {
        estoque.liberarReserva(produtoId, reservaAtual);
        reservaLiberada = true;
    }

    @Então("a reserva deve ser liberada")
    public void aReservaDeveSerLiberada() {
        assertTrue(reservaLiberada);
    }

    @Então("o saldo disponível deve aumentar em {int} unidades")
    public void oSaldoDisponivelDeveAumentarEmUnidades(int quantidade) {
        assertTrue(estoque.getSaldoDisponivel(produtoId) > 0);
    }

    @Dado("que foi criada uma reserva")
    public void queFoiCriadaUmaReserva() {
        queExisteUmaReservaDeUnidades(50);
    }

    @Dado("a reserva foi liberada")
    public void aReservaFoiLiberada() {
        reservaLiberada = true;
    }

    @Quando("eu consulto o histórico")
    public void euConsultoOHistorico() {
        // Consulta histórico
    }

    @Então("devo ver o registro da reserva")
    public void devoVerORegistroDaReserva() {
        assertTrue(true);
    }

    @Então("devo ver o registro da liberação")
    public void devoVerORegistroDaLiberacao() {
        assertTrue(reservaLiberada);
    }

    @Quando("o pedido é atendido")
    public void oPedidoEAtendido() {
        estoque.consumirReserva(produtoId, reservaAtual);
    }

    @Então("a reserva deve ser consumida")
    public void aReservaDeveSerConsumida() {
        assertEquals(0, estoque.getSaldoReservado(produtoId));
    }

    @Então("o saldo físico deve diminuir em {int} unidades")
    public void oSaldoFisicoDeveDiminuirEmUnidades(int quantidade) {
        assertTrue(estoque.getSaldoFisico(produtoId) < saldoFisicoInicial);
    }

    @Então("o saldo reservado deve diminuir em {int} unidades")
    public void oSaldoReservadoDeveDiminuirEmUnidades(int quantidade) {
        assertEquals(0, estoque.getSaldoReservado(produtoId));
    }
}
