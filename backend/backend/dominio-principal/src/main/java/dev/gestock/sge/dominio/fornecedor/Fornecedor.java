package dev.gestock.sge.dominio.fornecedor;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.autenticacao.cliente.Email;

public class Fornecedor {
	private final FornecedorId id;
	private String nome;
	private final Cnpj cnpj;
	private final Email contato;
	private LeadTime leadTime;
	private Custo custo;
	private Status status;

	public Fornecedor(FornecedorId id, String nome, Cnpj cnpj, Email contato, LeadTime leadTime, Custo custo, Status status) {
		notNull(id, "O id não pode ser nulo");
		notNull(cnpj, "O CNPJ não pode ser nulo");
		notNull(contato, "O contato não pode ser nulo");
		notNull(leadTime, "O lead time não pode ser nulo");
		notNull(custo, "O custo não pode ser nulo");
		notNull(status, "O status não pode ser nulo");

		this.id = id;
		setNome(nome);
		this.cnpj = cnpj;
		this.contato = contato;
		this.leadTime = leadTime;
		this.custo = custo;
		this.status = status;
	}

	public FornecedorId getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		notNull(nome, "O nome não pode ser nulo");
		notBlank(nome, "O nome não pode estar em branco");
		this.nome = nome;
	}

	public Cnpj getCnpj() {
		return cnpj;
	}

	public Email getContato() {
		return contato;
	}

	public LeadTime getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(LeadTime leadTime) {
		notNull(leadTime, "O lead time não pode ser nulo");
		this.leadTime = leadTime;
	}

	public Custo getCusto() {
		return custo;
	}

	public void setCusto(Custo custo) {
		notNull(custo, "O custo não pode ser nulo");
		this.custo = custo;
	}

	public Status getStatus() {
		return status;
	}

	public void ativar() {
		this.status = Status.ATIVO;
	}

	public void inativar() {
		this.status = Status.INATIVO;
	}
}

