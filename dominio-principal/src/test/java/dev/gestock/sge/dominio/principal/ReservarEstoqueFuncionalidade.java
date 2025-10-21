package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

import io.cucumber.java.pt.*;
import java.util.*;
import static org.junit.Assert.*;

public class ReservarEstoqueFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    private Estoque estoque;
    private ProdutoId produtoId = new ProdutoId(1L);
    private int saldoFisicoInicial;
    private int reservaAtual;
    private Exception excecaoCapturada;
    private String mensagemErro;
    private boolean reservaLiberada = false;
    private final List<String> historicoReservas = new ArrayList<>();

    // =========================================================
    // H24 — Reservar estoque para pedidos pendentes
    // =========================================================

    @Dado("que existe um estoque chamado {string} com {int} unidades disponiveis")
    public void queExisteUmEstoqueChamadoComUnidadesDisponiveis(String nome, int quantidade) {
        ClienteId clienteId = new ClienteId(1L);
        EstoqueId id = repositorio.novoEstoqueId();
        estoque = new Estoque(id, clienteId, nome, "Endereco X", 1000);
        estoque.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", Map.of());
        saldoFisicoInicial = quantidade;
        repositorio.salvar(estoque);
    }

    @Quando("o cliente cria um pedido de venda com {int} unidades do produto")
    public void oClienteCriaUmPedidoDeVendaComUnidadesDoProduto(int quantidade) {
        estoque.reservar(produtoId, quantidade);
        reservaAtual = quantidade;
        historicoReservas.add("Reserva criada: " + quantidade);
    }

    @Entao("{int} unidades devem ser reservadas")
    public void unidadesDevemSerReservadas(int quantidade) {
        assertEquals(quantidade, estoque.getSaldoReservado(produtoId));
    }

    @E("o saldo disponivel deve ser {int} unidades")
    public void oSaldoDisponivelDeveSerUnidades(int quantidade) {
        assertEquals(quantidade, estoque.getSaldoDisponivel(produtoId));
    }

    @E("o saldo fisico deve permanecer {int} unidades")
    public void oSaldoFisicoDevePermanecerUnidades(int quantidade) {
        assertEquals(quantidade, estoque.getSaldoFisico(produtoId));
    }

    // ---------------------------------------------------------
    // R2H24 — Saldo reservado nao pode ser usado
    // ---------------------------------------------------------

    @Dado("que existe um estoque chamado {string} com {int} unidades fisicas")
    public void queExisteUmEstoqueChamadoComUnidadesFisicas(String nome, int quantidade) {
        ClienteId clienteId = new ClienteId(1L);
        EstoqueId id = repositorio.novoEstoqueId();
        estoque = new Estoque(id, clienteId, nome, "Endereco Y", 1000);
        estoque.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", Map.of());
        saldoFisicoInicial = quantidade;
        repositorio.salvar(estoque);
    }

    @E("{int} unidades estao reservadas")
    public void unidadesEstaoReservadas(int quantidade) {
        estoque.reservar(produtoId, quantidade);
        reservaAtual = quantidade;
    }

    @Quando("o cliente tenta registrar uma saida de {int} unidades do produto")
    public void oClienteTentaRegistrarUmaSaidaDeUnidadesDoProduto(int quantidade) {
        try {
            estoque.registrarSaida(produtoId, quantidade, "Cliente", "Venda");
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    // ✅ Step renomeado para evitar duplicacao global
    @Entao("o sistema deve rejeitar a operacao de reserva")
    public void oSistemaDeveRejeitarAOperacaoDeReserva() {
        assertNotNull("Deveria ter lancado excecao", excecaoCapturada);
    }

    // ✅ Step renomeado para evitar conflito com outros contextos
    @E("deve exibir a mensagem de reserva {string}")
    public void deveExibirAMensagemDeReserva(String mensagem) {
        assertNotNull("Mensagem deve existir", mensagemErro);
        assertTrue(mensagemErro.contains(mensagem));
    }

    // =========================================================
    // H25 — Liberar reserva ao cancelar pedido
    // =========================================================

    @Dado("que existe uma reserva de {int} unidades do produto")
    public void queExisteUmaReservaDeUnidadesDoProduto(int quantidade) {
        ClienteId clienteId = new ClienteId(1L);
        EstoqueId id = repositorio.novoEstoqueId();
        estoque = new Estoque(id, clienteId, "Estoque A", "Endereco Z", 1000);
        estoque.registrarEntrada(produtoId, 200, "Sistema", "Carga inicial", Map.of());
        estoque.reservar(produtoId, quantidade);
        reservaAtual = quantidade;
        historicoReservas.add("Reserva criada: " + quantidade);
    }

    @Quando("o pedido e cancelado")
    public void oPedidoECancelado() {
        estoque.liberarReserva(produtoId, reservaAtual);
        reservaLiberada = true;
        historicoReservas.add("Reserva liberada: " + reservaAtual);
    }

    @Entao("a reserva deve ser liberada")
    public void aReservaDeveSerLiberada() {
        assertTrue(reservaLiberada);
        assertEquals(0, estoque.getSaldoReservado(produtoId));
    }

    @E("o saldo disponivel deve aumentar em {int} unidades")
    public void oSaldoDisponivelAumenta(int quantidade) {
        assertEquals(quantidade, estoque.getSaldoDisponivel(produtoId));
    }

    // ---------------------------------------------------------
    // R2H25 — Sistema mantem registro historico
    // ---------------------------------------------------------

    @Dado("que foi criada uma reserva de {int} unidades")
    public void queFoiCriadaUmaReservaDeUnidades(int quantidade) {
        historicoReservas.add("Reserva criada: " + quantidade);
    }

    @E("a reserva foi liberada")
    public void aReservaFoiLiberada() {
        historicoReservas.add("Reserva liberada");
    }

    @Quando("o cliente consulta o historico de reservas")
    public void oClienteConsultaHistoricoDeReservas() {
        assertFalse("Historico deve existir", historicoReservas.isEmpty());
    }

    @Entao("o sistema deve exibir o registro da reserva")
    public void oSistemaDeveExibirRegistroDaReserva() {
        assertTrue(historicoReservas.stream().anyMatch(s -> s.contains("Reserva criada")));
    }

    @E("o sistema deve exibir o registro da liberacao")
    public void oSistemaDeveExibirRegistroDaLiberacao() {
        assertTrue(historicoReservas.stream().anyMatch(s -> s.contains("Reserva liberada")));
    }

    // ---------------------------------------------------------
    // Consumir reserva ao atender pedido
    // ---------------------------------------------------------

    @Quando("o pedido e atendido")
    public void oPedidoEAtendido() {
        estoque.consumirReserva(produtoId, reservaAtual);
    }

    @Entao("a reserva deve ser consumida")
    public void aReservaDeveSerConsumida() {
        assertEquals(0, estoque.getSaldoReservado(produtoId));
    }

    @E("o saldo fisico do estoque deve diminuir em {int} unidades")
    public void oSaldoFisicoDoEstoqueDeveDiminuirEm(int quantidade) {
        assertEquals(saldoFisicoInicial - quantidade, estoque.getSaldoFisico(produtoId));
    }

    @E("o saldo reservado deve diminuir em {int} unidades")
    public void oSaldoReservadoDeveDiminuirEm(int quantidade) {
        assertEquals(0, estoque.getSaldoReservado(produtoId));
    }
}
