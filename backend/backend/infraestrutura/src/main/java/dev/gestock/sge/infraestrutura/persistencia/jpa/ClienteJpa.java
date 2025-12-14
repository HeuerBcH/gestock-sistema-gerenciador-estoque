package dev.gestock.sge.infraestrutura.persistencia.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CLIENTE")
class ClienteJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	String nome;
	String email;
	String documento;
	String senha;
}

