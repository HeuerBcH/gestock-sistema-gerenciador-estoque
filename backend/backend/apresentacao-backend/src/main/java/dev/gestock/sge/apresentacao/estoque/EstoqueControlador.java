package dev.gestock.sge.apresentacao.estoque;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.estoque.EstoqueResumo;
import dev.gestock.sge.aplicacao.estoque.EstoqueServicoAplicacao;
import dev.gestock.sge.aplicacao.produto.ProdutoResumo;
import dev.gestock.sge.dominio.estoque.*;
import dev.gestock.sge.dominio.fornecedor.Status;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.estoque.EstoqueFormulario.EstoqueDto;

@RestController
@RequestMapping("backend/estoque")
class EstoqueControlador {

	private @Autowired dev.gestock.sge.dominio.estoque.EstoqueServico estoqueServico;
	private @Autowired EstoqueServicoAplicacao estoqueServicoAplicacao;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<EstoqueResumo> pesquisar(
			@RequestParam(required = false) String termo,
			@RequestParam(required = false) String status) {
		
		if (termo != null && !termo.isBlank()) {
			return estoqueServicoAplicacao.pesquisarPorNomeOuEndereco(termo);
		}
		
		if (status != null && !status.isBlank()) {
			return estoqueServicoAplicacao.pesquisarPorStatus(status);
		}
		
		return estoqueServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	EstoqueResumo obter(@PathVariable("id") int id) {
		return estoqueServicoAplicacao.obterResumo(id);
	}

	@RequestMapping(method = POST, path = "salvar")
	EstoqueResumo salvar(@RequestBody EstoqueDto dto) {
		var estoqueId = dto.id != null ? new EstoqueId(dto.id) : new EstoqueId(0);
		var endereco = mapeador.map(dto.endereco, Endereco.class);
		var capacidade = mapeador.map(dto.capacidade, Capacidade.class);
		var status = dto.status != null ? Status.valueOf(dto.status.toUpperCase()) : Status.ATIVO;
		
		var estoque = new Estoque(estoqueId, dto.nome, endereco, capacidade, status);
		var estoqueSalvo = estoqueServico.salvar(estoque);
		
		return estoqueServicoAplicacao.obterResumo(estoqueSalvo.getId().getId());
	}

	@RequestMapping(method = DELETE, path = "{id}")
	void remover(@PathVariable("id") int id) {
		var estoqueId = new EstoqueId(id);
		estoqueServico.remover(estoqueId);
	}

	@RequestMapping(method = POST, path = "{id}/ativar")
	void ativar(@PathVariable("id") int id) {
		var estoqueId = new EstoqueId(id);
		estoqueServico.ativar(estoqueId);
	}

	@RequestMapping(method = POST, path = "{id}/inativar")
	void inativar(@PathVariable("id") int id) {
		var estoqueId = new EstoqueId(id);
		estoqueServico.inativar(estoqueId);
	}

	@RequestMapping(method = GET, path = "{id}/produtos")
	List<ProdutoResumo> pesquisarProdutos(@PathVariable("id") int id) {
		return estoqueServicoAplicacao.pesquisarProdutos(id);
	}
}

