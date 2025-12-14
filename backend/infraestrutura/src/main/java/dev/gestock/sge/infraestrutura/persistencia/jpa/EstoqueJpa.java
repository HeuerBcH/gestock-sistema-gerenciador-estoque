package dev.gestock.sge.infraestrutura.persistencia.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ESTOQUE")
class EstoqueJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	String nome;
	String endereco;
	int capacidade;
	String status;
}

