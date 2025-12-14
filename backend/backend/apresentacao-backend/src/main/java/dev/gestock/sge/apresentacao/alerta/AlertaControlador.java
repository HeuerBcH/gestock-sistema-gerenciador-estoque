package dev.gestock.sge.apresentacao.alerta;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.alerta.AlertaResumo;
import dev.gestock.sge.aplicacao.alerta.AlertaServicoAplicacao;
import dev.gestock.sge.aplicacao.alerta.AlertaTotais;

@RestController
@RequestMapping("backend/alerta")
class AlertaControlador {

	private @Autowired AlertaServicoAplicacao alertaServicoAplicacao;

	@RequestMapping(method = GET, path = "pesquisa")
	List<AlertaResumo> pesquisar(@RequestParam(required = false) String nivel) {
		if (nivel != null && !nivel.isBlank()) {
			return alertaServicoAplicacao.pesquisarPorNivel(nivel);
		}
		
		return alertaServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "totais")
	AlertaTotais obterTotais() {
		return alertaServicoAplicacao.obterTotais();
	}
}

