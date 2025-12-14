package dev.gestock.sge.dominio.autenticacao.cliente;

public interface ClienteRepositorio {
	Cliente salvar(Cliente cliente);

	Cliente obter(ClienteId id);

	Cliente obterPorEmail(Email email);

	Cliente obterPorDocumento(CpfCnpj documento);
}

