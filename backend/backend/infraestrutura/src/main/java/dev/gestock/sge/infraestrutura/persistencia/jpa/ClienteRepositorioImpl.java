package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.autenticacao.cliente.ClienteRepositorioAplicacao;
import dev.gestock.sge.aplicacao.autenticacao.cliente.ClienteResumo;
import dev.gestock.sge.dominio.autenticacao.cliente.Cliente;
import dev.gestock.sge.dominio.autenticacao.cliente.ClienteId;
import dev.gestock.sge.dominio.autenticacao.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.autenticacao.cliente.CpfCnpj;
import dev.gestock.sge.dominio.autenticacao.cliente.Email;

@Repository
class ClienteRepositorioImpl implements ClienteRepositorio, ClienteRepositorioAplicacao {
	@Autowired
	ClienteJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public Cliente salvar(Cliente cliente) {
		var clienteJpa = mapeador.map(cliente, ClienteJpa.class);
		clienteJpa = repositorio.save(clienteJpa);
		return mapeador.map(clienteJpa, Cliente.class);
	}

	@Transactional
	@Override
	public Cliente obter(ClienteId id) {
		var clienteJpa = repositorio.findById(id.getId()).orElse(null);
		return mapeador.map(clienteJpa, Cliente.class);
	}

	@Transactional
	@Override
	public Cliente obterPorEmail(Email email) {
		var clienteJpa = repositorio.findByEmail(email.getEndereco()).orElse(null);
		return mapeador.map(clienteJpa, Cliente.class);
	}

	@Transactional
	@Override
	public Cliente obterPorDocumento(CpfCnpj documento) {
		var clienteJpa = repositorio.findByDocumento(documento.getDocumento()).orElse(null);
		return mapeador.map(clienteJpa, Cliente.class);
	}

	@Override
	public List<ClienteResumo> pesquisarResumos() {
		var clientesJpa = repositorio.findAll();
		return clientesJpa.stream()
			.map(this::criarResumo)
			.toList();
	}

	@Override
	public ClienteResumo obterResumo(int id) {
		var clienteJpa = repositorio.findById(id).orElse(null);
		if (clienteJpa == null) {
			return null;
		}
		return criarResumo(clienteJpa);
	}

	private ClienteResumo criarResumo(ClienteJpa clienteJpa) {
		return new ClienteResumo() {
			@Override
			public int getId() {
				return clienteJpa.id;
			}

			@Override
			public String getNome() {
				return clienteJpa.nome;
			}

			@Override
			public String getEmail() {
				return clienteJpa.email;
			}
		};
	}
}

