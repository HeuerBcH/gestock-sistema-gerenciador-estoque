package dev.gestock.sge.aplicacao.reserva;

import static org.apache.commons.lang3.Validate.*;
import java.util.List;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.dominio.pedido.Pedido;
import dev.gestock.sge.dominio.pedido.Pedido.PedidoCriadoEvento;
import dev.gestock.sge.dominio.pedido.Pedido.PedidoCanceladoEvento;
import dev.gestock.sge.dominio.pedido.Pedido.PedidoRecebidoEvento;
import dev.gestock.sge.dominio.reserva.ReservaServico;
import dev.gestock.sge.dominio.reserva.TipoLiberacao;

public class ReservaServicoAplicacao {
	private final ReservaRepositorioAplicacao repositorioAplicacao;
	private final ReservaServico reservaServico;

	public ReservaServicoAplicacao(ReservaRepositorioAplicacao repositorioAplicacao, ReservaServico reservaServico,
			EventoBarramento barramento) {
		notNull(repositorioAplicacao, "O repositório de aplicação não pode ser nulo");
		notNull(reservaServico, "O serviço de reserva não pode ser nulo");
		notNull(barramento, "O barramento de eventos não pode ser nulo");

		this.repositorioAplicacao = repositorioAplicacao;
		this.reservaServico = reservaServico;

		// Registrar observadores de eventos
		barramento.adicionar(this::tratarPedidoCriado);
		barramento.adicionar(this::tratarPedidoCancelado);
		barramento.adicionar(this::tratarPedidoRecebido);
	}

	private void tratarPedidoCriado(PedidoCriadoEvento evento) {
		var pedido = evento.getPedido();
		reservaServico.criarReservas(pedido);
	}

	private void tratarPedidoCancelado(PedidoCanceladoEvento evento) {
		var pedido = evento.getPedido();
		reservaServico.liberarReservas(pedido.getId(), TipoLiberacao.CANCELADO);
	}

	private void tratarPedidoRecebido(PedidoRecebidoEvento evento) {
		var pedido = evento.getPedido();
		reservaServico.liberarReservas(pedido.getId(), TipoLiberacao.RECEBIDO);
	}

	public List<ReservaResumo> pesquisarResumos(String busca) {
		return repositorioAplicacao.pesquisarResumos(busca);
	}

	public ReservaTotais obterTotais() {
		return repositorioAplicacao.obterTotais();
	}
}

