package dev.gestock.sge.apresentacao.principal.produto;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.dominio.produto.ProdutoResumo;
import dev.gestock.sge.aplicacao.dominio.produto.ProdutoServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.principal.produto.ProdutoForm.ProdutoDto;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoServico;

@RestController
@RequestMapping("backend/produto")
class ProdutoControlador {
	private @Autowired ProdutoServico produtoServico;
	private @Autowired ProdutoServicoAplicacao produtoServicoAplicacao;
	private @Autowired ProdutoRepositorio produtoRepositorio;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<ProdutoResumo> pesquisar() {
		return produtoServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	Produto buscarPorId(@PathVariable("id") Long id) {
		var produtoId = mapeador.map(id, ProdutoId.class);
		return produtoRepositorio.buscarPorId(produtoId)
			.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
	}

	@RequestMapping(method = POST, path = "salvar")
	void salvar(@RequestBody ProdutoDto dto) {
		dto.id = null; // ID será gerado pela persistência
		var produto = mapeador.map(dto, Produto.class);
		produtoServico.cadastrar(produto);
	}

	@RequestMapping(method = POST, path = "{id}/atualizar")
	void atualizar(@PathVariable("id") Long id, @RequestBody ProdutoDto dto) {
		var produtoId = mapeador.map(id, ProdutoId.class);
		var produtoOpt = produtoRepositorio.buscarPorId(produtoId);
		
		if (produtoOpt.isPresent()) {
			var produto = produtoOpt.get();
			produto.atualizar(dto.nome, dto.unidadePeso, dto.peso);
			produtoServico.atualizar(produto);
		} else {
			throw new IllegalArgumentException("Produto não encontrado");
		}
	}

	@RequestMapping(method = POST, path = "{id}/inativar")
	void inativar(@PathVariable("id") Long id) {
		var produtoId = mapeador.map(id, ProdutoId.class);
		var produtoOpt = produtoRepositorio.buscarPorId(produtoId);
		
		if (produtoOpt.isPresent()) {
			produtoServico.inativar(produtoOpt.get());
		} else {
			throw new IllegalArgumentException("Produto não encontrado");
		}
	}
}
