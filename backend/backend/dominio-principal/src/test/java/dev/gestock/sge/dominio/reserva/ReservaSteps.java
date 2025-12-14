package dev.gestock.sge.dominio.reserva;

import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.fornecedor.Custo;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.pedido.DataPedido;
import dev.gestock.sge.dominio.pedido.ItemPedido;
import dev.gestock.sge.dominio.pedido.Pedido;
import dev.gestock.sge.dominio.pedido.PedidoId;
import dev.gestock.sge.dominio.pedido.StatusPedido;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.Quantidade;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReservaSteps {
	
	@Mock
	private ReservaRepositorio repositorio;
	
	private ReservaServico servico;
	private Pedido pedido;
	private List<Reserva> reservas;
	private Exception excecao;
	private int proximoId = 1;
	
	public ReservaSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new ReservaServico(repositorio);
	}
	
	@Dado("que existe um pedido cadastrado")
	public void que_existe_um_pedido_cadastrado() {
		var pedidoId = new PedidoId(proximoId++);
		var itens = new ArrayList<ItemPedido>();
		itens.add(new ItemPedido(new ProdutoId(1), new Quantidade(100), new Custo(10.50)));
		pedido = new Pedido(pedidoId, new FornecedorId(1), 
				new EstoqueId(1), itens, 
				new DataPedido(LocalDate.now()), StatusPedido.CRIADO);
	}
	
	@Dado("que existem reservas ativas para o pedido")
	public void que_existem_reservas_ativas_para_o_pedido() {
		var reservaId = new ReservaId(proximoId++);
		var reserva = new Reserva(reservaId, pedido.getId(), new ProdutoId(1), new Quantidade(100),
				new DataHoraReserva(java.time.LocalDateTime.now()), StatusReserva.ATIVA);
		reservas = List.of(reserva);
		when(repositorio.obterPorPedido(pedido.getId())).thenReturn(reservas);
		doNothing().when(repositorio).salvar(any(Reserva.class));
	}
	
	@Dado("que existe uma reserva com status {string}")
	public void que_existe_uma_reserva_com_status(String status) {
		var reservaId = new ReservaId(proximoId++);
		var reserva = new Reserva(reservaId, new PedidoId(1), new ProdutoId(1), new Quantidade(100),
				new DataHoraReserva(java.time.LocalDateTime.now()), StatusReserva.valueOf(status));
		reservas = List.of(reserva);
		when(repositorio.obterPorPedido(any(PedidoId.class))).thenReturn(reservas);
	}
	
	@Dado("que existe uma reserva com status LIBERADA")
	public void que_existe_uma_reserva_com_status_liberada() {
		var reservaId = new ReservaId(proximoId++);
		var reserva = new Reserva(reservaId, new PedidoId(1), new ProdutoId(1), new Quantidade(100),
				new DataHoraReserva(java.time.LocalDateTime.now()), StatusReserva.LIBERADA);
		reservas = List.of(reserva);
		when(repositorio.obterPorPedido(any(PedidoId.class))).thenReturn(reservas);
	}
	
	@Quando("eu crio reservas para o pedido")
	public void eu_crio_reservas_para_o_pedido() {
		servico.criarReservas(pedido);
	}
	
	@Quando("eu libero as reservas do pedido com tipo {string}")
	public void eu_libero_as_reservas_do_pedido_com_tipo(String tipo) {
		servico.liberarReservas(pedido.getId(), TipoLiberacao.valueOf(tipo));
		reservas = repositorio.obterPorPedido(pedido.getId());
	}
	
	@Quando("eu libero as reservas do pedido com tipo RECEBIDO")
	public void eu_libero_as_reservas_do_pedido_com_tipo_recebido() {
		servico.liberarReservas(pedido.getId(), TipoLiberacao.RECEBIDO);
		reservas = repositorio.obterPorPedido(pedido.getId());
	}
	
	@Quando("eu libero as reservas do pedido com tipo CANCELADO")
	public void eu_libero_as_reservas_do_pedido_com_tipo_cancelado() {
		servico.liberarReservas(pedido.getId(), TipoLiberacao.CANCELADO);
		reservas = repositorio.obterPorPedido(pedido.getId());
	}
	
	@Quando("eu tento liberar a reserva novamente")
	public void eu_tento_liberar_a_reserva_novamente() {
		try {
			reservas.get(0).liberar(TipoLiberacao.RECEBIDO);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Então("as reservas devem ser criadas com sucesso")
	public void as_reservas_devem_ser_criadas_com_sucesso() {
		verify(repositorio, atLeastOnce()).salvar(any(Reserva.class));
	}
	
	@Então("cada item do pedido deve ter uma reserva ATIVA")
	public void cada_item_do_pedido_deve_ter_uma_reserva_ativa() {
		verify(repositorio, times(pedido.getItens().size())).salvar(any(Reserva.class));
	}
	
	@Então("as reservas devem ter status {string}")
	public void as_reservas_devem_ter_status(String status) {
		assertThat(reservas.get(0).getStatus()).isEqualTo(StatusReserva.valueOf(status));
	}
	
	@Então("as reservas devem ter status LIBERADA")
	public void as_reservas_devem_ter_status_liberada() {
		assertThat(reservas.get(0).getStatus()).isEqualTo(StatusReserva.LIBERADA);
	}
	
	@Então("as reservas devem ter tipo de liberação {string}")
	public void as_reservas_devem_ter_tipo_de_liberacao(String tipo) {
		assertThat(reservas.get(0).getTipoLiberacao()).isEqualTo(TipoLiberacao.valueOf(tipo));
	}
	
	@Então("as reservas devem ter tipo de liberação RECEBIDO")
	public void as_reservas_devem_ter_tipo_de_liberacao_recebido() {
		assertThat(reservas.get(0).getTipoLiberacao()).isEqualTo(TipoLiberacao.RECEBIDO);
	}
	
	@Então("as reservas devem ter tipo de liberação CANCELADO")
	public void as_reservas_devem_ter_tipo_de_liberacao_cancelado() {
		assertThat(reservas.get(0).getTipoLiberacao()).isEqualTo(TipoLiberacao.CANCELADO);
	}
	
	@Então("deve ocorrer um erro informando que a reserva já está liberada")
	public void deve_ocorrer_um_erro_informando_que_a_reserva_ja_esta_liberada() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("liberada");
	}
}

