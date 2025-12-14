package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PEDIDO")
class PedidoJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@ManyToOne
	@JoinColumn(name = "FORNECEDOR_ID")
	FornecedorJpa fornecedor;

	@ManyToOne
	@JoinColumn(name = "ESTOQUE_ID")
	EstoqueJpa estoque;

	@Column(name = "VALOR_TOTAL")
	BigDecimal valorTotal;
	@Column(name = "DATA_PEDIDO")
	LocalDate dataPedido;
	@Column(name = "DATA_PREVISTA")
	LocalDate dataPrevista;
	String status;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "PEDIDO_ITEM", joinColumns = @JoinColumn(name = "PEDIDO_ID"))
	@AttributeOverride(name = "produtoId", column = @Column(name = "PRODUTO_ID"))
	@AttributeOverride(name = "quantidade", column = @Column(name = "QUANTIDADE"))
	@AttributeOverride(name = "precoUnitario", column = @Column(name = "PRECO_UNITARIO"))
	List<ItemPedidoJpa> itens;
}

