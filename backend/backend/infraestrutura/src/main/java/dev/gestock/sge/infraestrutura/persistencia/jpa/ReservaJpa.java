package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RESERVA")
class ReservaJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@ManyToOne
	@JoinColumn(name = "PEDIDO_ID")
	PedidoJpa pedido;

	@ManyToOne
	@JoinColumn(name = "PRODUTO_ID")
	ProdutoJpa produto;

	int quantidade;
	@Column(name = "DATA_HORA_RESERVA")
	LocalDateTime dataHoraReserva;
	String status;
	@Column(name = "TIPO_LIBERACAO")
	String tipoLiberacao;
	@Column(name = "DATA_HORA_LIBERACAO")
	LocalDateTime dataHoraLiberacao;
}

