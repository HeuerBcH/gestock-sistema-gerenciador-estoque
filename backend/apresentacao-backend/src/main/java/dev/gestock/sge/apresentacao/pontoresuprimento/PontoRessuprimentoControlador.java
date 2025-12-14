package dev.gestock.sge.apresentacao.pontoresuprimento;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoResumo;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoServicoAplicacao;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoTotais;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.pontoresuprimento.EstoqueSeguranca;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimento;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimentoId;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.apresentacao.pontoresuprimento.PontoRessuprimentoFormulario.PontoRessuprimentoDto;

@RestController
@RequestMapping("backend/ponto-ressuprimento")
class PontoRessuprimentoControlador {

	private @Autowired dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimentoServico pontoRessuprimentoServico;
	private @Autowired PontoRessuprimentoServicoAplicacao pontoRessuprimentoServicoAplicacao;

	@RequestMapping(method = GET, path = "pesquisa")
	List<PontoRessuprimentoResumo> pesquisar(
			@RequestParam(required = false) String termo,
			@RequestParam(required = false) String status) {
		
		if (status != null && !status.isBlank()) {
			return pontoRessuprimentoServicoAplicacao.pesquisarPorStatus(status);
		}
		
		if (termo != null && !termo.isBlank()) {
			return pontoRessuprimentoServicoAplicacao.pesquisarPorProdutoOuEstoque(termo);
		}
		
		return pontoRessuprimentoServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "totais")
	PontoRessuprimentoTotais obterTotais() {
		return pontoRessuprimentoServicoAplicacao.obterTotais();
	}

	@RequestMapping(method = GET, path = "{id}")
	PontoRessuprimentoResumo obter(@PathVariable("id") int id) {
		return pontoRessuprimentoServicoAplicacao.obterResumo(id);
	}

	@RequestMapping(method = POST, path = "registrar")
	PontoRessuprimentoResumo registrar(@RequestBody PontoRessuprimentoDto dto) {
		var pontoId = dto.id != null ? new PontoRessuprimentoId(dto.id) : new PontoRessuprimentoId(0);
		var estoqueId = new EstoqueId(dto.estoqueId);
		var produtoId = new ProdutoId(dto.produtoId);
		var estoqueSeguranca = new EstoqueSeguranca(dto.estoqueSeguranca);
		
		var ponto = new PontoRessuprimento(pontoId, estoqueId, produtoId, estoqueSeguranca);
		var pontoSalvo = pontoRessuprimentoServico.registrar(ponto);
		
		return pontoRessuprimentoServicoAplicacao.obterResumo(pontoSalvo.getId().getId());
	}

	@RequestMapping(method = PUT, path = "{id}/estoque-seguranca")
	PontoRessuprimentoResumo atualizarEstoqueSeguranca(@PathVariable("id") int id, @RequestBody Integer estoqueSeguranca) {
		var pontoId = new PontoRessuprimentoId(id);
		var estoqueSeg = new EstoqueSeguranca(estoqueSeguranca);
		pontoRessuprimentoServico.atualizarEstoqueSeguranca(pontoId, estoqueSeg);
		
		return pontoRessuprimentoServicoAplicacao.obterResumo(id);
	}

	@RequestMapping(method = DELETE, path = "{id}")
	void remover(@PathVariable("id") int id) {
		var pontoId = new PontoRessuprimentoId(id);
		pontoRessuprimentoServico.remover(pontoId);
	}

	@RequestMapping(method = POST, path = "sincronizar")
	java.util.Map<String, Object> sincronizar() {
		int registrosCriados = pontoRessuprimentoServicoAplicacao.sincronizarPontosRessuprimento();
		return java.util.Map.of(
			"registrosCriados", registrosCriados,
			"mensagem", registrosCriados > 0 
				? "Pontos de ressuprimento sincronizados com sucesso!"
				: "Nenhum novo ponto de ressuprimento para criar. Verifique se existem produtos nos estoques."
		);
	}
}

