package dev.gestock.sge.apresentacao.produto;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.produto.ProdutoResumo;
import dev.gestock.sge.aplicacao.produto.ProdutoServicoAplicacao;
import dev.gestock.sge.dominio.produto.*;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.Status;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.produto.ProdutoFormulario.ProdutoDto;

@RestController
@RequestMapping("backend/produto")
class ProdutoControlador {

	private @Autowired dev.gestock.sge.dominio.produto.ProdutoServico produtoServico;
	private @Autowired ProdutoServicoAplicacao produtoServicoAplicacao;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<ProdutoResumo> pesquisar(
			@RequestParam(required = false) String termo,
			@RequestParam(required = false) String status) {
		
		if (termo != null && !termo.isBlank()) {
			return produtoServicoAplicacao.pesquisarPorNomeOuCodigo(termo);
		}
		
		if (status != null && !status.isBlank()) {
			return produtoServicoAplicacao.pesquisarPorStatus(status);
		}
		
		return produtoServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	ProdutoResumo obter(@PathVariable("id") int id) {
		return produtoServicoAplicacao.obterResumo(id);
	}

	@RequestMapping(method = POST, path = "salvar")
	ProdutoResumo salvar(@RequestBody ProdutoDto dto) {
		var produtoId = dto.id != null ? new ProdutoId(dto.id) : new ProdutoId(0);
		var codigo = mapeador.map(dto.codigo, Codigo.class);
		var peso = mapeador.map(dto.peso, Peso.class);
		var perecivel = dto.perecivel != null ? Perecivel.valueOf(dto.perecivel.toUpperCase()) : Perecivel.NAO;
		var status = dto.status != null ? Status.valueOf(dto.status.toUpperCase()) : Status.ATIVO;
		
		List<FornecedorId> fornecedores = mapeador.map(
			dto.fornecedores,
			new org.modelmapper.TypeToken<List<FornecedorId>>() {}.getType()
		);
		
		var produto = new Produto(produtoId, codigo, dto.nome, peso, perecivel, status, fornecedores);
		var produtoSalvo = produtoServico.salvar(produto);
		
		return produtoServicoAplicacao.obterResumo(produtoSalvo.getId().getId());
	}

	@RequestMapping(method = DELETE, path = "{id}")
	void remover(@PathVariable("id") int id) {
		var produtoId = new ProdutoId(id);
		produtoServico.remover(produtoId);
	}

	@RequestMapping(method = POST, path = "{id}/ativar")
	void ativar(@PathVariable("id") int id) {
		var produtoId = new ProdutoId(id);
		produtoServico.ativar(produtoId);
	}

	@RequestMapping(method = POST, path = "{id}/inativar")
	void inativar(@PathVariable("id") int id) {
		var produtoId = new ProdutoId(id);
		produtoServico.inativar(produtoId);
	}
}

