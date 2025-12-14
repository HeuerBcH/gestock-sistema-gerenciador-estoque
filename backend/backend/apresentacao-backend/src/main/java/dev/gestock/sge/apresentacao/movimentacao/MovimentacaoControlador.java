package dev.gestock.sge.apresentacao.movimentacao;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.movimentacao.MovimentacaoResumo;
import dev.gestock.sge.aplicacao.movimentacao.MovimentacaoServicoAplicacao;
import dev.gestock.sge.aplicacao.movimentacao.MovimentacaoTotais;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.movimentacao.*;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.Quantidade;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.movimentacao.MovimentacaoFormulario.MovimentacaoDto;

@RestController
@RequestMapping("backend/movimentacao")
class MovimentacaoControlador {

	private @Autowired dev.gestock.sge.dominio.movimentacao.MovimentacaoServico movimentacaoServico;
	private @Autowired MovimentacaoServicoAplicacao movimentacaoServicoAplicacao;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<MovimentacaoResumo> pesquisar(
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
		
		if (tipo != null && !tipo.isBlank()) {
			return movimentacaoServicoAplicacao.pesquisarPorTipo(tipo);
		}
		
		if (dataInicio != null && dataFim != null) {
			return movimentacaoServicoAplicacao.pesquisarPorPeriodo(dataInicio, dataFim);
		}
		
		return movimentacaoServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "totais")
	MovimentacaoTotais obterTotais(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
		
		if (dataInicio != null && dataFim != null) {
			return movimentacaoServicoAplicacao.obterTotaisPorPeriodo(dataInicio, dataFim);
		}
		
		return movimentacaoServicoAplicacao.obterTotais();
	}

	@RequestMapping(method = GET, path = "{id}")
	MovimentacaoResumo obter(@PathVariable("id") int id) {
		return movimentacaoServicoAplicacao.obterResumo(id);
	}

	@RequestMapping(method = POST, path = "registrar")
	MovimentacaoResumo registrar(@RequestBody MovimentacaoDto dto) {
		var movimentacaoId = dto.id != null ? new MovimentacaoId(dto.id) : new MovimentacaoId(0);
		var dataHora = dto.dataHora != null ? dto.dataHora : LocalDateTime.now();
		var produtoId = new ProdutoId(dto.produtoId);
		var estoqueId = new EstoqueId(dto.estoqueId);
		var quantidade = new Quantidade(dto.quantidade);
		var tipo = dto.tipo != null ? TipoMovimentacao.valueOf(dto.tipo.toUpperCase()) : TipoMovimentacao.ENTRADA;
		var motivo = new Motivo(dto.motivo);
		var responsavel = new Responsavel(dto.responsavel);
		
		var movimentacao = new Movimentacao(movimentacaoId, dataHora, produtoId, estoqueId, quantidade, tipo, motivo, responsavel);
		var movimentacaoSalva = movimentacaoServico.registrar(movimentacao);
		
		return movimentacaoServicoAplicacao.obterResumo(movimentacaoSalva.getId().getId());
	}

	@RequestMapping(method = DELETE, path = "{id}")
	void remover(@PathVariable("id") int id) {
		var movimentacaoId = new MovimentacaoId(id);
		movimentacaoServico.remover(movimentacaoId);
	}
}

