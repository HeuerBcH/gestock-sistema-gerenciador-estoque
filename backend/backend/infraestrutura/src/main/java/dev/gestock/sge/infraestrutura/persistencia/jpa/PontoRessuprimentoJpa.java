package dev.gestock.sge.infraestrutura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PONTO_RESSUPRIMENTO")
class PontoRessuprimentoJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@ManyToOne
	@JoinColumn(name = "ESTOQUE_ID")
	EstoqueJpa estoque;

	@ManyToOne
	@JoinColumn(name = "PRODUTO_ID")
	ProdutoJpa produto;

	@Column(name = "ESTOQUE_SEGURANCA")
	int estoqueSeguranca;
}

