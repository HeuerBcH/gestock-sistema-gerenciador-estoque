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
@Table(name = "TRANSFERENCIA")
class TransferenciaJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@ManyToOne
	@JoinColumn(name = "PRODUTO_ID")
	ProdutoJpa produto;

	int quantidade;

	@ManyToOne
	@JoinColumn(name = "ESTOQUE_ORIGEM_ID")
	EstoqueJpa estoqueOrigem;

	@ManyToOne
	@JoinColumn(name = "ESTOQUE_DESTINO_ID")
	EstoqueJpa estoqueDestino;

	@Column(name = "DATA_HORA_TRANSFERENCIA")
	LocalDateTime dataHoraTransferencia;

	String responsavel;
	String motivo;

	@ManyToOne
	@JoinColumn(name = "MOVIMENTACAO_SAIDA_ID")
	MovimentacaoJpa movimentacaoSaida;

	@ManyToOne
	@JoinColumn(name = "MOVIMENTACAO_ENTRADA_ID")
	MovimentacaoJpa movimentacaoEntrada;
}

