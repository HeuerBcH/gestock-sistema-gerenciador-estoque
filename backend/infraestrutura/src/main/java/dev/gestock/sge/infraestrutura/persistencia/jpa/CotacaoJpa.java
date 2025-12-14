package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "COTACAO")
class CotacaoJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@ManyToOne
	@JoinColumn(name = "PRODUTO_ID")
	ProdutoJpa produto;

	@ManyToOne
	@JoinColumn(name = "FORNECEDOR_ID")
	FornecedorJpa fornecedor;

	BigDecimal preco;
	@Column(name = "LEAD_TIME")
	int leadTime;
	String validade;
	@Column(name = "STATUS_APROVACAO")
	String statusAprovacao;
}

