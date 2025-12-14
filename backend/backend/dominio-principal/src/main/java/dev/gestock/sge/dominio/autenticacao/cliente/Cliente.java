package dev.gestock.sge.dominio.autenticacao.cliente;

import static org.apache.commons.lang3.Validate.*;

public class Cliente {
	private final ClienteId id;
	private String nome;
	private final Email email;
	private final CpfCnpj documento;
	private Senha senha;

	public Cliente(ClienteId id, String nome, Email email, CpfCnpj documento, Senha senha) {
		notNull(id, "O id não pode ser nulo");
		notNull(email, "O e-mail não pode ser nulo");
		notNull(documento, "O documento não pode ser nulo");
		notNull(senha, "A senha não pode ser nula");

		this.id = id;
		setNome(nome);
		this.email = email;
		this.documento = documento;
		this.senha = senha;
	}

	public ClienteId getId() {
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

	public Email getEmail() {
		return email;
	}

	public CpfCnpj getDocumento() {
		return documento;
	}

	public Senha getSenha() {
		return senha;
	}

	public void alterarSenha(Senha novaSenha) {
		notNull(novaSenha, "A nova senha não pode ser nula");
		this.senha = novaSenha;
	}

	public boolean validarSenha(String senhaInformada) {
		return senha.validar(senhaInformada);
	}
}

