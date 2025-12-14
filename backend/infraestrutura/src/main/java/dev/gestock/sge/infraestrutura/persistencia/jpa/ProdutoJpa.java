package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRODUTO")
class ProdutoJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	String codigo;
	String nome;
	int peso;
	String perecivel;
	String status;

	@ManyToMany
	@JoinTable(
		name = "PRODUTO_FORNECEDOR",
		joinColumns = @JoinColumn(name = "PRODUTO_ID"),
		inverseJoinColumns = @JoinColumn(name = "FORNECEDOR_ID")
	)
	List<FornecedorJpa> fornecedores;
}

