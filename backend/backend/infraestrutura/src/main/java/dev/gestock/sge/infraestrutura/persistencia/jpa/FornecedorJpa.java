package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FORNECEDOR")
class FornecedorJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	String nome;
	String cnpj;
	String contato;
	@Column(name = "LEAD_TIME")
	int leadTime;
	BigDecimal custo;
	String status;
}

