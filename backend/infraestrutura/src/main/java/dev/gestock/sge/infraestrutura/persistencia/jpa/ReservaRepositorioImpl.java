package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import dev.gestock.sge.aplicacao.reserva.ReservaRepositorioAplicacao;
import dev.gestock.sge.aplicacao.reserva.ReservaResumo;
import dev.gestock.sge.aplicacao.reserva.ReservaTotais;
import dev.gestock.sge.dominio.pedido.PedidoId;
import dev.gestock.sge.dominio.reserva.Reserva;
import dev.gestock.sge.dominio.reserva.ReservaId;
import dev.gestock.sge.dominio.reserva.ReservaRepositorio;
import dev.gestock.sge.infraestrutura.persistencia.jpa.JpaMapeador;

@Repository
class ReservaRepositorioImpl implements ReservaRepositorio, ReservaRepositorioAplicacao {
	@Autowired
	ReservaJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Autowired
	PedidoJpaRepository pedidoRepositorio;

	@Autowired
	ProdutoJpaRepository produtoRepositorio;

	@Override
	public void salvar(Reserva reserva) {
		var reservaJpa = mapeador.map(reserva, ReservaJpa.class);
		// Carregar pedido e produto do banco
		reservaJpa.pedido = pedidoRepositorio.findById(reserva.getPedidoId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
		reservaJpa.produto = produtoRepositorio.findById(reserva.getProdutoId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
		repositorio.save(reservaJpa);
	}

	@Override
	public Reserva obter(ReservaId id) {
		var reservaJpa = repositorio.findById(id.getId()).orElse(null);
		return mapeador.map(reservaJpa, Reserva.class);
	}

	@Override
	public List<Reserva> obterPorPedido(PedidoId pedidoId) {
		var reservasJpa = repositorio.findByPedidoId(pedidoId.getId());
		return reservasJpa.stream().map(r -> mapeador.map(r, Reserva.class)).collect(Collectors.toList());
	}

	@Override
	public List<ReservaResumo> pesquisarResumos(String busca) {
		var reservasJpa = repositorio.pesquisarResumosJpa(busca);
		var resumos = new ArrayList<ReservaResumo>();
		for (var reservaJpa : reservasJpa) {
			resumos.add(new ReservaResumo() {
				@Override
				public int getId() {
					return reservaJpa.id;
				}

				@Override
				public int getPedidoId() {
					return reservaJpa.pedido != null ? reservaJpa.pedido.id : 0;
				}

				@Override
				public String getPedidoCodigo() {
					return reservaJpa.pedido != null ? "PED" + String.format("%03d", reservaJpa.pedido.id) : "";
				}

				@Override
				public int getProdutoId() {
					return reservaJpa.produto != null ? reservaJpa.produto.id : 0;
				}

				@Override
				public String getProdutoNome() {
					return reservaJpa.produto != null ? reservaJpa.produto.nome : "";
				}

				@Override
				public int getQuantidade() {
					return reservaJpa.quantidade;
				}

				@Override
				public LocalDateTime getDataHoraReserva() {
					return reservaJpa.dataHoraReserva;
				}

				@Override
				public String getStatus() {
					return reservaJpa.status;
				}

				@Override
				public String getTipoLiberacao() {
					return reservaJpa.tipoLiberacao;
				}

				@Override
				public LocalDateTime getDataHoraLiberacao() {
					return reservaJpa.dataHoraLiberacao;
				}
			});
		}
		return resumos;
	}

	@Override
	public ReservaTotais obterTotais() {
		var totalReservas = repositorio.contarTotal();
		var reservasAtivas = repositorio.contarAtivas();
		var reservasLiberadas = repositorio.contarLiberadas();
		var quantidadeReservadaAtiva = repositorio.somarQuantidadeAtiva();

		return new ReservaTotais() {
			@Override
			public int getTotalReservas() {
				return (int) totalReservas;
			}

			@Override
			public int getReservasAtivas() {
				return (int) reservasAtivas;
			}

			@Override
			public int getReservasLiberadas() {
				return (int) reservasLiberadas;
			}

			@Override
			public int getQuantidadeReservadaAtiva() {
				return (int) quantidadeReservadaAtiva;
			}
		};
	}
}

