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
@Table(name = "MOVIMENTACAO")
class MovimentacaoJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@Column(name = "DATA_HORA")
	LocalDateTime dataHora;

	@ManyToOne
	@JoinColumn(name = "PRODUTO_ID")
	ProdutoJpa produto;

	@ManyToOne
	@JoinColumn(name = "ESTOQUE_ID")
	EstoqueJpa estoque;

	int quantidade;
	String tipo;
	String motivo;
	String responsavel;
}

