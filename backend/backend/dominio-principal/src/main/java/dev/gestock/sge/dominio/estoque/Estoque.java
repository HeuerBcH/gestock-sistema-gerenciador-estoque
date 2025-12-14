package dev.gestock.sge.dominio.estoque;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.fornecedor.Status;

public class Estoque {
	private final EstoqueId id;
	private String nome;
	private Endereco endereco;
	private Capacidade capacidade;
	private Status status;

	public Estoque(EstoqueId id, String nome, Endereco endereco, Capacidade capacidade, Status status) {
		notNull(id, "O id não pode ser nulo");
		notNull(endereco, "O endereço não pode ser nulo");
		notNull(capacidade, "A capacidade não pode ser nula");
		notNull(status, "O status não pode ser nulo");

		this.id = id;
		setNome(nome);
		this.endereco = endereco;
		this.capacidade = capacidade;
		this.status = status;
	}

	public EstoqueId getId() {
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

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		notNull(endereco, "O endereço não pode ser nulo");
		this.endereco = endereco;
	}

	public Capacidade getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Capacidade capacidade) {
		notNull(capacidade, "A capacidade não pode ser nula");
		this.capacidade = capacidade;
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

