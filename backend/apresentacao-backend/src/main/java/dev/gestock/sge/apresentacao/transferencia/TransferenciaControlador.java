package dev.gestock.sge.apresentacao.transferencia;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaResumo;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaServicoAplicacao;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaTotais;
import dev.gestock.sge.apresentacao.transferencia.TransferenciaFormulario.TransferenciaDto;

@RestController
@RequestMapping("backend/transferencia")
class TransferenciaControlador {

	private @Autowired TransferenciaServicoAplicacao transferenciaServicoAplicacao;

	@RequestMapping(method = GET, path = "pesquisa")
	List<TransferenciaResumo> pesquisar(@RequestParam(required = false, defaultValue = "") String busca) {
		return transferenciaServicoAplicacao.pesquisarResumos(busca);
	}

	@RequestMapping(method = GET, path = "totais")
	TransferenciaTotais obterTotais() {
		return transferenciaServicoAplicacao.obterTotais();
	}

	@RequestMapping(method = POST, path = "registrar")
	Map<String, Object> registrar(@RequestBody TransferenciaDto dto) {
		var transferencia = transferenciaServicoAplicacao.registrar(
			dto.produtoId,
			dto.estoqueOrigemId,
			dto.estoqueDestinoId,
			dto.quantidade,
			dto.responsavel,
			dto.motivo
		);
		
		return Map.of(
			"sucesso", true,
			"mensagem", "Transferência realizada com sucesso!",
			"transferencia", transferencia != null ? transferencia : Map.of()
		);
	}
}

