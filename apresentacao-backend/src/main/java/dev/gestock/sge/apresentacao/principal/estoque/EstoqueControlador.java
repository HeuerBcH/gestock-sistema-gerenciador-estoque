package dev.gestock.sge.apresentacao.principal.estoque;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueResumo;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.principal.estoque.EstoqueFormulario.EstoqueDto;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.estoque.EstoqueServico;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

@RestController
@RequestMapping("backend/estoque")
class EstoqueControlador {
	private @Autowired EstoqueServico estoqueServico;
	private @Autowired EstoqueServicoAplicacao estoqueServicoAplicacao;
	private @Autowired EstoqueRepositorio estoqueRepositorio;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<EstoqueResumo> pesquisar() {
		return estoqueServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "cliente/{clienteId}")
	List<Estoque> pesquisarPorCliente(@PathVariable("clienteId") Long clienteId) {
		var id = mapeador.map(clienteId, ClienteId.class);
		return estoqueServico.pesquisarPorCliente(id);
	}

	@RequestMapping(method = POST, path = "salvar")
	void salvar(@RequestBody EstoqueDto dto) {
		dto.id = null; // ID será gerado pela persistência
		var estoque = mapeador.map(dto, Estoque.class);
		estoqueServico.cadastrar(estoque);
	}

	@RequestMapping(method = GET, path = "{id}")
	Estoque buscarPorId(@PathVariable("id") Long id) {
		var estoqueId = mapeador.map(id, EstoqueId.class);
		return estoqueRepositorio.buscarPorId(estoqueId)
			.orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));
	}

	@RequestMapping(method = POST, path = "{id}/atualizar")
	void atualizar(@PathVariable("id") Long id, @RequestBody EstoqueDto dto) {
		var estoqueId = mapeador.map(id, EstoqueId.class);
		var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
		
		if (estoqueOpt.isPresent()) {
			var estoque = estoqueOpt.get();
			estoque.renomear(dto.nome);
			estoque.alterarCapacidade(dto.capacidade);
			estoqueServico.atualizar(estoque);
		} else {
			throw new IllegalArgumentException("Estoque não encontrado");
		}
	}

	@RequestMapping(method = POST, path = "{id}/inativar")
	void inativar(@PathVariable("id") Long id) {
		var estoqueId = mapeador.map(id, EstoqueId.class);
		var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
		
		if (estoqueOpt.isPresent()) {
			estoqueServico.inativar(estoqueOpt.get());
		} else {
			throw new IllegalArgumentException("Estoque não encontrado");
		}
	}

	@RequestMapping(method = POST, path = "transferir")
	void transferir(@RequestBody TransferenciaDto dto) {
		var origemId = mapeador.map(dto.origemId, EstoqueId.class);
		var destinoId = mapeador.map(dto.destinoId, EstoqueId.class);
		var produtoId = mapeador.map(dto.produtoId, ProdutoId.class);
		
		var origemOpt = estoqueRepositorio.buscarPorId(origemId);
		var destinoOpt = estoqueRepositorio.buscarPorId(destinoId);
		
		if (origemOpt.isEmpty() || destinoOpt.isEmpty()) {
			throw new IllegalArgumentException("Estoque de origem ou destino não encontrado");
		}
		
		estoqueServico.transferir(origemOpt.get(), destinoOpt.get(), produtoId, 
			dto.quantidade, dto.responsavel, dto.motivo);
	}

	public static class TransferenciaDto {
		public Long origemId;
		public Long destinoId;
		public Long produtoId;
		public int quantidade;
		public String responsavel;
		public String motivo;
	}
}
