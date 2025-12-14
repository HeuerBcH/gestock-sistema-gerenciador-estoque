package dev.gestock.sge.apresentacao.cotacao;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.cotacao.CotacaoPorProdutoResumo;
import dev.gestock.sge.aplicacao.cotacao.CotacaoResumo;
import dev.gestock.sge.aplicacao.cotacao.CotacaoServicoAplicacao;
import dev.gestock.sge.dominio.cotacao.*;
import dev.gestock.sge.dominio.fornecedor.Custo;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.LeadTime;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.cotacao.CotacaoFormulario.CotacaoDto;

@RestController
@RequestMapping("backend/cotacao")
class CotacaoControlador {

	private @Autowired dev.gestock.sge.dominio.cotacao.CotacaoServico cotacaoServico;
	private @Autowired CotacaoServicoAplicacao cotacaoServicoAplicacao;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<CotacaoPorProdutoResumo> pesquisar() {
		return cotacaoServicoAplicacao.pesquisarPorProduto();
	}

	@RequestMapping(method = GET, path = "produto/{produtoId}")
	List<CotacaoResumo> pesquisarPorProduto(@PathVariable("produtoId") int produtoId) {
		return cotacaoServicoAplicacao.pesquisarPorProduto(produtoId);
	}

	@RequestMapping(method = GET, path = "{id}")
	CotacaoResumo obter(@PathVariable("id") int id) {
		return cotacaoServicoAplicacao.obterResumo(id);
	}

	@RequestMapping(method = POST, path = "salvar")
	CotacaoResumo salvar(@RequestBody CotacaoDto dto) {
		var cotacaoId = dto.id != null ? new CotacaoId(dto.id) : new CotacaoId(0);
		var produtoId = new ProdutoId(dto.produtoId);
		var fornecedorId = new FornecedorId(dto.fornecedorId);
		var preco = new Custo(dto.preco);
		var leadTime = new LeadTime(dto.leadTime);
		var validade = dto.validade != null ? Validade.valueOf(dto.validade.toUpperCase()) : Validade.ATIVA;
		var statusAprovacao = dto.statusAprovacao != null 
			? StatusAprovacao.valueOf(dto.statusAprovacao.toUpperCase()) 
			: StatusAprovacao.PENDENTE;
		
		var cotacao = new Cotacao(cotacaoId, produtoId, fornecedorId, preco, leadTime, validade, statusAprovacao);
		var cotacaoSalva = cotacaoServico.salvar(cotacao);
		
		return cotacaoServicoAplicacao.obterResumo(cotacaoSalva.getId().getId());
	}

	@RequestMapping(method = DELETE, path = "{id}")
	void remover(@PathVariable("id") int id) {
		var cotacaoId = new CotacaoId(id);
		cotacaoServico.remover(cotacaoId);
	}

	@RequestMapping(method = POST, path = "{id}/aprovar")
	void aprovar(@PathVariable("id") int id) {
		var cotacaoId = new CotacaoId(id);
		cotacaoServico.aprovar(cotacaoId);
	}

	@RequestMapping(method = POST, path = "{id}/desaprovar")
	void desaprovar(@PathVariable("id") int id) {
		var cotacaoId = new CotacaoId(id);
		cotacaoServico.desaprovar(cotacaoId);
	}

	@RequestMapping(method = POST, path = "sincronizar")
	java.util.Map<String, Object> sincronizar() {
		int cotacoesCriadas = cotacaoServicoAplicacao.sincronizarCotacoes();
		return java.util.Map.of(
			"cotacoesCriadas", cotacoesCriadas,
			"mensagem", cotacoesCriadas > 0 
				? "Cotações sincronizadas com sucesso!" 
				: "Todas as cotações já estão sincronizadas."
		);
	}
}

