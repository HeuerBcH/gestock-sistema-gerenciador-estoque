package dev.gestock.sge.dominio.autenticacao.cliente;

import static org.apache.commons.lang3.Validate.*;

public class ClienteServico {
	private final ClienteRepositorio repositorio;

	public ClienteServico(ClienteRepositorio repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void registrar(Cliente cliente) {
		notNull(cliente, "O cliente não pode ser nulo");

		// Verificar se já existe cliente com o mesmo e-mail
		var clienteExistente = repositorio.obterPorEmail(cliente.getEmail());
		if (clienteExistente != null) {
			throw new IllegalArgumentException("Já existe um cliente cadastrado com este e-mail");
		}

		// Verificar se já existe cliente com o mesmo documento
		clienteExistente = repositorio.obterPorDocumento(cliente.getDocumento());
		if (clienteExistente != null) {
			throw new IllegalArgumentException("Já existe um cliente cadastrado com este documento");
		}

		repositorio.salvar(cliente);
	}

	public Cliente registrarERetornar(Cliente cliente) {
		notNull(cliente, "O cliente não pode ser nulo");

		// Verificar se já existe cliente com o mesmo e-mail
		var clienteExistente = repositorio.obterPorEmail(cliente.getEmail());
		if (clienteExistente != null) {
			throw new IllegalArgumentException("Já existe um cliente cadastrado com este e-mail");
		}

		// Verificar se já existe cliente com o mesmo documento
		clienteExistente = repositorio.obterPorDocumento(cliente.getDocumento());
		if (clienteExistente != null) {
			throw new IllegalArgumentException("Já existe um cliente cadastrado com este documento");
		}

		return repositorio.salvar(cliente);
	}

	public Cliente autenticar(Email email, Senha senha) {
		notNull(email, "O e-mail não pode ser nulo");
		notNull(senha, "A senha não pode ser nula");

		var cliente = repositorio.obterPorEmail(email);
		if (cliente == null) {
			throw new IllegalArgumentException("E-mail ou senha inválidos");
		}

		if (!cliente.validarSenha(senha.getValor())) {
			throw new IllegalArgumentException("E-mail ou senha inválidos");
		}

		return cliente;
	}

	public Cliente obterPorEmail(Email email) {
		notNull(email, "O e-mail não pode ser nulo");
		return repositorio.obterPorEmail(email);
	}
}

