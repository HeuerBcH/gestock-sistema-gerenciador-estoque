package dev.gestock.sge.apresentacao.principal.alerta;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.dominio.alerta.AlertaResumo;
import dev.gestock.sge.aplicacao.dominio.alerta.AlertaServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.dominio.principal.alerta.Alerta;
import dev.gestock.sge.dominio.principal.alerta.AlertaId;
import dev.gestock.sge.dominio.principal.alerta.AlertaRepositorio;
import dev.gestock.sge.dominio.principal.alerta.AlertaServico;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

@RestController
@RequestMapping("backend/alerta")
class AlertaControlador {
	private @Autowired AlertaServico alertaServico;
	private @Autowired AlertaServicoAplicacao alertaServicoAplicacao;
	private @Autowired AlertaRepositorio alertaRepositorio;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<AlertaResumo> pesquisar() {
		return alertaServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	Alerta buscarPorId(@PathVariable("id") Long id) {
		var alertaId = mapeador.map(id, AlertaId.class);
		return alertaRepositorio.obter(alertaId)
			.orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado"));
	}

	@RequestMapping(method = POST, path = "gerar")
	Alerta gerarAlerta(@RequestBody GerarAlertaDto dto) {
		var produtoId = mapeador.map(dto.produtoId, ProdutoId.class);
		var estoqueId = mapeador.map(dto.estoqueId, EstoqueId.class);
		var fornecedorSugeridoId = dto.fornecedorSugeridoId != null 
			? mapeador.map(dto.fornecedorSugeridoId, FornecedorId.class) 
			: null;
		
		return alertaServico.gerarAlerta(produtoId, estoqueId, fornecedorSugeridoId);
	}

	@RequestMapping(method = POST, path = "{id}/desativar")
	void desativarAlerta(@PathVariable("id") Long id) {
		var alertaId = mapeador.map(id, AlertaId.class);
		var alertaOpt = alertaRepositorio.obter(alertaId);
		
		if (alertaOpt.isPresent()) {
			alertaServico.desativarAlerta(alertaOpt.get());
		} else {
			throw new IllegalArgumentException("Alerta não encontrado");
		}
	}

	public static class GerarAlertaDto {
		public Long produtoId;
		public Long estoqueId;
		public Long fornecedorSugeridoId;
	}
}
