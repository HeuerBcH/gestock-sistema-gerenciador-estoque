package dev.gestock.sge.apresentacao.principal.cliente;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.dominio.cliente.ClienteResumo;
import dev.gestock.sge.aplicacao.dominio.cliente.ClienteServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.principal.cliente.ClienteForm.ClienteDto;
import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.principal.cliente.ClienteServico;

@RestController
@RequestMapping("backend/cliente")
class ClienteControlador {
	private @Autowired ClienteServico clienteServico;
	private @Autowired ClienteServicoAplicacao clienteServicoAplicacao;
	private @Autowired ClienteRepositorio clienteRepositorio;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<ClienteResumo> pesquisar() {
		return clienteServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	Cliente buscarPorId(@PathVariable("id") Long id) {
		var clienteId = mapeador.map(id, ClienteId.class);
		return clienteRepositorio.buscarPorId(clienteId)
			.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
	}

	@RequestMapping(method = POST, path = "salvar")
	void salvar(@RequestBody ClienteDto dto) {
		dto.id = null; // ID será gerado pela persistência
		var cliente = mapeador.map(dto, Cliente.class);
		clienteServico.registrarCliente(cliente);
	}

	@RequestMapping(method = POST, path = "{id}/atualizar")
	void atualizar(@PathVariable("id") Long id, @RequestBody ClienteDto dto) {
		var clienteId = mapeador.map(id, ClienteId.class);
		var clienteOpt = clienteRepositorio.buscarPorId(clienteId);
		
		if (clienteOpt.isPresent()) {
			// Cliente não tem métodos públicos de atualização, então criamos um novo com os dados atualizados
			// e salvamos (o repositório vai fazer UPDATE se o ID já existir)
			var clienteAtualizado = new Cliente(clienteId, dto.nome, dto.documento, dto.email);
			clienteServico.registrarCliente(clienteAtualizado);
		} else {
			throw new IllegalArgumentException("Cliente não encontrado");
		}
	}
}
