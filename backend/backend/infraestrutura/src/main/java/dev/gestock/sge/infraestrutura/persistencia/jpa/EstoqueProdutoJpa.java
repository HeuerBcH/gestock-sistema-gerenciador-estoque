package dev.gestock.sge.infraestrutura.persistencia.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ESTOQUE_PRODUTO")
@IdClass(EstoqueProdutoId.class)
class EstoqueProdutoJpa {
	@Id
	@ManyToOne
	@JoinColumn(name = "ESTOQUE_ID")
	EstoqueJpa estoque;

	@Id
	@ManyToOne
	@JoinColumn(name = "PRODUTO_ID")
	ProdutoJpa produto;

	int quantidade;
}

