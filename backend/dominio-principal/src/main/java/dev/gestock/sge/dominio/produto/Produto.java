package dev.gestock.sge.dominio.produto;

import static org.apache.commons.lang3.Validate.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.Status;

public class Produto {
	private final ProdutoId id;
	private final Codigo codigo;
	private String nome;
	private Peso peso;
	private Perecivel perecivel;
	private Status status;
	private List<FornecedorId> fornecedores;

	public Produto(ProdutoId id, Codigo codigo, String nome, Peso peso, 
			Perecivel perecivel, Status status, List<FornecedorId> fornecedores) {
		notNull(id, "O id não pode ser nulo");
		notNull(codigo, "O código não pode ser nulo");
		notNull(peso, "O peso não pode ser nulo");
		notNull(perecivel, "O perecível não pode ser nulo");
		notNull(status, "O status não pode ser nulo");
		notNull(fornecedores, "A lista de fornecedores não pode ser nula");

		this.id = id;
		this.codigo = codigo;
		setNome(nome);
		this.peso = peso;
		this.perecivel = perecivel;
		this.status = status;
		setFornecedores(fornecedores);
	}

	public ProdutoId getId() {
		return id;
	}

	public Codigo getCodigo() {
		return codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		notNull(nome, "O nome não pode ser nulo");
		notBlank(nome, "O nome não pode estar em branco");
		this.nome = nome;
	}

	public Peso getPeso() {
		return peso;
	}

	public void setPeso(Peso peso) {
		notNull(peso, "O peso não pode ser nulo");
		this.peso = peso;
	}

	public Perecivel getPerecivel() {
		return perecivel;
	}

	public void setPerecivel(Perecivel perecivel) {
		notNull(perecivel, "O perecível não pode ser nulo");
		this.perecivel = perecivel;
	}

	public Status getStatus() {
		return status;
	}

	public Collection<FornecedorId> getFornecedores() {
		var copia = new ArrayList<FornecedorId>();
		copia.addAll(fornecedores);
		return copia;
	}

	public void setFornecedores(List<FornecedorId> fornecedores) {
		notNull(fornecedores, "A lista de fornecedores não pode ser nula");
		this.fornecedores = fornecedores;
	}

	public void ativar() {
		this.status = Status.ATIVO;
	}

	public void inativar() {
		this.status = Status.INATIVO;
	}
}

